package contrib.item.concreteItem;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ItemFairyConstructorTest {

  /**
   * Prüft, ob die Fee fehlerfrei instanziiert wird und ihre vordefinierten Standardwerte (Name,
   * Beschreibung, Texturpfad) exakt gesetzt sind.
   */
  @Test
  void fairyItem_testConstructorG1_StandardInitialisierung() {
    // Arrange & Act:
    ItemFairy itemFairy = new ItemFairy();

    // Assert: Das Objekt wird erfolgreich instanziiert.
    assertNotNull(
        itemFairy, "Das Objekt sollte erfolgreich instanziiert werden und nicht null sein.");

    // Assert: Der Anzeigename entspricht exakt "Fee".
    assertEquals(
        "Fee", itemFairy.displayName(), "Der Anzeigename wurde nicht korrekt initialisiert.");

    // Assert: Die Beschreibung entspricht exakt "Heilt volle HP.".
    assertEquals(
        "Heilt volle HP.",
        itemFairy.description(),
        "Die Beschreibung wurde nicht korrekt initialisiert.");

    // 1. Prüfen, ob die Animation überhaupt einen Ursprungspfad hat
    assertEquals(
        true,
        itemFairy.inventoryAnimation().sourcePath().isPresent(),
        "Die Animation der Fee sollte einen Quellpfad besitzen.");

    // 2. Den Pfad extrahieren und vergleichen
    assertEquals(
        ItemFairy.FAIRY_TEXTURE.pathString(),
        itemFairy.inventoryAnimation().sourcePath().get().pathString(),
        "Der Pfad der Textur entspricht nicht dem erwarteten FAIRY_TEXTURE-Pfad.");
  }
}
