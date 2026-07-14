# Analyse der Testbarkeit von FallingSystem.java

In der Datei `FallingSystem.java` wurden mehrere Abhängigkeiten identifiziert, die Unit-Tests erschweren ("gefährden"), da sie auf statische Methoden oder globalen Zustand zugreifen. Dies verhindert eine saubere Isolation der Klasse während des Tests.

## Identifizierte Problemstellen

### 1. core.Game (Statischer Zugriff auf den Spielzustand)
Das System greift direkt auf die statischen Methoden der Klasse `Game` zu. Um dies zu testen, müsste die gesamte Spielumgebung (inklusive Level und Tiles) initialisiert oder die Klasse `Game` statisch gemockt werden.
*   **Zeile 69:** `Tile tile = Game.tileAt(center).orElse(null);`
*   **Zeile 95:** `return Optional.of(Game.accessibleTilesInRange(playerCoords, 5).getFirst());`
*   **Zeile 97:** `return Game.randomTile(LevelElement.FLOOR);`

### 2. contrib.utils.EntityUtils (Statischer Zugriff auf Entitäten)
Die Methode sucht statisch nach der Position des Spielers im globalen Kontext.
*   **Zeile 87:** `Point playerCoords = EntityUtils.getPlayerPosition();`
*   **Problem:** Es ist schwer, isolierte Tests für spezifische Entitäten zu schreiben, ohne dass `EntityUtils` den globalen Zustand des Spiels kennen muss.

### 3. contrib.utils.components.Debugger (Statischer Seiteneffekt)
Hier wird eine statische Methode für eine Spielmechanik (Teleportation) genutzt.
*   **Zeile 90:** `.ifPresentOrElse(Debugger::TELEPORT, ...)`
*   **Problem:** Dies erzeugt einen Seiteneffekt, der schwer zu verifizieren ist, ohne den Debugger zu beobachten oder statisch zu mocken.

### 4. DEBUG_DONT_KILL (Globaler statischer Zustand)
Ein öffentliches, statisches Flag zur Steuerung der Logik.
*   **Zeile 47:** `public static boolean DEBUG_DONT_KILL = false;`
*   **Problem:** Wenn ein Test diesen Wert ändert und nicht explizit zurücksetzt, können nachfolgende Tests fehlschlagen (Inter-Test-Pollution).

### 5. DungeonLogger (Statischer Logger)
*   **Zeile 44 & 80:** Der Logger ist statisch an die Klasse gebunden. Dies erschwert das Unterdrücken oder Validieren von Log-Ausgaben in automatisierten Tests.

---

## Zusammenfassung
Die größten Hindernisse für "saubere" Unit-Tests sind die direkten, statischen Aufrufe von **Game** und **EntityUtils**, da diese eine laufende und initialisierte Spielumgebung voraussetzen.

**Lösung:** Refactoring durch das "Extract and Override"-Pattern oder Dependency Injection, um diese statischen Aufrufe in geschützte Methoden zu kapseln, die im Test einfach überschrieben werden können.

# Analyse der Testbarkeit von FogSystem.java

In der Datei `FogSystem.java` wurden mehrere Abhängigkeiten identifiziert, die Unit-Tests erschweren, da sie auf statische Methoden oder globalen Zustand zugreifen. Dies verhindert eine saubere Isolation der Klasse während des Tests.

## Identifizierte Problemstellen

### 1. core.Game (Statischer Zugriff auf Weltdaten)
Das System greift an vielen Stellen auf die statische API von `Game` zu, um Informationen über Tiles und Entitäten zu erhalten. Ein Test benötigt daher eine voll initialisierte `Game`-Instanz mit einem geladenen Level.
*   **Zeilen 176, 179, 269, 283:** `Game.tileAt(...)` – Finden von Tiles an Koordinaten.
*   **Zeile 251:** `Game.entityAtTile(...)` – Finden von Entitäten in verdunkelten Bereichen.

### 2. core.level.utils.LevelUtils (Statischer Zugriff auf Level-Operationen)
Das System nutzt Hilfsklassen für räumliche Abfragen, die intern oft auf das aktuell in `Game` geladene Level zugreifen.
*   **Zeile 274:** `LevelUtils.tilesInRange(playerPos, MAX_VIEW_DISTANCE)`
*   **Zeile 291:** `LevelUtils.tilesInRange(playerPos, currentViewDistance)`

### 3. contrib.utils.EntityUtils (Statischer Zugriff auf den Spieler)
Die Logik ist fest an die Existenz eines globalen Spielers gebunden.
*   **Zeile 271:** `Point playerPos = EntityUtils.getPlayerPosition();`
*   **Problem:** Ohne registrierten Spieler in der globalen Umgebung bricht die `execute`-Methode sofort ab, was isolierte Tests der Berechnungslogik erschwert.

### 4. Statischer Zustand (Sichtweite)
Die aktuelle Sichtweite ist als statische Variable implementiert.
*   **Zeile 42:** `private static int currentViewDistance = 7;`
*   **Problem:** Da dieses Feld `static` ist, bleiben Änderungen in einem Test für alle nachfolgenden Tests bestehen (Test-Pollution), falls sie nicht manuell zurückgesetzt werden.

### 5. Kapselung der Logik
Viele zentrale Berechnungen sind in `private` Methoden versteckt, die direkt die oben genannten statischen Ressourcen nutzen.
*   **Methoden:** `castLight`, `darkenTile`, `getTintColor`
*   **Problem:** Diese Methoden können nicht einzeln getestet werden, ohne die gesamte Kapselung zu durchbrechen oder die statischen Abhängigkeiten global zu mocken.

---

## Zusammenfassung
Die größten Hindernisse für "saubere" Unit-Tests sind die **statischen Welt-Abfragen** (`Game`, `LevelUtils`) und der **globale Spieler-Zugriff** (`EntityUtils`). Ein Testlauf erfordert derzeit ein fast vollständig hochgefahrenes Spiel-Framework.

**Empfohlene Lösung:** Refactoring der statischen Aufrufe in `protected` Wrapper-Methoden und Umwandlung von statischem Zustand in Instanzvariablen.

# Analyse der Testbarkeit von HealthSystem.java

Im Gegensatz zum `FallingSystem` und `FogSystem` ist das `HealthSystem` bereits wesentlich besser auf Unit-Tests vorbereitet, da es kaum auf statische Methoden oder globalen Zustand zugreift. Dennoch gibt es kleinere Punkte, die beachtet werden sollten.

## Identifizierte Problemstellen

### 1. Abhängigkeit von Enums (DamageType)
Die Schadensberechnung iteriert über alle Werte des `DamageType`-Enums.
*   **Zeile 102:** `return Stream.of(DamageType.values()).mapToInt(hsd.hc()::calculateDamageOf).sum();`
*   **Problem:** Wenn neue Schadensarten hinzugefügt werden, ändern sich die Ergebnisse der Berechnung global. Dies ist zwar kein hartes Hindernis für Tests, macht diese aber abhängiger von der Enum-Definition.

### 2. Kapselung der HSData (Record)
Das System nutzt eine interne `record`-Klasse `HSData`.
*   **Zeile 156:** `public record HSData(Entity e, HealthComponent hc, DrawComponent dc) {}`
*   **Vorteil:** Dies erleichtert das Mocking, da man einfach eine Instanz dieses Records mit Mock-Objekten für die Komponenten erstellen kann.

### 3. Zugriff auf Komponenten-Logik
Das System verlässt sich stark darauf, dass die Komponenten (`HealthComponent`, `DrawComponent`) korrekt funktionieren.
*   **Problem:** Ein Unit-Test für das `HealthSystem` ist oft gleichzeitig ein Integrationstest für die Komponenten, es sei denn, man mockt die Komponenten extrem feingranular (was bei Records/finalen Feldern manchmal schwierig ist).

---

## Zusammenfassung
Das `HealthSystem` ist ein positives Beispiel für ein testbares System im Projekt. Es nutzt das **"filteredEntityStream"**-Pattern von `core.System` korrekt und vermeidet den Zugriff auf die `Game`-Klasse oder `EntityUtils`.

**Lösung:** Hier ist aktuell kein kritisches Refactoring notwendig, um Unit-Tests zu ermöglichen. Tests können durch einfaches Mocking der `Entity` und ihrer Komponenten (`HealthComponent`, `DrawComponent`) realisiert werden.

# Analyse der Testbarkeit von HudSystem.java

Das `HudSystem` ist für die Verwaltung der Benutzeroberfläche (UI) verantwortlich und weist eine hohe Komplexität durch die Verknüpfung mit LibGDX (`Stage`, `Group`) sowie Netzwerk-Komponenten auf.

## Identifizierte Problemstellen

### 1. Abhängigkeit von core.Game (Globaler Zustand)
Das System greift an vielen Stellen auf die statische API von `Game` zu, um Informationen über Spieler, die Stage oder das Netzwerk zu erhalten.
*   **Zeilen 95, 126:** `Game.allPlayers()` – Abfrage aller Spieler-Entitäten.
*   **Zeile 104, 142:** `Game.findEntityById(...)` – Suche nach Entitäten.
*   **Zeile 146:** `Game.stage()` – Zugriff auf die UI-Stage von LibGDX.
*   **Zeile 151, 196:** `Game.isMultiplayerClient()` & `Game.network()` – Netzwerk-Abfragen.
*   **Zeile 238, 248:** `Game.systems()` – Stoppen/Starten anderer Systeme.

### 2. LibGDX Abhängigkeiten (Grafik-Kontext)
*   **Methoden:** `addListener`, `removeListener`, `addDialogToStage`
*   **Problem:** Das System arbeitet direkt mit `com.badlogic.gdx.scenes.scene2d.Stage` und `Group`. Für Unit-Tests bedeutet das, dass oft ein LibGDX-Headless-Backend oder ein Mocking der Stage notwendig ist, da viele LibGDX-Methoden intern auf native Grafik-Ressourcen zugreifen.

### 3. Statische Hilfsklassen und Tracker
*   **Zeilen 152, 185, 186:** `DialogTracker.instance()`, `NetworkUtils.getAllConnectedClientIds()`, etc.
*   **Problem:** Diese Singletons und statischen Utility-Klassen sind ohne Mocking-Frameworks (wie `mockStatic`) nicht isolierbar.

### 4. core.game.PreRunConfiguration
*   **Zeilen 237, 243:** `PreRunConfiguration.multiplayerEnabled()`
*   **Problem:** Statische Abfrage der Konfiguration, die das Verhalten von `pauseGame`/`unpauseGame` maßgeblich beeinflusst.

---

## Zusammenfassung
Das `HudSystem` ist aufgrund seiner Rolle als Bindeglied zwischen Spiellogik, UI-Framework (LibGDX) und Netzwerk eines der am schwersten zu testenden Systeme. Die Kopplung an statische Methoden von `Game` und Singletons wie `DialogTracker` macht reine Unit-Tests fast unmöglich.

**Empfohlene Lösung:**
1.  **Kapselung der Game-Aufrufe:** Statische Zugriffe auf `Game.stage()`, `Game.allPlayers()` etc. sollten in `protected` Methoden ausgelagert werden, damit sie in Tests überschrieben werden können.
2.  **Abstraktion der UI-Stage:** Die Interaktion mit der `Stage` könnte über ein Interface erfolgen, um den Test vom Grafik-Backend zu entkoppeln.
3.  **Injektion der Konfiguration:** Anstatt `PreRunConfiguration` statisch abzufragen, sollte dieser Wert (oder ein Provider dafür) dem System übergeben werden.

# Analyse der Testbarkeit von IdleSoundSystem.java

Das `IdleSoundSystem` ist ein relativ kleines System, das jedoch durch den Einsatz von Zufallszahlen und statischen Welt-Abfragen schwer deterministisch zu testen ist.

## Identifizierte Problemstellen

### 1. Abhängigkeit von core.Game (Globaler Zustand)
*   **Zeile 52:** `Game.player().flatMap(Game::positionOf).orElse(null);` – Abfrage der Spielerposition über statische Methoden.
*   **Zeile 69:** `Game.audio().playOnEntity(...)` – Zugriff auf das globale Audio-System.
*   **Problem:** Tests hängen von einer initialisierten `Game`-Instanz ab, um die Spielerposition zu bestimmen und Sound-Effekte auszulösen.

### 2. Statische Zufallszahlen (Nicht-Determinismus)
*   **Zeile 27:** `private static final Random RANDOM = new Random();`
*   **Zeile 68:** `if (RANDOM.nextFloat(0f, 1f) < CHANCE_TO_PLAY_SOUND)`
*   **Problem:** Die Entscheidung, ob ein Sound abgespielt wird, basiert auf einer extrem niedrigen Wahrscheinlichkeit (0,1%). In einem Unit-Test ist es ohne Kontrolle über den Seed oder das Mocking des Zufallszahlengenerators fast unmöglich, den Pfad des Sound-Abspielens gezielt zu testen oder zu verhindern.

### 3. Hardcodierte Schwellenwerte
*   **Zeilen 28, 29:** `DISTANCE_THRESHOLD = 10.0f` & `CHANCE_TO_PLAY_SOUND = 0.001f`
*   **Problem:** Da diese Werte `private static final` sind, können sie für Tests nicht angepasst werden. Ein Test, der die Sound-Logik verifizieren will, müsste entweder sehr viele Iterationen durchlaufen oder die Positionen der Entitäten extrem präzise setzen.

---

## Zusammenfassung
Die größten Hindernisse für Unit-Tests im `IdleSoundSystem` sind der **statische Zugriff auf das Audio-System** und die **Nicht-Deterministik durch den statischen Random-Generator**.

**Empfohlene Lösung:**
1.  **Wrapper für Game-Aufrufe:** Die Abfrage der Spielerposition und das Abspielen von Sounds in `protected` Methoden kapseln.
2.  **Kontrolle über Zufall:** Den `Random`-Generator entweder über den Konstruktor injizierbar machen oder die Zufallsentscheidung in eine überschreibbare Methode auslagern.
3.  **Konfigurierbare Schwellenwerte:** Die Schwellenwerte für Distanz und Wahrscheinlichkeit über den Konstruktor oder Setter steuerbar machen.

# Analyse der Testbarkeit von LevelEditorSystem.java

Das `LevelEditorSystem` ist eines der komplexesten Systeme im Projekt, da es direkt in die Engine-Mechaniken (Input, Rendering, Level-Struktur) eingreift und massiv Gebrauch von statischem Zustand macht.

## Identifizierte Problemstellen

### 1. Extremer Einsatz von statischem Zustand
Fast der gesamte Systemzustand ist über statische Variablen definiert.
*   **Variablen:** `active`, `internalStopped`, `currentMode`, `currentModeInstance`, `feedbackMessage`, `playerClallbacks`, `pathToLevels`.
*   **Problem:** Dies führt zu massiver **Test-Pollution**. Wenn ein Test den Editor aktiviert oder den Modus wechselt, bleibt dieser Zustand für alle folgenden Tests erhalten, was zu unvorhersehbarem Verhalten führt.

### 2. Harte Abhängigkeit von core.Game und Engine-Systemen
*   **Zeilen 107, 187:** `Game.player()` – Erwartet eine aktive Spieler-Entität im globalen Kontext.
*   **Zeilen 152, 172:** `Game.windowHeight()` & `Game.currentLevel()` – Erfordert eine vollständig initialisierte Spielwelt.
*   **Zeile 219:** `Game.systems().get(DrawSystem.class)` – Greift direkt auf andere Engine-Systeme zu, um Shader zu manipulieren.

### 3. LibGDX und Grafik-Kontext
*   **Zeile 52:** `public static final BitmapFont FONT = FontHelper.getDefaultFont(24);`
*   **Problem:** Die statische Initialisierung der Font schlägt in reinen Unit-Tests ohne Grafik-Backend (Headless) fehl, da LibGDX-Ressourcen geladen werden.
*   **Zeile 166:** `Gdx.graphics.getDeltaTime()` – Direkter Zugriff auf die statische LibGDX-Grafik-API.

### 4. Instanziierung von Logik-Klassen (Modes)
*   **Methode:** `getModeInstance()`
*   **Problem:** Die verschiedenen Editor-Modi (`TilesMode`, `DecoMode`, etc.) werden hart im Code instanziiert. Es ist unmöglich, das Verhalten des Systems zu testen, ohne die gesamte Logik aller Unter-Modi mit zu testen.

### 5. Statische Seiteneffekte in Methoden
*   **Methode:** `active(boolean active)`
*   **Problem:** Diese Methode verändert aktiv Komponenten von Entitäten im Spiel (z. B. God-Mode für den Spieler, Entfernen von Input-Callbacks). Dies in einem isolierten Test zu verifizieren oder rückgängig zu machen, ist extrem aufwendig.

---

## Zusammenfassung
Das `LevelEditorSystem` ist in seinem aktuellen Zustand **nahezu untestbar** für Unit-Tests. Die Kombination aus globalem statischem Zustand, harten Abhängigkeiten zum Grafik-Framework und direkten Zugriffen auf die `Game`-Welt macht eine Isolation unmöglich.

**Empfohlene Lösung:**
1.  **Eliminierung statischer Felder:** Der gesamte Zustand (`active`, `currentMode` etc.) muss in Instanzvariablen umgewandelt werden.
2.  **Abstraktion der Grafik/Input-API:** Zugriffe auf `Gdx` und `FontHelper` müssen hinter Interfaces oder `protected` Methoden verborgen werden.
3.  **Injektion von Modi:** Die verschiedenen Editor-Modi sollten über eine Factory oder per Dependency Injection bereitgestellt werden.



