package contrib.item.concreteItem;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import contrib.components.CollideComponent;
import contrib.components.HealthComponent;
import core.Entity;
import core.Game;
import core.components.PlayerComponent;
import core.level.DungeonLevel;
import core.level.Tile;
import core.level.utils.DesignLabel;
import core.level.utils.LevelElement;
import core.systems.LevelSystem;
import core.utils.Direction;
import core.utils.Point;
import core.utils.components.path.IPath;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for {@link ItemHeart}. */
public class ItemHeartTest {

  private static final String HEART_TEXTURE_PATH = "items/pickups/heart_pickup.png";
  private static final String DATA_KEY_HEAL_AMOUNT = "heal_amount";

  private DungeonLevel level;

  /** Sets up a 5x5 floor level and the level system before each test. */
  @BeforeEach
  public void before() {
    Game.add(new LevelSystem());

    level =
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

  /**
   * Registers a local player entity so that {@link Game#player()} returns it.
   *
   * @return the registered player entity.
   */
  private Entity registerPlayer() {
    Entity player = new Entity();
    player.add(new PlayerComponent());
    Game.add(player);
    return player;
  }

  /**
   * The constructor stores a positive heal amount and sets the fixed name, description and texture.
   */
  @Test
  public void constructorWithPositiveHealAmount() {
    ItemHeart heart = new ItemHeart(5);

    assertEquals(5, heart.healAmount());
    assertEquals("Herz", heart.displayName());
    assertEquals("Heilt ein wenig HP.", heart.description());
    assertEquals(
        Optional.of(HEART_TEXTURE_PATH),
        heart.inventoryAnimation().sourcePath().map(IPath::pathString));
  }

  /** The constructor stores a zero or negative heal amount without validation. */
  @Test
  public void constructorWithZeroOrNegativeHealAmount() {
    assertEquals(0, new ItemHeart(0).healAmount());
    assertEquals(-5, new ItemHeart(-5).healAmount());
  }

  /** The default constructor heals one health point. */
  @Test
  public void defaultConstructorHealsOne() {
    assertEquals(1, new ItemHeart().healAmount());
  }

  /** healAmount() returns exactly the value passed at construction. */
  @Test
  public void healAmountReturnsStoredValue() {
    assertEquals(42, new ItemHeart(42).healAmount());
  }

  /** itemData() serializes the heal amount as a single heal-amount entry. */
  @Test
  public void itemDataSerializesHealAmount() {
    Map<String, String> data = new ItemHeart(7).itemData();

    assertEquals(1, data.size());
    assertEquals("7", data.get(DATA_KEY_HEAL_AMOUNT));
  }

  /** collect() always returns false because a heart cannot be inventoried. */
  @Test
  public void collectReturnsFalse() {
    ItemHeart heart = new ItemHeart(5);

    assertFalse(heart.collect(new Entity(), new Entity()));
  }

  /** collect() returns false and throws nothing when the entities are null. */
  @Test
  public void collectWithNullsReturnsFalse() {
    ItemHeart heart = new ItemHeart(5);

    assertFalse(assertDoesNotThrow(() -> heart.collect(null, null)));
  }

  /** drop() on a floor tile adds an entity with a collide component to the game. */
  @Test
  public void dropOnFloorTileAddsEntityWithCollideComponent() {
    long before = Game.levelEntities().count();

    Optional<Entity> result = new ItemHeart(5).drop(new Point(3, 3));

    assertTrue(result.isPresent());
    Entity pickUp = result.get();
    assertTrue(pickUp.isPresent(CollideComponent.class), "dropped heart needs a collide component");
    assertEquals(before + 1, Game.levelEntities().count());
    assertTrue(
        Game.levelEntities().anyMatch(e -> e == pickUp), "entity should be added to the game");
  }

  /** drop() on a non-floor tile returns an empty optional and adds nothing. */
  @Test
  public void dropOnNonFloorTileReturnsEmpty() {
    Point wallPoint = new Point(1, 1);
    // Turn a floor tile into a wall so the tile exists but is not a FloorTile.
    Tile tile = Game.tileAt(wallPoint).orElseThrow();
    level.changeTileElementType(tile, LevelElement.WALL);
    long before = Game.levelEntities().count();

    Optional<Entity> result = new ItemHeart(5).drop(wallPoint);

    assertTrue(result.isEmpty());
    assertEquals(before, Game.levelEntities().count());
  }

  /** drop() outside the map returns an empty optional and adds nothing. */
  @Test
  public void dropOutsideMapReturnsEmpty() {
    long before = Game.levelEntities().count();

    Optional<Entity> result = new ItemHeart(5).drop(new Point(-999, -999));

    assertTrue(result.isEmpty());
    assertEquals(before, Game.levelEntities().count());
  }

  /** drop() with a null position throws a NullPointerException. */
  @Test
  public void dropNullPositionThrows() {
    assertThrows(NullPointerException.class, () -> new ItemHeart(5).drop(null));
  }

  /** Collision with the player heals it and removes the heart when the player has health. */
  @Test
  public void collisionWithPlayerHealsAndRemoves() {
    Entity player = registerPlayer();
    HealthComponent health = new HealthComponent(50);
    health.currentHealthpoints(10);
    player.add(health);

    Entity pickUp = new ItemHeart(5).drop(new Point(3, 3)).orElseThrow();
    CollideComponent collide = pickUp.fetch(CollideComponent.class).orElseThrow();

    collide.onEnter(pickUp, player, Direction.UP);

    assertEquals(15, health.currentHealthpoints(), "player should be healed by the heal amount");
    assertFalse(
        Game.levelEntities().anyMatch(e -> e == pickUp),
        "the heart should be removed from the game");
  }

  /** Collision with a player that has no health component still removes the heart. */
  @Test
  public void collisionWithPlayerNoHealthRemovesOnly() {
    Entity player = registerPlayer();

    Entity pickUp = new ItemHeart(5).drop(new Point(3, 3)).orElseThrow();
    CollideComponent collide = pickUp.fetch(CollideComponent.class).orElseThrow();

    assertDoesNotThrow(() -> collide.onEnter(pickUp, player, Direction.UP));

    assertFalse(
        Game.levelEntities().anyMatch(e -> e == pickUp),
        "the heart should be removed even without healing");
  }

  /** Collision with a non-player entity neither heals nor removes the heart. */
  @Test
  public void collisionWithNonPlayerDoesNothing() {
    registerPlayer();
    Entity other = new Entity();

    Entity pickUp = new ItemHeart(5).drop(new Point(3, 3)).orElseThrow();
    CollideComponent collide = pickUp.fetch(CollideComponent.class).orElseThrow();

    collide.onEnter(pickUp, other, Direction.UP);

    assertTrue(
        Game.levelEntities().anyMatch(e -> e == pickUp),
        "the heart must remain after a non-player collision");
  }

  /** Collision does nothing when no player is registered. */
  @Test
  public void collisionWithoutPlayerDoesNothing() {
    Entity other = new Entity();

    Entity pickUp = new ItemHeart(5).drop(new Point(3, 3)).orElseThrow();
    CollideComponent collide = pickUp.fetch(CollideComponent.class).orElseThrow();

    assertDoesNotThrow(() -> collide.onEnter(pickUp, other, Direction.UP));

    assertTrue(
        Game.levelEntities().anyMatch(e -> e == pickUp),
        "the heart must remain when there is no player");
  }

  /** Collision with a null entity throws a NullPointerException. */
  @Test
  public void collisionWithNullOtherThrows() {
    registerPlayer();

    Entity pickUp = new ItemHeart(5).drop(new Point(3, 3)).orElseThrow();
    CollideComponent collide = pickUp.fetch(CollideComponent.class).orElseThrow();

    assertThrows(NullPointerException.class, () -> collide.onEnter(pickUp, null, Direction.UP));
  }
}
