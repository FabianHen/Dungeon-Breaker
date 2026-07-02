package contrib.item.concreteItem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import contrib.components.InventoryComponent;
import core.Entity;
import core.Game;
import core.components.PositionComponent;
import core.level.DungeonLevel;
import core.level.utils.DesignLabel;
import core.level.utils.LevelElement;
import core.systems.LevelSystem;
import core.utils.Point;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for {@link ItemHammer}. */
public class ItemHammerTest {

  /** Sets up a 5x5 floor level and the level system before each test. */
  @BeforeEach
  public void before() {
    Game.add(new LevelSystem());

    DungeonLevel level =
        new DungeonLevel(
            new LevelElement[][] {
              new LevelElement[] {
                LevelElement.FLOOR,
                LevelElement.FLOOR,
                LevelElement.FLOOR,
                LevelElement.FLOOR,
                LevelElement.FLOOR
              },
              new LevelElement[] {
                LevelElement.FLOOR,
                LevelElement.FLOOR,
                LevelElement.FLOOR,
                LevelElement.FLOOR,
                LevelElement.FLOOR
              },
              new LevelElement[] {
                LevelElement.FLOOR,
                LevelElement.FLOOR,
                LevelElement.FLOOR,
                LevelElement.FLOOR,
                LevelElement.FLOOR
              },
              new LevelElement[] {
                LevelElement.FLOOR,
                LevelElement.FLOOR,
                LevelElement.FLOOR,
                LevelElement.FLOOR,
                LevelElement.FLOOR
              },
              new LevelElement[] {
                LevelElement.FLOOR,
                LevelElement.FLOOR,
                LevelElement.FLOOR,
                LevelElement.FLOOR,
                LevelElement.FLOOR
              }
            },
            DesignLabel.DEFAULT);
    Game.currentLevel(level);
  }

  /** Removes all entities and systems and clears the level after each test. */
  @AfterEach
  public void cleanup() {
    Game.removeAllEntities();
    Game.removeAllSystems();
    Game.currentLevel(null);
  }

  /** Tests that the constructor sets name, description and texture correctly. */
  @Test
  public void constructor_setsNameDescriptionAndTexture() {
    ItemHammer hammer = new ItemHammer();

    assertEquals("Hammer", hammer.displayName());
    assertEquals("A powerful tool to destroy objects", hammer.description());
    assertEquals("items/tools/hammer.png", ItemHammer.DEFAULT_TEXTURE.pathString());
  }

  /** Tests that using the hammer with a position and inventory drops it and removes it. */
  @Test
  public void use_withPositionAndInventory_dropsAndRemovesFromInventory() {
    ItemHammer hammer = new ItemHammer();
    Entity user = new Entity();
    user.add(new PositionComponent(new Point(3, 3)));
    InventoryComponent inventoryComponent = new InventoryComponent(1);
    user.add(inventoryComponent);
    inventoryComponent.add(hammer);

    long countBefore = Game.levelEntities().count();
    hammer.use(user);

    assertFalse(inventoryComponent.hasItem(hammer));
    assertEquals(countBefore + 1, Game.levelEntities().count());
  }

  /** Tests that using the hammer with a position but no inventory drops it without exceptions. */
  @Test
  public void use_withPositionNoInventory_dropsNoException() {
    ItemHammer hammer = new ItemHammer();
    Entity user = new Entity();
    user.add(new PositionComponent(new Point(3, 3)));

    long countBefore = Game.levelEntities().count();
    hammer.use(user);

    assertEquals(countBefore + 1, Game.levelEntities().count());
  }

  /** Tests that using the hammer without a position does nothing. */
  @Test
  public void use_withoutPosition_doesNothing() {
    ItemHammer hammer = new ItemHammer();
    Entity user = new Entity();
    InventoryComponent inventoryComponent = new InventoryComponent(1);
    user.add(inventoryComponent);
    inventoryComponent.add(hammer);

    long countBefore = Game.levelEntities().count();
    hammer.use(user);

    assertEquals(countBefore, Game.levelEntities().count());
    assertTrue(inventoryComponent.hasItem(hammer));
  }

  /** Tests that using the hammer with a null user throws a NullPointerException. */
  @Test
  public void use_nullUser_throwsNPE() {
    ItemHammer hammer = new ItemHammer();

    assertThrows(NullPointerException.class, () -> hammer.use(null));
  }
}
