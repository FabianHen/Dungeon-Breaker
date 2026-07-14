package contrib.components;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import contrib.item.Item;
import core.Entity;
import core.Game;
import core.utils.components.draw.animation.Animation;
import core.utils.components.path.SimpleIPath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/** Tests for the {@link InventoryComponent}. */
public class InventoryComponentTest {

  /** WTF? . */
  public static final SimpleIPath MISSING_TEXTURE =
      new SimpleIPath("animation/missing_texture.png");

  /** WTF? . */
  @AfterEach
  public void cleanup() {
    Game.removeAllEntities();
  }

  /** Constructor should create the inventory with the given parameters. */
  @Test
  public void validCreation() {

    Entity e = new Entity();
    InventoryComponent ic = new InventoryComponent(1);
    e.add(ic);
    assertEquals(0, ic.count());
  }

  /** Adding one valid Item. */
  @Test
  public void addItemValid() {
    Entity e = new Entity();
    InventoryComponent ic = new InventoryComponent(1);
    e.add(ic);
    Item itemData = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
    assertTrue(ic.add(itemData));
    assertEquals(1, ic.count());
  }

  /**
   * When there is enough space in the Inventory it should be possible to add more than one Item.
   */
  @Test
  public void addItemValidMultiple() {
    Entity e = new Entity();
    InventoryComponent ic = new InventoryComponent(3);
    e.add(ic);
    ic.add(new Item("Test item", "Test description", new Animation(MISSING_TEXTURE)));
    assertTrue(ic.add(new Item("Test item", "Test description", new Animation(MISSING_TEXTURE))));

    assertEquals(2, ic.count());
  }

  /** Adding two Items to an Inventory with a size of 1 should only add the first. */
  @Test
  public void addItemOverSize() {
    Entity e = new Entity();
    InventoryComponent ic = new InventoryComponent(1);
    e.add(ic);
    ic.add(new Item("Test item", "Test description", new Animation(MISSING_TEXTURE)));
    assertFalse(ic.add(new Item("Test item", "Test description", new Animation(MISSING_TEXTURE))));
    assertEquals(1, ic.count());
  }

  @Nested
  class RemoveItem {

    /** G1: Removing an equals-matching Item removes the stored item. */
    @Test
    public void removeItemEqualsMatchAssertOne() {
      Entity e = new Entity();
      InventoryComponent ic = new InventoryComponent(1);
      e.add(ic);
      Item storedItem = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      Item equalItem = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));

      ic.add(storedItem);
      Optional<Item> removed = ic.remove(equalItem);

      assertTrue(removed.isPresent());
    }

    /** G1: Removing an equals-matching Item removes the stored item. */
    @Test
    public void removeItemEqualsMatchAssertTwo() {
      Entity e = new Entity();
      InventoryComponent ic = new InventoryComponent(1);
      e.add(ic);
      Item storedItem = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      Item equalItem = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));

      ic.add(storedItem);
      Optional<Item> removed = ic.remove(equalItem);

      assertTrue(removed.isPresent());
      assertSame(storedItem, removed.get());
    }

    /** G1: Removing an equals-matching Item removes the stored item. */
    @Test
    public void removeItemEqualsMatchAssertThree() {
      Entity e = new Entity();
      InventoryComponent ic = new InventoryComponent(1);
      e.add(ic);
      Item storedItem = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      Item equalItem = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));

      ic.add(storedItem);
      Optional<Item> removed = ic.remove(equalItem);

      assertNull(ic.items()[0]);
    }

    /** G1: Removing an equals-matching Item removes the stored item. */
    @Test
    public void removeItemEqualsMatchAssertFour() {
      Entity e = new Entity();
      InventoryComponent ic = new InventoryComponent(1);
      e.add(ic);
      List<Item> removedItems = new ArrayList<>();
      ic.onItemRemoved(removedItems::add);
      Item storedItem = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      Item equalItem = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));

      ic.add(storedItem);
      ic.remove(equalItem);

      assertEquals(List.of(storedItem), removedItems);
    }

    /** G1: Removing an equals-matching Item removes the stored item. */
    @Test
    public void removeItemEqualsMatchAssertFive() {
      Entity e = new Entity();
      InventoryComponent ic = new InventoryComponent(1);
      e.add(ic);
      Item storedItem = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      Item equalItem = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));

      ic.add(storedItem);
      ic.remove(equalItem);

      assertEquals(0, ic.count());
    }

    /** G2: Removing one of multiple equals-matching Items removes the first occurrence. */
    @Test
    public void removeItemMultipleEqualsMatchesRemovesFirstOccurrenceAssertOne() {
      Entity e = new Entity();
      InventoryComponent ic = new InventoryComponent(3);
      e.add(ic);
      Item firstMatch = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      Item secondMatch = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      Item otherItem = new Item("Other item", "Other description", new Animation(MISSING_TEXTURE));
      Item equalItem = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));

      ic.set(0, firstMatch);
      ic.set(1, otherItem);
      ic.set(2, secondMatch);
      Optional<Item> removed = ic.remove(equalItem);

      assertTrue(removed.isPresent());
    }

    /** G2: Removing one of multiple equals-matching Items removes the first occurrence. */
    @Test
    public void removeItemMultipleEqualsMatchesRemovesFirstOccurrenceAssertTwo() {
      Entity e = new Entity();
      InventoryComponent ic = new InventoryComponent(3);
      e.add(ic);
      Item firstMatch = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      Item secondMatch = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      Item otherItem = new Item("Other item", "Other description", new Animation(MISSING_TEXTURE));
      Item equalItem = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));

      ic.set(0, firstMatch);
      ic.set(1, otherItem);
      ic.set(2, secondMatch);
      Optional<Item> removed = ic.remove(equalItem);

      assertTrue(removed.isPresent());
      assertSame(firstMatch, removed.get());
    }

    /** G2: Removing one of multiple equals-matching Items removes the first occurrence. */
    @Test
    public void removeItemMultipleEqualsMatchesRemovesFirstOccurrenceAssertThree() {
      Entity e = new Entity();
      InventoryComponent ic = new InventoryComponent(3);
      e.add(ic);
      Item firstMatch = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      Item secondMatch = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      Item otherItem = new Item("Other item", "Other description", new Animation(MISSING_TEXTURE));
      Item equalItem = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));

      ic.set(0, firstMatch);
      ic.set(1, otherItem);
      ic.set(2, secondMatch);
      ic.remove(equalItem);

      assertNull(ic.items()[0]);
    }

    /** G2: Removing one of multiple equals-matching Items removes the first occurrence. */
    @Test
    public void removeItemMultipleEqualsMatchesRemovesFirstOccurrenceAssertFour() {
      Entity e = new Entity();
      InventoryComponent ic = new InventoryComponent(3);
      e.add(ic);
      Item firstMatch = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      Item secondMatch = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      Item otherItem = new Item("Other item", "Other description", new Animation(MISSING_TEXTURE));
      Item equalItem = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));

      ic.set(0, firstMatch);
      ic.set(1, otherItem);
      ic.set(2, secondMatch);
      ic.remove(equalItem);

      assertSame(otherItem, ic.items()[1]);
    }

    /** G2: Removing one of multiple equals-matching Items removes the first occurrence. */
    @Test
    public void removeItemMultipleEqualsMatchesRemovesFirstOccurrenceAssertFive() {
      Entity e = new Entity();
      InventoryComponent ic = new InventoryComponent(3);
      e.add(ic);
      Item firstMatch = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      Item secondMatch = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      Item otherItem = new Item("Other item", "Other description", new Animation(MISSING_TEXTURE));
      Item equalItem = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));

      ic.set(0, firstMatch);
      ic.set(1, otherItem);
      ic.set(2, secondMatch);
      ic.remove(equalItem);

      assertSame(secondMatch, ic.items()[2]);
    }

    /** G2: Removing one of multiple equals-matching Items removes the first occurrence. */
    @Test
    public void removeItemMultipleEqualsMatchesRemovesFirstOccurrenceAssertSix() {
      Entity e = new Entity();
      InventoryComponent ic = new InventoryComponent(3);
      e.add(ic);
      List<Item> removedItems = new ArrayList<>();
      ic.onItemRemoved(removedItems::add);
      Item firstMatch = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      Item secondMatch = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      Item otherItem = new Item("Other item", "Other description", new Animation(MISSING_TEXTURE));
      Item equalItem = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));

      ic.set(0, firstMatch);
      ic.set(1, otherItem);
      ic.set(2, secondMatch);
      ic.remove(equalItem);

      assertEquals(List.of(firstMatch), removedItems);
    }

    /** G2: Removing one of multiple equals-matching Items removes the first occurrence. */
    @Test
    public void removeItemMultipleEqualsMatchesRemovesFirstOccurrenceAssertSeven() {
      Entity e = new Entity();
      InventoryComponent ic = new InventoryComponent(3);
      e.add(ic);
      Item firstMatch = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      Item secondMatch = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      Item otherItem = new Item("Other item", "Other description", new Animation(MISSING_TEXTURE));
      Item equalItem = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));

      ic.set(0, firstMatch);
      ic.set(1, otherItem);
      ic.set(2, secondMatch);
      Optional<Item> removed = ic.remove(equalItem);

      assertEquals(2, ic.count());
    }

    /** U1: Removing a non-existing Item leaves the inventory unchanged. */
    @Test
    public void removeItemNotPresentAssertOne() {
      Entity e = new Entity();
      InventoryComponent ic = new InventoryComponent(2);
      e.add(ic);
      Item storedItem = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      Item otherItem = new Item("Other item", "Other description", new Animation(MISSING_TEXTURE));

      ic.add(storedItem);
      Optional<Item> removed = ic.remove(otherItem);

      assertTrue(removed.isEmpty());
    }

    /** U1: Removing a non-existing Item leaves the inventory unchanged. */
    @Test
    public void removeItemNotPresentAssertTwo() {
      Entity e = new Entity();
      InventoryComponent ic = new InventoryComponent(2);
      e.add(ic);
      List<Item> removedItems = new ArrayList<>();
      ic.onItemRemoved(removedItems::add);
      Item storedItem = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      Item otherItem = new Item("Other item", "Other description", new Animation(MISSING_TEXTURE));

      ic.add(storedItem);
      ic.remove(otherItem);

      assertTrue(removedItems.isEmpty());
    }

    /** U1: Removing a non-existing Item leaves the inventory unchanged. */
    @Test
    public void removeItemNotPresentAssertThree() {
      Entity e = new Entity();
      InventoryComponent ic = new InventoryComponent(2);
      e.add(ic);
      Item storedItem = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      Item otherItem = new Item("Other item", "Other description", new Animation(MISSING_TEXTURE));
      Item[] expectedItems;

      ic.add(storedItem);
      expectedItems = ic.items();
      ic.remove(otherItem);
      Item[] actualItems = ic.items();

      assertSame(expectedItems[0], actualItems[0]);
    }

    /** U1: Removing a non-existing Item leaves the inventory unchanged. */
    @Test
    public void removeItemNotPresentAssertFour() {
      Entity e = new Entity();
      InventoryComponent ic = new InventoryComponent(2);
      e.add(ic);
      Item storedItem = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      Item otherItem = new Item("Other item", "Other description", new Animation(MISSING_TEXTURE));
      Item[] expectedItems;

      ic.add(storedItem);
      expectedItems = ic.items();
      ic.remove(otherItem);
      Item[] actualItems = ic.items();

      assertSame(expectedItems[1], actualItems[1]);
    }

    /** U2: {@code null} should not throw and should not remove any Item. */
    @Test
    public void removeItemNull() {
      InventoryComponent ic = new InventoryComponent(1);
      Item storedItem = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));

      ic.add(storedItem);

      Optional<Item> removed = assertDoesNotThrow(() -> ic.remove(null));

      assertTrue(removed.isEmpty());
    }
  }

  @Nested
  class RemoveIndex {

    /** G1: A valid index with an occupied slot removes the item. */
    @Test
    public void removeValidOccupiedSlotAssertOne() {
      InventoryComponent ic = new InventoryComponent(1);
      Item item = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));

      ic.add(item);
      Optional<Item> removed = ic.remove(0);

      assertTrue(removed.isPresent());
    }

    /** G1: A valid index with an occupied slot removes the item. */
    @Test
    public void removeValidOccupiedSlotAssertTwo() {
      InventoryComponent ic = new InventoryComponent(1);
      Item item = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));

      ic.add(item);
      Optional<Item> removed = ic.remove(0);

      assertTrue(removed.isPresent());
      assertSame(item, removed.get());
    }

    /** G1: A valid index with an occupied slot removes the item. */
    @Test
    public void removeValidOccupiedSlotAssertThree() {
      InventoryComponent ic = new InventoryComponent(1);
      Item item = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));

      ic.add(item);
      ic.remove(0);

      assertNull(ic.items()[0]);
    }

    /** G1: A valid index with an occupied slot removes the item. */
    @Test
    public void removeValidOccupiedSlotAssertFour() {
      InventoryComponent ic = new InventoryComponent(1);
      List<Item> removedItems = new ArrayList<>();
      ic.onItemRemoved(removedItems::add);
      Item item = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));

      ic.add(item);
      ic.remove(0);

      assertEquals(List.of(item), removedItems);
    }

    /**
     * G2: A valid index with an empty slot returns empty and calls the callback with {@code null}.
     */
    @Test
    public void removeValidEmptySlotAssertOne() {
      InventoryComponent ic = new InventoryComponent(1);

      Optional<Item> removed = ic.remove(0);

      assertTrue(removed.isEmpty());
    }

    /**
     * G2: A valid index with an empty slot returns empty and calls the callback with {@code null}.
     */
    @Test
    public void removeValidEmptySlotAssertTwo() {
      InventoryComponent ic = new InventoryComponent(1);
      List<Item> removedItems = new ArrayList<>();
      ic.onItemRemoved(removedItems::add);

      ic.remove(0);

      assertEquals(1, removedItems.size());
    }

    /**
     * G2: A valid index with an empty slot returns empty and calls the callback with {@code null}.
     */
    @Test
    public void removeValidEmptySlotAssertThree() {
      InventoryComponent ic = new InventoryComponent(1);
      List<Item> removedItems = new ArrayList<>();
      ic.onItemRemoved(removedItems::add);

      ic.remove(0);

      assertNull(removedItems.getFirst());
    }

    /** G3: Index {@code 0} is a valid lower boundary. */
    @Test
    public void removeLowerValidBoundaryAssertOne() {
      InventoryComponent ic = new InventoryComponent(2);
      Item item = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      ic.set(0, item);

      Optional<Item> removed = ic.remove(0);
      assertTrue(removed.isPresent());
    }

    /** G3: Index {@code 0} is a valid lower boundary. */
    @Test
    public void removeLowerValidBoundaryAssertTwo() {
      InventoryComponent ic = new InventoryComponent(2);
      Item item = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      ic.set(0, item);

      Optional<Item> removed = ic.remove(0);

      assertTrue(removed.isPresent());
      assertSame(item, removed.get());
    }

    /** G3: Index {@code 0} is a valid lower boundary. */
    @Test
    public void removeLowerValidBoundaryAssertThree() {
      InventoryComponent ic = new InventoryComponent(2);
      Item item = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      ic.set(0, item);

      ic.remove(0);

      assertNull(ic.items()[0]);
    }

    /** G3: Index {@code 0} is a valid lower boundary. */
    @Test
    public void removeLowerValidBoundaryAssertFour() {
      InventoryComponent ic = new InventoryComponent(2);
      Item item = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      ic.set(0, item);

      List<LogRecord> records =
          collectInventoryWarningsDuring(
              () -> {
                ic.remove(0);
              });

      assertTrue(records.isEmpty());
    }

    /** G4: Index {@code length - 1} is a valid upper boundary. */
    @Test
    public void removeUpperValidBoundaryAssertOne() {
      InventoryComponent ic = new InventoryComponent(2);
      Item item = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      ic.set(1, item);

      Optional<Item> removed = ic.remove(1);
      assertTrue(removed.isPresent());
    }

    /** G4: Index {@code length - 1} is a valid upper boundary. */
    @Test
    public void removeUpperValidBoundaryAssertTwo() {
      InventoryComponent ic = new InventoryComponent(2);
      Item item = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      ic.set(1, item);

      Optional<Item> removed = ic.remove(1);

      assertTrue(removed.isPresent());
      assertSame(item, removed.get());
    }

    /** G4: Index {@code length - 1} is a valid upper boundary. */
    @Test
    public void removeUpperValidBoundaryAssertThree() {
      InventoryComponent ic = new InventoryComponent(2);
      Item item = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      ic.set(1, item);

      ic.remove(1);

      assertNull(ic.items()[1]);
    }

    /** G4: Index {@code length - 1} is a valid upper boundary. */
    @Test
    public void removeUpperValidBoundaryAssertFour() {
      InventoryComponent ic = new InventoryComponent(2);
      Item item = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
      ic.set(1, item);

      List<LogRecord> records =
          collectInventoryWarningsDuring(
              () -> {
                ic.remove(1);
              });

      assertTrue(records.isEmpty());
    }

    /** U1: A negative index returns empty without a callback. */
    @Test
    public void removeNegativeIndexAssertOne() {
      InventoryComponent ic = new InventoryComponent(1);

      Optional<Item> removed = ic.remove(-2);

      assertTrue(removed.isEmpty());
    }

    /** U1: A negative index returns empty without a callback. */
    @Test
    public void removeNegativeIndexAssertTwo() {
      InventoryComponent ic = new InventoryComponent(1);
      List<Item> removedItems = new ArrayList<>();
      ic.onItemRemoved(removedItems::add);

      Optional<Item> removed = ic.remove(-2);

      assertTrue(removedItems.isEmpty());
    }

    /** U1: A negative index returns empty without a callback. */
    @Test
    public void removeNegativeIndexAssertThree() {
      InventoryComponent ic = new InventoryComponent(1);

      List<LogRecord> records =
          collectInventoryWarningsDuring(
              () -> {
                ic.remove(-2);
              });

      assertFalse(records.isEmpty());
    }

    /** U2: An index outside the inventory returns empty without a callback. */
    @Test
    public void removeIndexOutsideInventoryAssertOne() {
      InventoryComponent ic = new InventoryComponent(1);

      Optional<Item> removed = ic.remove(2);

      assertTrue(removed.isEmpty());
    }

    /** U2: An index outside the inventory returns empty without a callback. */
    @Test
    public void removeIndexOutsideInventoryAssertTwo() {
      InventoryComponent ic = new InventoryComponent(1);
      List<Item> removedItems = new ArrayList<>();
      ic.onItemRemoved(removedItems::add);

      Optional<Item> removed = ic.remove(2);

      assertTrue(removedItems.isEmpty());
    }

    /** U2: An index outside the inventory returns empty without a callback. */
    @Test
    public void removeIndexOutsideInventoryAssertThree() {
      InventoryComponent ic = new InventoryComponent(1);

      List<LogRecord> records =
          collectInventoryWarningsDuring(
              () -> {
                ic.remove(2);
              });

      assertFalse(records.isEmpty());
    }

    /** U3: Index {@code -1} is just below the valid range. */
    @Test
    public void removeJustBelowValidRangeAssertOne() {
      InventoryComponent ic = new InventoryComponent(1);

      Optional<Item> removed = ic.remove(-1);

      assertTrue(removed.isEmpty());
    }

    /** U3: Index {@code -1} is just below the valid range. */
    @Test
    public void removeJustBelowValidRangeAssertTwo() {
      InventoryComponent ic = new InventoryComponent(1);
      List<Item> removedItems = new ArrayList<>();
      ic.onItemRemoved(removedItems::add);

      ic.remove(-1);

      assertTrue(removedItems.isEmpty());
    }

    /** U3: Index {@code -1} is just below the valid range. */
    @Test
    public void removeJustBelowValidRangeAssertThree() {
      InventoryComponent ic = new InventoryComponent(1);

      List<LogRecord> records =
          collectInventoryWarningsDuring(
              () -> {
                ic.remove(-1);
              });

      assertFalse(records.isEmpty());
    }

    /** U4: Index {@code length} is just above the valid range. */
    @Test
    public void removeJustAboveValidRangeAssertOne() {
      InventoryComponent ic = new InventoryComponent(1);

      Optional<Item> removed = ic.remove(1);

      assertTrue(removed.isEmpty());
    }

    /** U4: Index {@code length} is just above the valid range. */
    @Test
    public void removeJustAboveValidRangeAssertTwo() {
      InventoryComponent ic = new InventoryComponent(1);
      List<Item> removedItems = new ArrayList<>();
      ic.onItemRemoved(removedItems::add);

      ic.remove(1);

      assertTrue(removedItems.isEmpty());
    }

    /** U4: Index {@code length} is just above the valid range. */
    @Test
    public void removeJustAboveValidRangeAssertThree() {
      InventoryComponent ic = new InventoryComponent(1);

      List<LogRecord> records =
          collectInventoryWarningsDuring(
              () -> {
                ic.remove(1);
              });

      assertFalse(records.isEmpty());
    }

    private List<LogRecord> collectInventoryWarningsDuring(Runnable action) {
      Logger logger = Logger.getLogger(InventoryComponent.class.getName());
      Level previousLevel = logger.getLevel();
      CapturingHandler handler = new CapturingHandler();
      handler.setLevel(Level.WARNING);

      logger.addHandler(handler);
      logger.setLevel(Level.ALL);
      try {
        action.run();
      } finally {
        logger.removeHandler(handler);
        logger.setLevel(previousLevel);
      }
      return handler.records();
    }
  }

  @Nested
  class RemoveClassAmount {

    /** G1: If {@code amount} is smaller than the first stack, the stack is reduced. */
    @Test
    public void removeAmountSmallerThanFirstStackAssertOne() {
      InventoryComponent ic = new InventoryComponent(1);
      DummyItem stack = new DummyItem(5, 10);
      ic.set(0, stack);

      ic.remove(DummyItem.class, 2);

      assertSame(stack, ic.items()[0]);
    }

    /** G1: If {@code amount} is smaller than the first stack, the stack is reduced. */
    @Test
    public void removeAmountSmallerThanFirstStackAssertTwo() {
      InventoryComponent ic = new InventoryComponent(1);
      DummyItem stack = new DummyItem(5, 10);
      ic.set(0, stack);

      ic.remove(DummyItem.class, 2);

      assertEquals(3, stack.stackSize());
    }

    /** G1: If {@code amount} is smaller than the first stack, the stack is reduced. */
    @Test
    public void removeAmountSmallerThanFirstStackAssertThree() {
      InventoryComponent ic = new InventoryComponent(1);
      List<Item> removedItems = new ArrayList<>();
      ic.onItemRemoved(removedItems::add);
      DummyItem stack = new DummyItem(5, 10);
      ic.set(0, stack);

      ic.remove(DummyItem.class, 2);

      assertEquals(1, removedItems.size());
    }

    /** G1: If {@code amount} is smaller than the first stack, the stack is reduced. */
    @Test
    public void removeAmountSmallerThanFirstStackAssertFour() {
      InventoryComponent ic = new InventoryComponent(1);
      List<Item> removedItems = new ArrayList<>();
      ic.onItemRemoved(removedItems::add);
      DummyItem stack = new DummyItem(5, 10);
      ic.set(0, stack);

      ic.remove(DummyItem.class, 2);

      assertSame(stack, removedItems.getFirst());
    }

    /** G2: If {@code amount} exactly matches a stack, the stack is fully removed. */
    @Test
    public void removeAmountExactlyOneStackAssertOne() {
      InventoryComponent ic = new InventoryComponent(1);
      DummyItem stack = new DummyItem(3, 10);
      ic.set(0, stack);

      ic.remove(DummyItem.class, 3);

      assertNull(ic.items()[0]);
    }

    /** G2: If {@code amount} exactly matches a stack, the stack is fully removed. */
    @Test
    public void removeAmountExactlyOneStackAssertTwo() {
      InventoryComponent ic = new InventoryComponent(1);
      List<Item> removedItems = new ArrayList<>();
      ic.onItemRemoved(removedItems::add);
      DummyItem stack = new DummyItem(3, 10);
      ic.set(0, stack);

      ic.remove(DummyItem.class, 3);

      assertEquals(2, removedItems.size());
    }

    /** G2: If {@code amount} exactly matches a stack, the stack is fully removed. */
    @Test
    public void removeAmountExactlyOneStackAssertThree() {
      InventoryComponent ic = new InventoryComponent(1);
      List<Item> removedItems = new ArrayList<>();
      ic.onItemRemoved(removedItems::add);
      DummyItem stack = new DummyItem(3, 10);
      ic.set(0, stack);

      ic.remove(DummyItem.class, 3);

      assertSame(stack, removedItems.getFirst());
    }

    /** G2: If {@code amount} exactly matches a stack, the stack is fully removed. */
    @Test
    public void removeAmountExactlyOneStackAssertFour() {
      InventoryComponent ic = new InventoryComponent(1);
      List<Item> removedItems = new ArrayList<>();
      ic.onItemRemoved(removedItems::add);
      DummyItem stack = new DummyItem(3, 10);
      ic.set(0, stack);

      ic.remove(DummyItem.class, 3);

      assertSame(stack, removedItems.get(1));
    }

    /** G3: If {@code amount} spans multiple stacks, stacks are processed in slot order. */
    @Test
    public void removeAmountAcrossMultipleStacksAssertOne() {
      InventoryComponent ic = new InventoryComponent(3);
      DummyItem firstStack = new DummyItem(2, 10);
      DummyItem secondStack = new DummyItem(4, 10);
      DummyItem thirdStack = new DummyItem(5, 10);
      ic.set(0, firstStack);
      ic.set(1, secondStack);
      ic.set(2, thirdStack);

      ic.remove(DummyItem.class, 5);

      assertEquals(2, ic.items(DummyItem.class).size());
    }

    /** G3: If {@code amount} spans multiple stacks, stacks are processed in slot order. */
    @Test
    public void removeAmountAcrossMultipleStacksAssertTwo() {
      InventoryComponent ic = new InventoryComponent(3);
      DummyItem firstStack = new DummyItem(2, 10);
      DummyItem secondStack = new DummyItem(4, 10);
      DummyItem thirdStack = new DummyItem(5, 10);
      ic.set(0, firstStack);
      ic.set(1, secondStack);
      ic.set(2, thirdStack);

      ic.remove(DummyItem.class, 5);

      for (Item item : ic.items(DummyItem.class)) {
        assertTrue(item.stackSize() > 0);
      }
    }

    /** G3: If {@code amount} spans multiple stacks, stacks are processed in slot order. */
    @Test
    public void removeAmountAcrossMultipleStacksAssertThree() {
      InventoryComponent ic = new InventoryComponent(3);
      DummyItem firstStack = new DummyItem(2, 10);
      DummyItem secondStack = new DummyItem(4, 10);
      DummyItem thirdStack = new DummyItem(5, 10);
      ic.set(0, firstStack);
      ic.set(1, secondStack);
      ic.set(2, thirdStack);

      ic.remove(DummyItem.class, 5);

      int stackSizeSum = ic.items(DummyItem.class).stream().mapToInt(Item::stackSize).sum();
      assertEquals(6, stackSizeSum);
    }

    /** G3: If {@code amount} spans multiple stacks, stacks are processed in slot order. */
    @Test
    public void removeAmountAcrossMultipleStacksAssertFour() {
      InventoryComponent ic = new InventoryComponent(3);
      List<Item> removedItems = new ArrayList<>();
      ic.onItemRemoved(removedItems::add);
      DummyItem firstStack = new DummyItem(2, 10);
      DummyItem secondStack = new DummyItem(4, 10);
      DummyItem thirdStack = new DummyItem(5, 10);
      ic.set(0, firstStack);
      ic.set(1, secondStack);
      ic.set(2, thirdStack);

      ic.remove(DummyItem.class, 5);

      assertEquals(3, removedItems.size());
    }

    /** G4: If {@code amount} is greater than the total stock, all matching stacks are removed. */
    @Test
    public void removeAmountGreaterThanTotalStockAssertOne() {
      InventoryComponent ic = new InventoryComponent(3);
      DummyItem firstStack = new DummyItem(2, 10);
      DummyItem secondStack = new DummyItem(3, 10);
      OtherDummyItem otherStack = new OtherDummyItem(4, 10);
      ic.set(0, firstStack);
      ic.set(1, otherStack);
      ic.set(2, secondStack);

      assertDoesNotThrow(() -> ic.remove(DummyItem.class, 10));
    }

    /** G4: If {@code amount} is greater than the total stock, all matching stacks are removed. */
    @Test
    public void removeAmountGreaterThanTotalStockAssertTwo() {
      InventoryComponent ic = new InventoryComponent(3);
      DummyItem firstStack = new DummyItem(2, 10);
      DummyItem secondStack = new DummyItem(3, 10);
      OtherDummyItem otherStack = new OtherDummyItem(4, 10);
      ic.set(0, firstStack);
      ic.set(1, otherStack);
      ic.set(2, secondStack);

      ic.remove(DummyItem.class, 10);

      assertNull(ic.items()[0]);
    }

    /** G4: If {@code amount} is greater than the total stock, all matching stacks are removed. */
    @Test
    public void removeAmountGreaterThanTotalStockAssertThree() {
      InventoryComponent ic = new InventoryComponent(3);
      DummyItem firstStack = new DummyItem(2, 10);
      DummyItem secondStack = new DummyItem(3, 10);
      OtherDummyItem otherStack = new OtherDummyItem(4, 10);
      ic.set(0, firstStack);
      ic.set(1, otherStack);
      ic.set(2, secondStack);

      ic.remove(DummyItem.class, 10);

      assertSame(otherStack, ic.items()[1]);
    }

    /** G4: If {@code amount} is greater than the total stock, all matching stacks are removed. */
    @Test
    public void removeAmountGreaterThanTotalStockAssertFour() {
      InventoryComponent ic = new InventoryComponent(3);
      DummyItem firstStack = new DummyItem(2, 10);
      DummyItem secondStack = new DummyItem(3, 10);
      OtherDummyItem otherStack = new OtherDummyItem(4, 10);
      ic.set(0, firstStack);
      ic.set(1, otherStack);
      ic.set(2, secondStack);

      ic.remove(DummyItem.class, 10);

      assertNull(ic.items()[2]);
    }

    /** U1: An {@code amount} less than or equal to zero removes nothing and calls no callback. */
    @Test
    public void removeAmountLessThanOrEqualZeroAssertOne() {
      InventoryComponent ic = new InventoryComponent(1);
      DummyItem stack = new DummyItem(3, 10);
      ic.set(0, stack);

      ic.remove(DummyItem.class, 0);
      ic.remove(DummyItem.class, -1);

      assertSame(stack, ic.items()[0]);
    }

    /** U1: An {@code amount} less than or equal to zero removes nothing and calls no callback. */
    @Test
    public void removeAmountLessThanOrEqualZeroAssertTwo() {
      InventoryComponent ic = new InventoryComponent(1);
      DummyItem stack = new DummyItem(3, 10);
      ic.set(0, stack);

      ic.remove(DummyItem.class, 0);
      ic.remove(DummyItem.class, -1);

      assertEquals(3, stack.stackSize());
    }

    /** U1: An {@code amount} less than or equal to zero removes nothing and calls no callback. */
    @Test
    public void removeAmountLessThanOrEqualZeroAssertThree() {
      InventoryComponent ic = new InventoryComponent(1);
      List<Item> removedItems = new ArrayList<>();
      ic.onItemRemoved(removedItems::add);
      DummyItem stack = new DummyItem(3, 10);
      ic.set(0, stack);

      ic.remove(DummyItem.class, 0);
      ic.remove(DummyItem.class, -1);

      assertTrue(removedItems.isEmpty());
    }

    /** U2: If no item of the class exists, nothing is removed and no callback is called. */
    @Test
    public void removeClassWithoutMatchingItemsAssertOne() {
      InventoryComponent ic = new InventoryComponent(1);
      OtherDummyItem otherStack = new OtherDummyItem(3, 10);
      ic.set(0, otherStack);

      ic.remove(DummyItem.class, 2);

      assertSame(otherStack, ic.items()[0]);
    }

    /** U2: If no item of the class exists, nothing is removed and no callback is called. */
    @Test
    public void removeClassWithoutMatchingItemsAssertTwo() {
      InventoryComponent ic = new InventoryComponent(1);
      OtherDummyItem otherStack = new OtherDummyItem(3, 10);
      ic.set(0, otherStack);

      ic.remove(DummyItem.class, 2);

      assertEquals(3, otherStack.stackSize());
    }

    /** U2: If no item of the class exists, nothing is removed and no callback is called. */
    @Test
    public void removeClassWithoutMatchingItemsAssertThree() {
      InventoryComponent ic = new InventoryComponent(1);
      List<Item> removedItems = new ArrayList<>();
      ic.onItemRemoved(removedItems::add);
      OtherDummyItem otherStack = new OtherDummyItem(3, 10);
      ic.set(0, otherStack);

      ic.remove(DummyItem.class, 2);

      assertTrue(removedItems.isEmpty());
    }

    /** U3: {@code null} as class is invalid. */
    @Test
    public void removeNullClass() {
      InventoryComponent ic = new InventoryComponent(1);

      assertThrows(NullPointerException.class, () -> ic.remove(null, 1));
    }
  }

  @Nested
  class RemoveOneItem {

    /** G1: An identical item instance with {@code stackSize > 1} is reduced by one. */
    @Test
    public void removeOneIdentityMatchKeepsSlotWhenStackGreaterThanOneAssertOne() {
      InventoryComponent ic = new InventoryComponent(1);
      DummyItem item = new DummyItem(3, 10);
      ic.set(0, item);

      boolean removed = ic.removeOne(item);

      assertTrue(removed);
    }

    /** G1: An identical item instance with {@code stackSize > 1} is reduced by one. */
    @Test
    public void removeOneIdentityMatchKeepsSlotWhenStackGreaterThanOneAssertTwo() {
      InventoryComponent ic = new InventoryComponent(1);
      DummyItem item = new DummyItem(3, 10);
      ic.set(0, item);

      ic.removeOne(item);

      assertEquals(2, item.stackSize());
    }

    /** G1: An identical item instance with {@code stackSize > 1} is reduced by one. */
    @Test
    public void removeOneIdentityMatchKeepsSlotWhenStackGreaterThanOneAssertThree() {
      InventoryComponent ic = new InventoryComponent(1);
      DummyItem item = new DummyItem(3, 10);
      ic.set(0, item);

      ic.removeOne(item);

      assertSame(item, ic.items()[0]);
    }

    /** G1: An identical item instance with {@code stackSize > 1} is reduced by one. */
    @Test
    public void removeOneIdentityMatchKeepsSlotWhenStackGreaterThanOneAssertFour() {
      InventoryComponent ic = new InventoryComponent(1);
      List<Item> removedItems = new ArrayList<>();
      ic.onItemRemoved(removedItems::add);
      DummyItem item = new DummyItem(3, 10);
      ic.set(0, item);

      ic.removeOne(item);

      assertEquals(List.of(item), removedItems);
    }

    /** G2: An identical item instance with {@code stackSize == 1} is removed from its slot. */
    @Test
    public void removeOneIdentityMatchClearsSlotWhenStackIsOneAssertOne() {
      InventoryComponent ic = new InventoryComponent(1);
      DummyItem item = new DummyItem(1, 10);
      ic.set(0, item);

      boolean removed = ic.removeOne(item);

      assertTrue(removed);
    }

    /** G2: An identical item instance with {@code stackSize == 1} is removed from its slot. */
    @Test
    public void removeOneIdentityMatchClearsSlotWhenStackIsOneAssertTwo() {
      InventoryComponent ic = new InventoryComponent(1);
      DummyItem item = new DummyItem(1, 10);
      ic.set(0, item);

      boolean removed = ic.removeOne(item);

      assertEquals(0, item.stackSize());
    }

    /** G2: An identical item instance with {@code stackSize == 1} is removed from its slot. */
    @Test
    public void removeOneIdentityMatchClearsSlotWhenStackIsOneAssertThree() {
      InventoryComponent ic = new InventoryComponent(1);
      DummyItem item = new DummyItem(1, 10);
      ic.set(0, item);

      ic.removeOne(item);

      assertNull(ic.items()[0]);
    }

    /** G2: An identical item instance with {@code stackSize == 1} is removed from its slot. */
    @Test
    public void removeOneIdentityMatchClearsSlotWhenStackIsOneAssertFour() {
      InventoryComponent ic = new InventoryComponent(1);
      List<Item> removedItems = new ArrayList<>();
      ic.onItemRemoved(removedItems::add);
      DummyItem item = new DummyItem(1, 10);
      ic.set(0, item);

      ic.removeOne(item);

      assertEquals(List.of(item), removedItems);
    }

    /** G3: Without identity match, an equals-matching item is reduced. */
    @Test
    public void removeOneEqualsMatchWhenNoIdentityMatchExistsAssertOne() {
      InventoryComponent ic = new InventoryComponent(1);
      DummyItem storedItem = new DummyItem(3, 10);
      DummyItem equalItem = new DummyItem(3, 10);
      ic.set(0, storedItem);

      boolean removed = ic.removeOne(equalItem);

      assertTrue(removed);
    }

    /** G3: Without identity match, an equals-matching item is reduced. */
    @Test
    public void removeOneEqualsMatchWhenNoIdentityMatchExistsAssertTwo() {
      InventoryComponent ic = new InventoryComponent(1);
      DummyItem storedItem = new DummyItem(3, 10);
      DummyItem equalItem = new DummyItem(3, 10);
      ic.set(0, storedItem);

      ic.removeOne(equalItem);

      assertEquals(2, storedItem.stackSize());
    }

    /** G3: Without identity match, an equals-matching item is reduced. */
    @Test
    public void removeOneEqualsMatchWhenNoIdentityMatchExistsAssertThree() {
      InventoryComponent ic = new InventoryComponent(1);
      DummyItem storedItem = new DummyItem(3, 10);
      DummyItem equalItem = new DummyItem(3, 10);
      ic.set(0, storedItem);

      ic.removeOne(equalItem);

      assertSame(storedItem, ic.items()[0]);
    }

    /** G3: Without identity match, an equals-matching item is reduced. */
    @Test
    public void removeOneEqualsMatchWhenNoIdentityMatchExistsAssertFour() {
      InventoryComponent ic = new InventoryComponent(1);
      List<Item> removedItems = new ArrayList<>();
      ic.onItemRemoved(removedItems::add);
      DummyItem storedItem = new DummyItem(3, 10);
      DummyItem equalItem = new DummyItem(3, 10);
      ic.set(0, storedItem);

      ic.removeOne(equalItem);

      assertEquals(List.of(storedItem), removedItems);
    }

    /** U1: If no item matches, nothing is removed and no callback is called. */
    @Test
    public void removeOneNoMatchAssertOne() {
      InventoryComponent ic = new InventoryComponent(1);
      DummyItem storedItem = new DummyItem(3, 10);
      OtherDummyItem otherItem = new OtherDummyItem(3, 10);
      ic.set(0, storedItem);

      boolean removed = ic.removeOne(otherItem);

      assertFalse(removed);
    }

    /** U1: If no item matches, nothing is removed and no callback is called. */
    @Test
    public void removeOneNoMatchAssertTwo() {
      InventoryComponent ic = new InventoryComponent(1);
      DummyItem storedItem = new DummyItem(3, 10);
      OtherDummyItem otherItem = new OtherDummyItem(3, 10);
      ic.set(0, storedItem);

      ic.removeOne(otherItem);

      assertSame(storedItem, ic.items()[0]);
    }

    /** U1: If no item matches, nothing is removed and no callback is called. */
    @Test
    public void removeOneNoMatchAssertThree() {
      InventoryComponent ic = new InventoryComponent(1);
      DummyItem storedItem = new DummyItem(3, 10);
      OtherDummyItem otherItem = new OtherDummyItem(3, 10);
      ic.set(0, storedItem);

      ic.removeOne(otherItem);

      assertEquals(3, storedItem.stackSize());
    }

    /** U1: If no item matches, nothing is removed and no callback is called. */
    @Test
    public void removeOneNoMatchAssertFour() {
      InventoryComponent ic = new InventoryComponent(1);
      List<Item> removedItems = new ArrayList<>();
      ic.onItemRemoved(removedItems::add);
      DummyItem storedItem = new DummyItem(3, 10);
      OtherDummyItem otherItem = new OtherDummyItem(3, 10);
      ic.set(0, storedItem);

      ic.removeOne(otherItem);

      assertTrue(removedItems.isEmpty());
    }

    /**
     * U2: {@code null} with an empty slot matches the empty slot and fails while dereferencing it.
     */
    @Test
    public void removeOneNullWithFreeSlot() {
      InventoryComponent ic = new InventoryComponent(1);

      assertThrows(NullPointerException.class, () -> ic.removeOne(null));
    }

    /** U3: {@code null} with a full inventory has no match and returns {@code false}. */
    @Test
    public void removeOneNullWithFullInventoryAssertOne() {
      InventoryComponent ic = new InventoryComponent(1);
      DummyItem item = new DummyItem(1, 10);
      ic.set(0, item);

      boolean removed = ic.removeOne(null);

      assertFalse(removed);
    }

    /** U3: {@code null} with a full inventory has no match and returns {@code false}. */
    @Test
    public void removeOneNullWithFullInventoryAssertTwo() {
      InventoryComponent ic = new InventoryComponent(1);
      DummyItem item = new DummyItem(1, 10);
      ic.set(0, item);

      ic.removeOne(null);

      assertSame(item, ic.items()[0]);
    }

    /** U3: {@code null} with a full inventory has no match and returns {@code false}. */
    @Test
    public void removeOneNullWithFullInventoryAssertThree() {
      InventoryComponent ic = new InventoryComponent(1);
      DummyItem item = new DummyItem(1, 10);
      ic.set(0, item);

      ic.removeOne(null);

      assertEquals(1, item.stackSize());
    }

    /** U3: {@code null} with a full inventory has no match and returns {@code false}. */
    @Test
    public void removeOneNullWithFullInventoryAssertFour() {
      InventoryComponent ic = new InventoryComponent(1);
      List<Item> removedItems = new ArrayList<>();
      ic.onItemRemoved(removedItems::add);
      DummyItem item = new DummyItem(1, 10);
      ic.set(0, item);

      ic.removeOne(null);

      assertTrue(removedItems.isEmpty());
    }

    /** U4: An item with {@code stackSize == 0} causes the stack setter to reject {@code -1}. */
    @Test
    public void removeOneItemWithStackSizeZero() {
      InventoryComponent ic = new InventoryComponent(1);
      DummyItem item = new DummyItem(0, 10);
      ic.set(0, item);

      assertThrows(IllegalArgumentException.class, () -> ic.removeOne(item));
    }
  }

  /** Empty inventory should return an empty List. */
  @Test
  public void getAllItemsEmptyInventory() {
    Entity e = new Entity();
    InventoryComponent ic = new InventoryComponent(0);
    e.add(ic);
    assertEquals(0, ic.count());
  }

  /** An inventory with one Item should return a List with this Item. */
  @Test
  public void getAllItemsInventoryWithOnlyOneItem() {
    Entity e = new Entity();
    InventoryComponent ic = new InventoryComponent(1);
    e.add(ic);
    Item itemData = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
    ic.add(itemData);
    Item[] list = ic.items();
    assertEquals(1, list.length);
    assertTrue(Arrays.asList(list).contains(itemData));
  }

  /** An inventory with one Item should return a List with this Item. */
  @Test
  public void getAllItemsInventoryWithTwoItems() {
    Entity e = new Entity();
    InventoryComponent ic = new InventoryComponent(2);
    e.add(ic);
    Item itemData1 = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
    ic.add(itemData1);
    Item itemData2 = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
    ic.add(itemData2);
    Item[] list = ic.items();
    assertEquals(2, list.length);
    assertTrue(Arrays.asList(list).contains(itemData1));
    assertTrue(Arrays.asList(list).contains(itemData2));
  }

  /** An inventory should only be able to return Items it contains. */
  @Test
  public void getAllItemsInventoryNoAddedItemButCreated() {
    Entity e = new Entity();
    InventoryComponent ic = new InventoryComponent(1);
    e.add(ic);
    Item itemData = new Item("Test item", "Test description", new Animation(MISSING_TEXTURE));
    Item[] list = ic.items();
    assertEquals(0, ic.count());
    assertFalse(Arrays.asList(list).contains(itemData));
  }

  /** WTF? . */
  @Test
  public void tranfserItem() {
    InventoryComponent ic = new InventoryComponent(1);
    InventoryComponent other = new InventoryComponent(1);
    Item item = mock(Item.class);
    when(item.maxStackSize()).thenReturn(1);
    when(item.stackSize()).thenReturn(1);
    ic.add(item);
    assertTrue(Arrays.asList(ic.items()).contains(item));
    assertTrue(ic.transfer(item, other));
    assertTrue(Arrays.asList(other.items()).contains(item));
    assertFalse(Arrays.asList(ic.items()).contains(item));
  }

  /** WTF? . */
  @Test
  public void tranfserItemNoSpace() {
    InventoryComponent ic = new InventoryComponent(1);
    InventoryComponent other = new InventoryComponent(0);
    Item item = mock(Item.class);
    when(item.maxStackSize()).thenReturn(1);
    when(item.stackSize()).thenReturn(1);
    ic.add(item);
    assertTrue(Arrays.asList(ic.items()).contains(item));
    assertFalse(ic.transfer(item, other));
    assertFalse(Arrays.asList(other.items()).contains(item));
    assertTrue(Arrays.asList(ic.items()).contains(item));
  }

  /** WTF? . */
  @Test
  public void tranfserItemNoItem() {
    InventoryComponent ic = new InventoryComponent(1);
    InventoryComponent other = new InventoryComponent(1);
    Item item = mock(Item.class);
    assertFalse(ic.transfer(item, other));
  }

  /** WTF? . */
  @Test
  public void transferItemToItself() {
    InventoryComponent ic = new InventoryComponent(1);
    Item item = mock(Item.class);
    when(item.maxStackSize()).thenReturn(1);
    when(item.stackSize()).thenReturn(1);
    ic.add(item);
    assertTrue(Arrays.asList(ic.items()).contains(item));
    assertFalse(ic.transfer(item, ic));
    assertTrue(Arrays.asList(ic.items()).contains(item));
  }

  /**
   * Tests adding a stack of items when no item of the same class exists in the inventory yet.
   *
   * <p>Expected behavior: - A new stack is created in the inventory. - The new stack has the same
   * stack size as the incoming item. - The method returns true.
   */
  @Test
  public void addStack_NoExistingItem() {
    InventoryComponent ic = new InventoryComponent(5);

    Item item = mock(Item.class);
    when(item.maxStackSize()).thenReturn(5);
    when(item.stackSize()).thenReturn(3);

    boolean result = ic.add(item);

    assertTrue(result);
    assertEquals(1, ic.count());

    // Verify that the inventory contains the item and it has the correct stack size
    Set<Item> storedItems = ic.items(item.getClass());
    assertEquals(1, storedItems.size());
    assertEquals(3, storedItems.iterator().next().stackSize());
  }

  /**
   * Tests adding a stack of items to an inventory where an existing stack of the same item type
   * exists and has enough space to merge completely.
   *
   * <p>Expected behavior: - The incoming stack is fully merged into the existing stack. - No new
   * stack is created. - The existing stack's size equals its maxStackSize after merge. - The
   * incoming stack size becomes zero (emptied). - The method returns true.
   */
  @Test
  public void addStack_MergeCompletely() {
    InventoryComponent ic = new InventoryComponent(5);

    DummyItem existing = new DummyItem(2, 5);
    ic.add(existing);

    DummyItem incoming = new DummyItem(3, 5);

    boolean result = ic.add(incoming);

    assertTrue(result);
    assertEquals(1, ic.count());
    assertEquals(5, existing.stackSize()); // Full stack after merge
    assertEquals(0, incoming.stackSize()); // Incoming stack emptied
  }

  /**
   * Tests adding a stack of items when multiple existing stacks of the same item type exist, each
   * partially filled, and the incoming stack needs to be split across them.
   *
   * <p>Additionally, when the incoming stack does not fit completely into existing stacks, the
   * remainder is added as a new stack in the inventory.
   *
   * <p>Expected behavior: - Existing stacks are filled up to their maxStackSize. - Remaining items
   * form a new stack if remainder > 0. - Total stack count increases accordingly. - The method
   * returns true. - The incoming item's stackSize is reduced accordingly and should be 0 after full
   * merge.
   */
  @Test
  public void addStack_SplitAcrossMultipleStacks() {
    InventoryComponent ic = new InventoryComponent(5);

    DummyItem stack1 = new DummyItem(5, 5);
    DummyItem stack2 = new DummyItem(2, 5);
    ic.add(stack1);
    ic.add(stack2);
    stack1.stackSize(1);

    assertEquals(2, ic.count());

    DummyItem incoming = new DummyItem(5, 5);
    boolean result = ic.add(incoming);
    assertTrue(result);

    assertEquals(2, ic.count());
    Set<Item> storedItems = ic.items(DummyItem.class);
    assertEquals(2, storedItems.size());

    assertEquals(5, stack1.stackSize());
    assertEquals(3, stack2.stackSize());
    assertEquals(0, incoming.stackSize());
  }

  /**
   * Tests adding an incoming stack partially merging into an existing stack, with the remainder
   * added as a new stack in the inventory.
   *
   * <p>Expected behavior: - Existing stack is filled up to maxStackSize. - Remaining items form a
   * new stack. - Inventory count increases accordingly. - The method returns true.
   */
  @Test
  public void addStack_PartialMerge_NewStackForRest() {
    InventoryComponent ic = new InventoryComponent(5);

    DummyItem stack1 = new DummyItem(4, 5);
    ic.add(stack1);

    DummyItem incoming = new DummyItem(3, 5);
    boolean result = ic.add(incoming);

    assertTrue(result);
    assertEquals(2, ic.count()); // stack1 voll, Rest als neuer Stack

    assertEquals(5, stack1.stackSize()); // stack1 wurde aufgefüllt
    assertEquals(2, incoming.stackSize()); // restliche 2 Items verbleiben im incoming
  }

  /**
   * Tests adding an incoming stack when there is partial space in existing stack, but no space left
   * for the remaining items.
   *
   * <p>Expected behavior: - Existing stack is filled up to maxStackSize. - No new stack can be
   * added due to full inventory. - The method returns false.
   */
  @Test
  public void addStack_NoSpaceForRest() {
    InventoryComponent ic = new InventoryComponent(1); // only 1 slot

    DummyItem stack1 = new DummyItem(4, 5);
    ic.add(stack1);

    DummyItem incoming = new DummyItem(3, 5);
    boolean result = ic.add(incoming);

    assertFalse(result); // no space for remainder
    assertEquals(5, stack1.stackSize()); // existing stack filled up
    assertEquals(2, incoming.stackSize()); // 2 items merged, 1 remains
  }

  private static class DummyItem extends Item {
    public DummyItem(int stackSize, int maxStackSize) {
      super("Dummy item", "Dummy description", null, null, stackSize, maxStackSize);
    }
  }

  private static class OtherDummyItem extends Item {
    public OtherDummyItem(int stackSize, int maxStackSize) {
      super("Other dummy item", "Other dummy description", null, null, stackSize, maxStackSize);
    }
  }

  private static class CapturingHandler extends Handler {
    private final List<LogRecord> records = new ArrayList<>();

    @Override
    public void publish(LogRecord record) {
      if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
        records.add(record);
      }
    }

    @Override
    public void flush() {}

    @Override
    public void close() {}

    List<LogRecord> records() {
      return records;
    }
  }
}
