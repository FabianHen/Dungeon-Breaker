package contrib.components.inventroycomponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import contrib.components.InventoryComponent;
import contrib.item.Item;
import org.junit.jupiter.api.Test;

/** Tests für die Konstruktoren der InventoryComponent nach definierten Äquivalenzklassen. */
public class InventoryComponentConstructorTest {

  /** G1: Default-Konstruktor erzeugt 24 Slots. */
  @Test
  public void testConstructorG1_Default_Creates24Slots() {
    // Arrange & Act
    InventoryComponent inventory = new InventoryComponent();

    // Assert
    assertEquals(
        24, inventory.items().length, "Der Default-Konstruktor muss exakt 24 Slots anlegen.");
    assertEquals(0, inventory.count(), "Das Inventar muss initial leer sein (count == 0).");
    assertTrue(inventory.isEmpty(), "isEmpty() muss true zurückgeben.");
  }

  /** G2: positive Größe. */
  @Test
  public void testConstructorG2_PositiveSize() {
    // Arrange
    int maxSize = 10;

    // Act
    InventoryComponent inventory = new InventoryComponent(maxSize);

    // Assert
    assertEquals(
        maxSize,
        inventory.items().length,
        "Das Inventar muss exakt die übergebene Anzahl an Slots anlegen.");
    assertEquals(0, inventory.count(), "Das Inventar muss initial leer sein.");
    assertTrue(inventory.isEmpty(), "isEmpty() muss true zurückgeben.");
  }

  /** G3: Größe 0. */
  @Test
  public void testConstructorG3_ZeroSize() {
    // Arrange
    InventoryComponent inventory = new InventoryComponent(0);
    Item mockItem = mock(Item.class);
    when(mockItem.stackSize()).thenReturn(1);

    // Act
    int nextSlot = inventory.findNextAvailableSlot();
    boolean itemAdded = inventory.add(mockItem);

    // Assert
    assertEquals(-1, nextSlot, "Bei Größe 0 darf kein Slot verfügbar sein (-1).");
    assertFalse(itemAdded, "add() muss false zurückgeben, da kein Platz vorhanden ist.");
  }

  /** U1: negative Größe. */
  @Test
  public void testConstructorU1_NegativeSize_ThrowsException() {
    // Arrange
    int invalidSize = -5;

    // Act & Assert
    assertThrows(
        NegativeArraySizeException.class,
        () -> new InventoryComponent(invalidSize),
        "Eine negative Größe muss eine NegativeArraySizeException auslösen.");
  }
}
