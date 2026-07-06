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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ItemPotionWater}: constructing the potion and using it.
 */
public class ItemPotionWaterTest {

  /** The fixed amount the water potion heals for, mirroring the constant in the item. */
  private static final int HEAL_AMOUNT = 5;

  @Test
  @DisplayName("constructor sets the fixed name, description and animation path")
  public void constructorInitializesFixedValues() {
    ItemPotionWater potion = new ItemPotionWater();

    assertEquals("Bottle of Water", potion.displayName());
    assertEquals(
        "A bottle of water. It's not very useful except for hydration. It heals you for "
            + HEAL_AMOUNT
            + " health points.",
        potion.description());
    assertEquals(
        Optional.of("items/potion/water_bottle.png"),
        potion.inventoryAnimation().sourcePath().map(IPath::pathString));
  }

  @Test
  @DisplayName("use() removes the potion and queues a -5 HEAL hit when inventory and health exist")
  public void useWithInventoryAndHealthRemovesAndHeals() {
    ItemPotionWater potion = new ItemPotionWater();
    InventoryComponent inventory = new InventoryComponent(1);
    inventory.add(potion);
    HealthComponent health = new HealthComponent(50);
    Entity user = new Entity();
    user.add(inventory);
    user.add(health);

    potion.use(user);

    assertFalse(inventory.hasItem(potion), "potion should be removed from the inventory");
    assertEquals(
        -HEAL_AMOUNT,
        health.calculateDamageOf(DamageType.HEAL),
        "a single HEAL hit of -5 should have been queued");
  }

  @Test
  @DisplayName("use() still removes the potion without error when the entity has no health component")
  public void useWithInventoryNoHealthRemovesWithoutError() {
    ItemPotionWater potion = new ItemPotionWater();
    InventoryComponent inventory = new InventoryComponent(1);
    inventory.add(potion);
    Entity user = new Entity();
    user.add(inventory);

    assertDoesNotThrow(() -> potion.use(user));

    assertFalse(inventory.hasItem(potion), "the last exemplar should still be removed even without healing");
  }

  @Test
  @DisplayName("use() does nothing and queues no healing when the entity has no inventory")
  public void useWithoutInventoryDoesNothing() {
    ItemPotionWater potion = new ItemPotionWater();
    HealthComponent health = new HealthComponent(50);
    Entity user = new Entity();
    // Health is present but must never be touched: the outer inventory guard skips everything.
    user.add(health);

    assertDoesNotThrow(() -> potion.use(user));

    assertEquals(
        0,
        health.calculateDamageOf(DamageType.HEAL),
        "no healing may occur when the entity has no inventory");
  }

  @Test
  @DisplayName("use() throws a NullPointerException when the entity is null")
  public void useWithNullEntityThrows() {
    ItemPotionWater potion = new ItemPotionWater();

    assertThrows(NullPointerException.class, () -> potion.use(null));
  }
}
