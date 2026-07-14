package contrib.item.concreteItem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import contrib.components.InventoryComponent;
import contrib.entities.EntityFactory;
import core.Entity;
import core.Game;
import core.components.PositionComponent;
import core.level.DungeonLevel;
import core.level.Tile;
import core.level.utils.DesignLabel;
import core.level.utils.LevelElement;
import core.systems.LevelSystem;
import core.utils.Point;
import core.utils.components.path.IPath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/**
 * Tests for {@link ItemResourceEgg}: constructing the egg and using it.
 */
public class ItemResourceEggTest {

  private static final Point USER_POSITION = new Point(2, 2);

  /** Sets up a 5x5 floor level and the level system before each test. */
  @BeforeEach
  public void before() {
    Game.add(new LevelSystem());

    DungeonLevel level =
        new DungeonLevel(
            new LevelElement[][] {
              new LevelElement[] {
                LevelElement.FLOOR, LevelElement.FLOOR, LevelElement.FLOOR, LevelElement.FLOOR,
                LevelElement.FLOOR
              },
              new LevelElement[] {
                LevelElement.FLOOR, LevelElement.FLOOR, LevelElement.FLOOR, LevelElement.FLOOR,
                LevelElement.FLOOR
              },
              new LevelElement[] {
                LevelElement.FLOOR, LevelElement.FLOOR, LevelElement.FLOOR, LevelElement.FLOOR,
                LevelElement.FLOOR
              },
              new LevelElement[] {
                LevelElement.FLOOR, LevelElement.FLOOR, LevelElement.FLOOR, LevelElement.FLOOR,
                LevelElement.FLOOR
              },
              new LevelElement[] {
                LevelElement.FLOOR, LevelElement.FLOOR, LevelElement.FLOOR, LevelElement.FLOOR,
                LevelElement.FLOOR
              }
            },
            DesignLabel.DEFAULT);

    for (Tile t : new ArrayList<>(level.exitTiles())) {
      level.changeTileElementType(t, LevelElement.FLOOR);
    }
    Game.currentLevel(level);
  }

  /** Removes all entities and systems and clears the level after each test. */
  @AfterEach
  public void cleanup() {
    Game.removeAllEntities();
    Game.removeAllSystems();
    Game.currentLevel(null);
  }

  /** Builds a user entity with an inventory holding the egg and a valid position. */
  private Entity userWithInventoryAndPosition(ItemResourceEgg egg) {
    Entity user = new Entity();
    InventoryComponent inventory = new InventoryComponent(1);
    inventory.add(egg);
    user.add(inventory);
    user.add(new PositionComponent(USER_POSITION));
    return user;
  }

  /** Builds a monster entity that carries a position component. */
  private Entity monsterWithPosition() {
    Entity monster = new Entity();
    monster.add(new PositionComponent(new Point(0, 0)));
    return monster;
  }

  /** The constructor sets the fixed display name. */
  @Test
  public void constructorSetsDisplayName() {
    assertEquals("Egg", new ItemResourceEgg().displayName());
  }

  /** The constructor sets the fixed description. */
  @Test
  public void constructorSetsDescription() {
    assertEquals(
        "An egg. What was there before? The chicken or the egg?",
        new ItemResourceEgg().description());
  }

  /** The constructor sets the fixed animation path. */
  @Test
  public void constructorSetsAnimationPath() {
    assertEquals(
        Optional.of("items/resource/egg.png"),
        new ItemResourceEgg().inventoryAnimation().sourcePath().map(IPath::pathString));
  }

  /** use() removes one egg from the inventory. */
  @Test
  public void useRemovesEggFromInventory() {
    ItemResourceEgg egg = new ItemResourceEgg();
    Entity user = userWithInventoryAndPosition(egg);
    InventoryComponent inventory = user.fetch(InventoryComponent.class).orElseThrow();

    try (MockedStatic<EntityFactory> factory = mockStatic(EntityFactory.class)) {
      factory.when(EntityFactory::randomMonster).thenReturn(monsterWithPosition());
      egg.use(user);
    }

    assertFalse(inventory.hasItem(egg), "the egg should be removed from the inventory");
  }

  /** use() sets the spawned monster's position to the user's position. */
  @Test
  public void useSetsMonsterPositionToUserPosition() {
    ItemResourceEgg egg = new ItemResourceEgg();
    Entity user = userWithInventoryAndPosition(egg);
    Entity monster = monsterWithPosition();

    try (MockedStatic<EntityFactory> factory = mockStatic(EntityFactory.class)) {
      factory.when(EntityFactory::randomMonster).thenReturn(monster);
      egg.use(user);
    }

    assertEquals(
        USER_POSITION,
        monster.fetch(PositionComponent.class).orElseThrow().position(),
        "the monster should be placed at the user's position");
  }

  /** use() adds the spawned monster to the game. */
  @Test
  public void useAddsMonsterToGame() {
    ItemResourceEgg egg = new ItemResourceEgg();
    Entity user = userWithInventoryAndPosition(egg);
    Entity monster = monsterWithPosition();

    try (MockedStatic<EntityFactory> factory = mockStatic(EntityFactory.class)) {
      factory.when(EntityFactory::randomMonster).thenReturn(monster);
      egg.use(user);
    }

    assertTrue(
        Game.levelEntities().anyMatch(entity -> entity == monster),
        "the spawned monster should be added to the game");
  }

  // --- use(): entity without inventory ---

  /** use() spawns no monster when the entity has no inventory. */
  @Test
  public void useWithoutInventoryDoesNotSpawnMonster() {
    ItemResourceEgg egg = new ItemResourceEgg();
    Entity user = new Entity();
    user.add(new PositionComponent(USER_POSITION));
    long before = Game.levelEntities().count();

    egg.use(user);

    assertEquals(
        before, Game.levelEntities().count(), "no monster should be spawned without an inventory");
  }

  // --- use(): null entity ---

  /** use() throws a NullPointerException when the entity is null. */
  @Test
  public void useWithNullEntityThrows() {
    ItemResourceEgg egg = new ItemResourceEgg();

    assertThrows(NullPointerException.class, () -> egg.use(null));
  }

  // --- use(): user without a position component ---

  /** use() throws a NoSuchElementException when the user has no position component. */
  @Test
  public void useWithoutUserPositionThrows() {
    ItemResourceEgg egg = new ItemResourceEgg();
    Entity user = new Entity();
    InventoryComponent inventory = new InventoryComponent(1);
    inventory.add(egg);
    user.add(inventory);

    try (MockedStatic<EntityFactory> factory = mockStatic(EntityFactory.class)) {
      factory.when(EntityFactory::randomMonster).thenReturn(monsterWithPosition());
      assertThrows(NoSuchElementException.class, () -> egg.use(user));
    }
  }

  /** use() removes the egg before the missing user position makes it fail. */
  @Test
  public void useWithoutUserPositionStillRemovesEgg() {
    ItemResourceEgg egg = new ItemResourceEgg();
    Entity user = new Entity();
    InventoryComponent inventory = new InventoryComponent(1);
    inventory.add(egg);
    user.add(inventory);

    try (MockedStatic<EntityFactory> factory = mockStatic(EntityFactory.class)) {
      factory.when(EntityFactory::randomMonster).thenReturn(monsterWithPosition());
      try {
        egg.use(user);
      } catch (NoSuchElementException expected) {
        // use() fails on the position lookup after removing the egg; removal is asserted below.
      }
    }

    assertFalse(inventory.hasItem(egg), "the egg should be removed before the position lookup fails");
  }

  /** use() wraps a monster-creation IOException in a RuntimeException. */
  @Test
  public void useWrapsMonsterCreationIoErrorInRuntimeException() {
    ItemResourceEgg egg = new ItemResourceEgg();
    Entity user = userWithInventoryAndPosition(egg);
    RuntimeException thrown = null;

    try (MockedStatic<EntityFactory> factory = mockStatic(EntityFactory.class)) {
      factory.when(EntityFactory::randomMonster).thenThrow(new IOException("boom"));
      try {
        egg.use(user);
      } catch (RuntimeException e) {
        thrown = e;
      }
    }

    assertInstanceOf(
        IOException.class, thrown.getCause(), "the IOException should be wrapped as the cause");
  }

  /** use() removes the egg before a monster-creation IOException makes it fail. */
  @Test
  public void useWithMonsterCreationErrorStillRemovesEgg() {
    ItemResourceEgg egg = new ItemResourceEgg();
    Entity user = userWithInventoryAndPosition(egg);
    InventoryComponent inventory = user.fetch(InventoryComponent.class).orElseThrow();

    try (MockedStatic<EntityFactory> factory = mockStatic(EntityFactory.class)) {
      factory.when(EntityFactory::randomMonster).thenThrow(new IOException("boom"));
      try {
        egg.use(user);
      } catch (RuntimeException expected) {
        // use() wraps and rethrows after removing the egg; removal is asserted below.
      }
    }

    assertFalse(inventory.hasItem(egg), "the egg should be removed before monster creation fails");
  }

  /** use() throws a NoSuchElementException when the generated monster has no position. */
  @Test
  public void useWithMonsterWithoutPositionThrows() {
    ItemResourceEgg egg = new ItemResourceEgg();
    Entity user = userWithInventoryAndPosition(egg);

    try (MockedStatic<EntityFactory> factory = mockStatic(EntityFactory.class)) {
      factory.when(EntityFactory::randomMonster).thenReturn(new Entity());
      assertThrows(NoSuchElementException.class, () -> egg.use(user));
    }
  }

  /** use() removes the egg before the positionless monster makes it fail. */
  @Test
  public void useWithMonsterWithoutPositionStillRemovesEgg() {
    ItemResourceEgg egg = new ItemResourceEgg();
    Entity user = userWithInventoryAndPosition(egg);
    InventoryComponent inventory = user.fetch(InventoryComponent.class).orElseThrow();

    try (MockedStatic<EntityFactory> factory = mockStatic(EntityFactory.class)) {
      factory.when(EntityFactory::randomMonster).thenReturn(new Entity());
      try {
        egg.use(user);
      } catch (NoSuchElementException expected) {
        // use() fails on the monster's position lookup after removing the egg; asserted below.
      }
    }

    assertFalse(inventory.hasItem(egg), "the egg should be removed before the monster lookup fails");
  }
}
