package contrib.entities;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import contrib.components.InventoryComponent;
import contrib.item.Item;
import core.Entity;
import core.utils.components.MissingComponentException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link HeroController#useItem(Entity entity, int itemSlot)}. This test assume the
 * correkt behaivior of {@link InventoryComponent#get(int)}.
 */
public class useItemTest {

  // G1. Gültiges Item im Slot wird erfolgreich verwendet 1/2
  @Test
  void useItem_ItemSlot0ValidEntityWithItemInInventory_ItemUsedOnce() {
    // arrange
    int itemSlot = 0;
    Entity mockEntity = mock(Entity.class);
    InventoryComponent mockInventoryComponent = mock(InventoryComponent.class);
    Item mockItem = mock(Item.class);
    when(mockEntity.fetch(InventoryComponent.class))
        .thenReturn(Optional.of(mockInventoryComponent));
    when(mockInventoryComponent.get(itemSlot)).thenReturn(Optional.of(mockItem));

    // act
    HeroController.useItem(mockEntity, itemSlot);

    // assert
    verify(mockItem, times(1)).use(mockEntity);
  }

  // G1. Gültiges Item im Slot wird erfolgreich verwendet 2/2
  @Test
  void useItem_ItemSlot0ValidEntityWithItemInInventory_ReturnTrue() {
    // arrange
    int itemSlot = 0;
    Entity mockEntity = mock(Entity.class);
    InventoryComponent mockInventoryComponent = mock(InventoryComponent.class);
    Item mockItem = mock(Item.class);
    when(mockEntity.fetch(InventoryComponent.class))
        .thenReturn(Optional.of(mockInventoryComponent));
    when(mockInventoryComponent.get(itemSlot)).thenReturn(Optional.of(mockItem));

    // assert
    assertTrue(HeroController.useItem(mockEntity, itemSlot), "return value of useItem is false");
  }

  // G2. Slot ist leer 1/2
  @Test
  void useItem_ItemSlot0ValidEntityWithEmptyInventory_NoExeptionThrown() {
    // arrange
    int itemSlot = 0;
    Entity mockEntity = mock(Entity.class);
    InventoryComponent mockInventoryComponent = mock(InventoryComponent.class);
    when(mockEntity.fetch(InventoryComponent.class))
        .thenReturn(Optional.of(mockInventoryComponent));
    when(mockInventoryComponent.get(itemSlot)).thenReturn(Optional.empty());

    // assert
    assertDoesNotThrow(
        () -> {
          HeroController.useItem(mockEntity, itemSlot);
        },
        "useItem throw an exeption");
  }

  // G2. Slot ist leer 2/2
  @Test
  void useItem_ItemSlot0ValidEntityWithEmptyInventory_ReturnFalse() {
    // arrange
    int itemSlot = 0;
    Entity mockEntity = mock(Entity.class);
    InventoryComponent mockInventoryComponent = mock(InventoryComponent.class);
    when(mockEntity.fetch(InventoryComponent.class))
        .thenReturn(Optional.of(mockInventoryComponent));
    when(mockInventoryComponent.get(itemSlot)).thenReturn(Optional.empty());

    // assert
    assertFalse(HeroController.useItem(mockEntity, itemSlot), "return value of useItem is true");
  }

  // U1. Entity ist null
  @Test
  void useItem_ItemSlot0EntityNull_ThrowNullPointerException() {
    // arrange
    int itemSlot = 0;

    // assert
    assertThrows(
        NullPointerException.class,
        () -> {
          HeroController.useItem(null, itemSlot);
        },
        "useItem not throw an exeption");
  }

  // U2. InventoryComponent fehlt
  @Test
  void useItem_ItemSlot0ValidEntityWithNoInventory_ThrowMissingComponentException() {
    // arrange
    int itemSlot = 0;
    Entity mockEntity = mock(Entity.class);
    when(mockEntity.fetch(InventoryComponent.class)).thenReturn(Optional.empty());

    // assert
    assertThrows(
        MissingComponentException.class,
        () -> {
          HeroController.useItem(mockEntity, itemSlot);
        },
        "useItem not throw an exeption");
  }
}
