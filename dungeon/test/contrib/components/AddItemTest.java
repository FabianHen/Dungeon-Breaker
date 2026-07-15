package contrib.components;

import static org.junit.jupiter.api.Assertions.*;

import contrib.item.Item;
import core.utils.components.draw.animation.Animation;
import core.utils.components.path.SimpleIPath;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests für {@code InventoryComponent.add(Item)}.
 *
 * <p>Deckt sowohl gültige als auch ungültige Äquivalenzklassen ab: vollständiges und teilweises
 * Stacking in bestehende Stacks, Ablage in einem freien Slot bei fehlendem gleichartigen Stack,
 * Ablage in ein leeres Inventar, ein volles Inventar ohne Stacking-Möglichkeit sowie {@code null}
 * als Item.
 */
public class AddItemTest {

  private static final SimpleIPath MISSING_TEXTURE =
      new SimpleIPath("animation/missing_texture.png");

  @Test
  void addStacksIntoExistingStackWhenInventoryIsFull() {
    InventoryComponent inventory = new InventoryComponent(1);
    Item existing = createItem("Existing", 3, 5);
    Item incoming = createItem("Incoming", 2, 5);
    inventory.set(0, existing);
    assertEquals(-1, inventory.findNextAvailableSlot(), "Vorbedingung: kein freier Slot");

    List<Item> addedItems = new ArrayList<>();
    inventory.onItemAdded(addedItems::add);

    boolean result = inventory.add(incoming);

    assertTrue(result);
    assertEquals(1, inventory.count());
    assertEquals(5, existing.stackSize());
    assertEquals(0, incoming.stackSize());
    assertEquals(List.of(incoming), addedItems);
  }

  @Test
  void addPartiallyStacksAndUsesFirstFreeSlot() {
    InventoryComponent inventory = new InventoryComponent(2);
    Item existing = createItem("Existing", 2, 5);
    Item incoming = createItem("Incoming", 4, 5);
    inventory.set(0, existing);
    assertNotEquals(-1, inventory.findNextAvailableSlot(), "Vorbedingung: freier Slot vorhanden");

    List<Item> addedItems = new ArrayList<>();
    inventory.onItemAdded(addedItems::add);

    boolean result = inventory.add(incoming);

    assertTrue(result);
    assertEquals(2, inventory.count());
    assertEquals(5, existing.stackSize());
    assertEquals(1, incoming.stackSize());
    assertSame(incoming, inventory.get(1).orElseThrow());
    assertEquals(List.of(incoming), addedItems);
  }

  @Test
  void addPlacesItemInFirstFreeSlotWhenNoSameClassStackExists() {
    InventoryComponent inventory = new InventoryComponent(2);
    inventory.set(0, createOtherItem("Other", 1, 1));
    Item incoming = createItem("Incoming", 1, 1);

    List<Item> addedItems = new ArrayList<>();
    inventory.onItemAdded(addedItems::add);

    boolean result = inventory.add(incoming);

    assertTrue(result);
    assertSame(incoming, inventory.get(1).orElseThrow());
    assertEquals(List.of(incoming), addedItems);
  }

  @Test
  void addPlacesItemInFirstSlotWhenInventoryIsEmpty() {
    InventoryComponent inventory = new InventoryComponent(3);
    Item incoming = createItem("Incoming", 1, 1);

    boolean result = inventory.add(incoming);

    assertTrue(result);
    assertSame(incoming, inventory.get(0).orElseThrow());
    assertEquals(1, inventory.count());
  }

  @Test
  void addReturnsFalseWhenInventoryIsFullAndCannotStack() {
    InventoryComponent inventory = new InventoryComponent(1);
    Item existing = createItem("Existing", 5, 5);
    inventory.set(0, existing);
    assertEquals(-1, inventory.findNextAvailableSlot(), "Vorbedingung: kein freier Slot");

    List<Item> addedItems = new ArrayList<>();
    inventory.onItemAdded(addedItems::add);

    boolean result = inventory.add(createItem("Incoming", 1, 5));

    assertFalse(result);
    assertEquals(1, inventory.count());
    assertSame(existing, inventory.get(0).orElseThrow());
    assertTrue(addedItems.isEmpty());
  }

  @Test
  void addThrowsNullPointerExceptionForNullItem() {
    InventoryComponent inventory = new InventoryComponent(1);

    assertThrows(NullPointerException.class, () -> inventory.add(null));
  }

  private static Item createItem(String name, int stackSize, int maxStackSize) {
    return new TestItem(name, stackSize, maxStackSize);
  }

  private static Item createOtherItem(String name, int stackSize, int maxStackSize) {
    return new OtherItem(name, stackSize, maxStackSize);
  }

  private static Animation createAnimation() {
    return new Animation(MISSING_TEXTURE);
  }

  private static class TestItem extends Item {
    private TestItem(String name, int stackSize, int maxStackSize) {
      super(name, "description", createAnimation(), createAnimation(), stackSize, maxStackSize);
    }
  }

  private static class OtherItem extends Item {
    private OtherItem(String name, int stackSize, int maxStackSize) {
      super(name, "description", createAnimation(), createAnimation(), stackSize, maxStackSize);
    }
  }
}