package contrib.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import contrib.item.Item;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Test für InventoryComponent.itemOfClass() */
public class ItemOfClassTest {
  private static class SwordItem extends Item {
    SwordItem() {
      super(null, null, null, null, 1, 1);
    }
  }

  private static class PotionItem extends Item {
    PotionItem() {
      super(null, null, null, null, 1, 1);
    }
  }

  private static InventoryComponent inventoryWith(Item... items) {
    InventoryComponent inventoryComponent = new InventoryComponent(Math.max(items.length, 1));
    for (int i = 0; i < items.length; i++) {
      inventoryComponent.set(i, items[i]);
    }
    return inventoryComponent;
  }

  @Test
  void itemOfClass_SingleInstance_ReturnsThatItem() {
    // Arrange
    SwordItem sword = new SwordItem();
    InventoryComponent inventoryComponent = inventoryWith(sword);

    // Act
    Optional<Item> result = inventoryComponent.itemOfClass(SwordItem.class);

    // Assert
    assertEquals(Optional.of(sword), result);
  }

  @Test
  void itemOfClass_MultipleInstances_ReturnFirstInArrayOrder() {
    // Arrange
    SwordItem firstInArray = new SwordItem();
    InventoryComponent inventoryComponent = inventoryWith(firstInArray, new SwordItem());

    // Act
    Optional<Item> result = inventoryComponent.itemOfClass(SwordItem.class);

    // Assert
    assertEquals(Optional.of(firstInArray), result);
  }

  @Test
  void itemOfClass_NoInstance_ReturnsEmpty() {
    // Arrange
    InventoryComponent inventoryComponent = inventoryWith(new PotionItem());

    // Act
    Optional<Item> result = inventoryComponent.itemOfClass(SwordItem.class);

    // Assert
    assertEquals(Optional.empty(), result);
  }

  @Test
  void itemOfClass_NullClass_ThrowsNullPointerException() {
    // Arrange
    InventoryComponent inventoryComponent = inventoryWith(new SwordItem());

    // Act & Assert
    assertThrows(NullPointerException.class, () -> inventoryComponent.itemOfClass(null));
  }
}
