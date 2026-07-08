package contrib.item.concreteItem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

import core.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ItemFairyCollectTest {

  private ItemFairy itemFairy;

  @BeforeEach
  void setUp() {
    itemFairy = new ItemFairy();
  }

  /**
   * Stellt sicher, dass die Fee niemals in ein Inventar aufgenommen werden kann und die Methode
   * folgerichtig immer false zurückgibt.
   */
  @Test
  void testCollectG1_BeliebigeEntitaeten() {
    // Arrange: Zwei beliebige, gültige Entitäten erstellen
    Entity dummyItem = mock(Entity.class);
    Entity dummyCollector = mock(Entity.class);

    // Act: Die Methode aufrufen
    boolean result = itemFairy.collect(dummyItem, dummyCollector);

    // Assert: Erwartung ist exakt false
    assertEquals(
        false,
        result,
        "Die collect-Methode muss bei gültigen Entitäten false zurückgeben, da die Fee nicht aufgesammelt werden darf.");
  }

  /**
   * Bestätigt, dass die Methode robust programmiert ist und selbst bei der Übergabe von leeren
   * Variablen (null) nicht abstürzt, sondern sicher false liefert.
   */
  @Test
  void testCollectG2_NullReferenzen() {
    // Act & Assert: Prüfen, dass keine Exception geworfen wird
    assertDoesNotThrow(
        () -> {

          // Act: Aufruf mit null-Referenzen
          boolean result = itemFairy.collect(null, null);

          // Assert: Erwartung ist weiterhin exakt false
          assertEquals(
              false,
              result,
              "Die Methode muss false zurückgeben, selbst wenn null-Werte übergeben werden.");
        },
        "Die Methode darf bei der Übergabe von null keine Exception (wie NullPointerException) werfen.");
  }
}
