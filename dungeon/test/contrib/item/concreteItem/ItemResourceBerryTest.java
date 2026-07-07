package contrib.item.concreteItem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import contrib.components.HealthComponent;
import contrib.components.InventoryComponent;
import contrib.utils.components.health.DamageType;
import core.Entity;
import org.junit.jupiter.api.Test;

class ItemResourceBerryTest {

  @Test
  void constructor_ShouldCreateBerryWithCorrectValues() {
    ItemResourceBerry berry = new ItemResourceBerry();

    assertEquals("Berry", berry.displayName());
    assertEquals("A berry.", berry.description());
    assertEquals(
      "items/resource/berry.png",
      berry.inventoryAnimation().sourcePath().orElseThrow().pathString());
    assertEquals(
      "items/resource/berry.png",
      berry.worldAnimation().sourcePath().orElseThrow().pathString());
  }

  @Test
  void use_ShouldRemoveBerryAndHealEntityWithInventoryAndHealthComponent() {
    Entity entity = new Entity();
    InventoryComponent inventory = new InventoryComponent();
    HealthComponent health = new HealthComponent(50);
    ItemResourceBerry berry = new ItemResourceBerry();

    inventory.add(berry);
    entity.add(inventory);
    entity.add(health);

    berry.use(entity);

    assertEquals(0, inventory.count());
    assertEquals(-5, health.calculateDamageOf(DamageType.HEAL));
  }

  @Test
  void use_ShouldRemoveBerryWithoutHealingWhenHealthComponentIsMissing() {
    Entity entity = new Entity();
    InventoryComponent inventory = new InventoryComponent();
    ItemResourceBerry berry = new ItemResourceBerry();

    inventory.add(berry);
    entity.add(inventory);

    berry.use(entity);

    assertEquals(0, inventory.count());
  }

  @Test
  void use_ShouldNotHealWhenInventoryComponentIsMissing() {
    Entity entity = new Entity();
    HealthComponent health = new HealthComponent(50);
    ItemResourceBerry berry = new ItemResourceBerry();

    entity.add(health);

    berry.use(entity);

    assertEquals(0, health.calculateDamageOf(DamageType.HEAL));
  }

  @Test
  void use_ShouldThrowNullPointerExceptionForNullEntity() {
    ItemResourceBerry berry = new ItemResourceBerry();

    assertThrows(NullPointerException.class, () -> berry.use(null));
  }
}
