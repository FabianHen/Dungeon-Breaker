package contrib.item.concreteItem;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import contrib.components.HealthComponent;
import contrib.components.InventoryComponent;
import contrib.utils.components.health.DamageType;
import core.Entity;
import core.utils.components.path.IPath;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ItemResourceMushroomRed}: constructing the mushroom and using it.
 */
public class ItemResourceMushroomRedTest {

  /** The fixed poison damage dealt on use, mirroring the constant in the item. */
  private static final int DAMAGE_AMOUNT = 20;

  /** The constructor sets the fixed name, description and animation path. */
  @Test
  public void constructorInitializesFixedValues() {
    ItemResourceMushroomRed mushroom = new ItemResourceMushroomRed();

    assertEquals("Red Mushroom", mushroom.displayName());
    assertEquals("A red mushroom.", mushroom.description());
    assertEquals(
        Optional.of("items/resource/mushroom_red.png"),
        mushroom.inventoryAnimation().sourcePath().map(IPath::pathString));
  }

  /** use() removes the mushroom and queues a POISON hit of 20 when inventory and health exist. */
  @Test
  public void useWithInventoryAndHealthRemovesAndDamages() {
    ItemResourceMushroomRed mushroom = new ItemResourceMushroomRed();
    InventoryComponent inventory = new InventoryComponent(1);
    inventory.add(mushroom);
    HealthComponent health = new HealthComponent(50);
    Entity user = new Entity();
    user.add(inventory);
    user.add(health);

    mushroom.use(user);

    assertFalse(inventory.hasItem(mushroom), "mushroom should be removed from the inventory");
    assertEquals(
        DAMAGE_AMOUNT,
        health.calculateDamageOf(DamageType.POISON),
        "a single POISON hit of 20 should have been queued");
  }

  /** use() still removes the mushroom without error when the entity has no health component. */
  @Test
  public void useWithInventoryNoHealthRemovesWithoutError() {
    ItemResourceMushroomRed mushroom = new ItemResourceMushroomRed();
    InventoryComponent inventory = new InventoryComponent(1);
    inventory.add(mushroom);
    Entity user = new Entity();
    user.add(inventory);

    assertDoesNotThrow(() -> mushroom.use(user));

    assertFalse(
        inventory.hasItem(mushroom),
        "the last exemplar should still be removed even without damage");
  }

  /** use() does nothing and queues no damage when the entity has no inventory. */
  @Test
  public void useWithoutInventoryDoesNothing() {
    ItemResourceMushroomRed mushroom = new ItemResourceMushroomRed();
    HealthComponent health = new HealthComponent(50);
    Entity user = new Entity();
    // Health is present but must never be touched: the outer inventory guard skips everything.
    user.add(health);

    assertDoesNotThrow(() -> mushroom.use(user));

    assertEquals(
        0,
        health.calculateDamageOf(DamageType.POISON),
        "no damage may occur when the entity has no inventory");
  }

  /** use() throws a NullPointerException when the entity is null. */
  @Test
  public void useWithNullEntityThrows() {
    ItemResourceMushroomRed mushroom = new ItemResourceMushroomRed();

    assertThrows(NullPointerException.class, () -> mushroom.use(null));
  }
}
