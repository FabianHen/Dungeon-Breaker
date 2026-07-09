package contrib.item;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import core.utils.components.draw.animation.Animation;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Tests */
public class ItemRegistryTest {

  /** Tests that looking up an existing item id returns the registered class. */
  @Test
  void lookup_withExistingId_shouldReturnClass() {
    ItemRegistry.register("TestID", Item.class);

    Class<?> result = ItemRegistry.lookup("TestID").orElseThrow();

    assertEquals(Item.class, result);
  }

  /** Tests that looking up an unknown item id returns an empty result. */
  @Test
  void lookup_withMissingId_shouldReturnEmpty() {
    var result = ItemRegistry.lookup("missing");

    assertTrue(result.isEmpty());
  }

  /** Tests that looking up an item with a null id throws an IllegalArgumentException. */
  @Test
  void lookup_withNullId_shouldThrowException() {
    assertThrows(IllegalArgumentException.class, () -> ItemRegistry.lookup(null));
  }

  /** Tests that looking up an item with a blank id throws an IllegalArgumentException. */
  @Test
  void lookup_withBlankId_shouldThrowException() {
    assertThrows(IllegalArgumentException.class, () -> ItemRegistry.lookup("   "));
  }

  /** Tests that registering an item with a valid id stores the item class correctly. */
  @Test
  void register_withValidId_shouldRegisterClass() {
    String id = "TestID";

    ItemRegistry.register(id, Item.class);

    assertTrue(ItemRegistry.lookup(id).isPresent());
  }

  /** Tests that registering an item with a null class throws a NullPointerException. */
  @Test
  void register_withNullClass_shouldThrowException() {
    assertThrows(NullPointerException.class, () -> ItemRegistry.register("TestID", null));
  }

  /** Tests that registering an item with a null id throws an IllegalArgumentException. */
  @Test
  void register_withNullId_shouldThrowException() {
    assertThrows(IllegalArgumentException.class, () -> ItemRegistry.register(null, Item.class));
  }

  /**
   * Tests that registering an item class without an explicit id uses the class name as the registry
   * id.
   */
  @Test
  void register_withClassOnly_shouldUseSimpleName() {
    ItemRegistry.register(Item.class);

    assertTrue(ItemRegistry.lookup("Item").isPresent());
  }

  /**
   * Tests that registering the same class with the same id multiple times does not throw an
   * exception.
   */
  @Test
  void register_sameClassTwice_shouldNotThrow() {
    ItemRegistry.register("ID", Item.class);

    assertDoesNotThrow(() -> ItemRegistry.register("ID", Item.class));
  }

  /** Tests that registering an item with a factory allows creating a new item instance. */
  @Test
  void register_withFactory_shouldCreateItem() {
    ItemRegistry.register(
        "ID", Item.class, data -> new Item("Test", "Description", (Animation) null));

    var result = ItemRegistry.create("ID", Map.of());

    assertTrue(result.isPresent());
  }

  /** Tests that registering a factory for an already registered item allows item creation. */
  @Test
  void registerFactory_shouldRegisterFactory() {
    ItemRegistry.register("ID", Item.class);

    ItemRegistry.registerFactory("ID", data -> new Item("Test", "Description", (Animation) null));

    var result = ItemRegistry.create("ID", Map.of());

    assertTrue(result.isPresent());
  }

  /** Tests that registering a null factory throws a NullPointerException. */
  @Test
  void registerFactory_withNullFactory_shouldThrow() {
    assertThrows(NullPointerException.class, () -> ItemRegistry.registerFactory("ID", null));
  }

  /** Tests that creating an item without a registered factory returns an empty result. */
  @Test
  void create_withoutFactory_shouldReturnEmpty() {
    ItemRegistry.register("ID", Item.class);

    var result = ItemRegistry.create("ID", Map.of());

    assertTrue(result.isEmpty());
  }

  /** Tests that creating an item with null creation data works when a factory is registered. */
  @Test
  void create_withNullData_shouldWork() {
    ItemRegistry.register(
        "ID", Item.class, data -> new Item("Test", "Description", (Animation) null));

    var result = ItemRegistry.create("ID", null);

    assertTrue(result.isPresent());
  }

  /** Tests that creating an item fails when the registered factory returns null. */
  @Test
  void create_factoryReturningNull_shouldThrow() {
    ItemRegistry.register("ID", Item.class, data -> null);

    assertThrows(IllegalStateException.class, () -> ItemRegistry.create("ID", Map.of()));
  }

  /** Tests that the entries method contains all registered item ids and classes. */
  @Test
  void entries_shouldContainRegisteredItem() {
    ItemRegistry.register("ID", Item.class);

    var entries = ItemRegistry.entries();

    assertEquals(Item.class, entries.get("ID"));
  }

  /** Tests that retrieving the id of a registered class returns the correct id. */
  @Test
  void idForClass_shouldReturnId() {
    ItemRegistry.register("ID", Item.class);

    var result = ItemRegistry.idFor(Item.class).orElseThrow();

    assertEquals("ID", result);
  }

  /** Tests that retrieving the id of an unknown class returns an empty result. */
  @Test
  void idForUnknownClass_shouldReturnEmpty() {
    var result = ItemRegistry.idFor(Item.class);

    assertTrue(result.isEmpty());
  }

  /** Tests that retrieving the id of a registered item instance returns the correct id. */
  @Test
  void idForItem_shouldReturnId() {
    ItemRegistry.register("ID", Item.class);
    Item item = new Item("Test", "Description", (Animation) null);

    String result = ItemRegistry.idFor(item);

    assertEquals("ID", result);
  }

  /** Tests that retrieving the id of an unregistered item instance throws an exception. */
  @Test
  void idForUnregisteredItem_shouldThrow() {
    Item item = new Item("Test", "Description", (Animation) null);

    assertThrows(IllegalArgumentException.class, () -> ItemRegistry.idFor(item));
  }

  /** Tests that checking a registered class returns true. */
  @Test
  void isRegistered_existingClass_shouldReturnTrue() {
    ItemRegistry.register("ID", Item.class);

    boolean result = ItemRegistry.isRegistered(Item.class);

    assertTrue(result);
  }

  /** Tests that checking an unregistered class returns false. */
  @Test
  void isRegistered_missingClass_shouldReturnFalse() {
    boolean result = ItemRegistry.isRegistered(Item.class);

    assertFalse(result);
  }
}
