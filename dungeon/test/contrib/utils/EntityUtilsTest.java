package contrib.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import contrib.components.CollideComponent;
import core.Entity;
import core.Game;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.utils.Point;
import core.utils.Vector2;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import testingUtils.GameTestBase;

/** Tests for {@link EntityUtils}. */
class EntityUtilsTest {

  /**
   * Creates a mocked {@link DrawComponent} that reports the given sprite size.
   *
   * @param width the value {@code getWidth()} must return.
   * @param height the value {@code getHeight()} must return.
   * @return a mock with {@code getWidth()}/{@code getHeight()} stubbed accordingly.
   */
  private static DrawComponent mockDrawComponent(float width, float height) {
    DrawComponent dc = Mockito.mock(DrawComponent.class);
    Mockito.when(dc.getWidth()).thenReturn(width);
    Mockito.when(dc.getHeight()).thenReturn(height);
    return dc;
  }

  /**
   * Creates an entity with only a {@link CollideComponent} spanning the world-space rectangle
   * {@code [0, 2) x [0, 2)} (a hitbox with zero offset and size 2x2).
   *
   * @return a new entity with that collider.
   */
  private static Entity entityWithSmallCollider() {
    Entity entity = new Entity();
    entity.add(new CollideComponent(Vector2.of(0f, 0f), Vector2.of(2f, 2f)));
    return entity;
  }

  /** Tests that {@link EntityUtils#teleportEntityTo(Entity, Point)} sets the entity's position. */
  @Test
  void teleportEntityToSetsThePosition() {
    Entity entity = new Entity();
    entity.add(new PositionComponent(0f, 0f));
    Point target = new Point(7f, 3f);

    EntityUtils.teleportEntityTo(entity, target);

    assertEquals(
        target,
        entity.fetch(PositionComponent.class).orElseThrow().position(),
        "teleportEntityTo must set the entity's PositionComponent to the given point");
  }

  /**
   * Tests that {@link EntityUtils#getPosition(Entity)} returns the collider's absolute center when
   * a {@link CollideComponent} is present, ignoring the raw PositionComponent value.
   */
  @Test
  void getPositionReturnsColliderCenter_whenCollideComponentPresent() {
    Entity entity = new Entity();
    // PositionComponent must exist (getPosition() always fetches it), but its value is irrelevant
    // here since the collider branch does not consult it.
    entity.add(new PositionComponent(100f, 100f));
    entity.add(new CollideComponent(Vector2.of(0f, 0f), Vector2.of(4f, 2f)));

    assertEquals(
        new Point(2f, 1f),
        EntityUtils.getPosition(entity),
        "getPosition() must return the collider's absolute center when a CollideComponent is present");
  }

  /**
   * Tests that {@link EntityUtils#getPosition(Entity)} returns the sprite's center when a {@link
   * DrawComponent} is present and no {@link CollideComponent} exists.
   */
  @Test
  void getPositionReturnsSpriteCenter_whenOnlyDrawComponentPresent() {
    Entity entity = new Entity();
    entity.add(new PositionComponent(10f, 20f));
    entity.add(mockDrawComponent(4f, 2f));

    assertEquals(
        new Point(12f, 21f),
        EntityUtils.getPosition(entity),
        "getPosition() must return position + half the sprite size when only a DrawComponent is present");
  }

  /**
   * Tests that {@link EntityUtils#getDistance(Entity, Entity)} computes the Euclidean distance
   * between the positions of two entities.
   */
  @Test
  void getDistanceComputesEuclideanDistanceBetweenEntities() {
    Entity a = new Entity();
    a.add(new PositionComponent(0f, 0f));
    Entity b = new Entity();
    b.add(new PositionComponent(3f, 4f));

    assertEquals(
        5.0,
        EntityUtils.getDistance(a, b),
        1e-6,
        "getDistance() must return the Euclidean distance between the two entities' positions");
  }

  /**
   * Tests for {@link EntityUtils#isPointOverEntity(Entity, Point)}.
   *
   * <p>The method tries, in order, a {@link CollideComponent}-based check, a {@link
   * DrawComponent}-based bounding-box check, and finally a small fallback radius around {@link
   * EntityUtils#getPosition(Entity)}.
   */
  @Nested
  class IsPointOverEntity {

    /** G1: a point inside the entity's collider is considered to be over the entity. */
    @Test
    void returnsTrue_whenPointIsInsideCollider() {
      Entity entity = entityWithSmallCollider();

      assertTrue(
          EntityUtils.isPointOverEntity(entity, new Point(1f, 1f)),
          "a point inside the collider must be considered over the entity");
    }

    /** U1: a point outside the entity's collider is not considered to be over the entity. */
    @Test
    void returnsFalse_whenPointIsOutsideCollider() {
      Entity entity = entityWithSmallCollider();

      assertFalse(
          EntityUtils.isPointOverEntity(entity, new Point(5f, 5f)),
          "a point outside the collider must not be considered over the entity");
    }

    /**
     * G2: without a {@link CollideComponent}, a point inside the sprite bounds of a {@link
     * DrawComponent} is considered to be over the entity.
     */
    @Test
    void returnsTrue_whenPointIsInsideSpriteBounds() {
      Entity entity = new Entity();
      entity.add(new PositionComponent(0f, 0f));
      entity.add(mockDrawComponent(4f, 2f));

      assertTrue(
          EntityUtils.isPointOverEntity(entity, new Point(2f, 1f)),
          "a point inside the sprite bounds must be considered over the entity");
    }

    /** U2: a point outside the sprite bounds of a {@link DrawComponent} is not over the entity. */
    @Test
    void returnsFalse_whenPointIsOutsideSpriteBounds() {
      Entity entity = new Entity();
      entity.add(new PositionComponent(0f, 0f));
      entity.add(mockDrawComponent(4f, 2f));

      assertFalse(
          EntityUtils.isPointOverEntity(entity, new Point(10f, 10f)),
          "a point outside the sprite bounds must not be considered over the entity");
    }

    /**
     * G3: without a {@link CollideComponent} or {@link DrawComponent}, a point within the fallback
     * radius around the entity's position is considered to be over the entity.
     */
    @Test
    void returnsTrue_whenPointIsWithinFallbackRadius() {
      Entity entity = new Entity();
      entity.add(new PositionComponent(0f, 0f));

      assertTrue(
          EntityUtils.isPointOverEntity(entity, new Point(0.3f, 0f)),
          "a point within the fallback radius must be considered over the entity");
    }
  }

  /**
   * Tests for the player-accessor methods of {@link EntityUtils} when no player is present.
   *
   * <p>Uses {@link GameTestBase} so {@link Game#player()} deterministically reports no player.
   */
  @Nested
  class PlayerAccessors extends GameTestBase {

    /**
     * U3: without a player in the game, {@link EntityUtils#getPlayerPosition()}, {@link
     * EntityUtils#getPlayerCoordinate()} and {@link EntityUtils#getPlayerViewDirection()} must all
     * return {@code null}.
     */
    @Test
    void playerAccessorsReturnNull_whenNoPlayerExists() {
      assertNull(
          EntityUtils.getPlayerPosition(), "getPlayerPosition() must be null without a player");
      assertNull(
          EntityUtils.getPlayerCoordinate(), "getPlayerCoordinate() must be null without a player");
      assertNull(
          EntityUtils.getPlayerViewDirection(),
          "getPlayerViewDirection() must be null without a player");
    }
  }

  /**
   * Component test for {@link EntityUtils#findEntityAtPoint(Point, java.util.stream.Stream)}.
   *
   * <p>Verifies the interplay of several real entities (each combining a {@link PositionComponent}
   * and a {@link CollideComponent}) registered in the game: entities whose collider does not
   * contain the point are excluded, and among the entities that do contain it, the one whose center
   * is closest to the point wins.
   */
  @Nested
  class FindEntityAtPointIntegration extends GameTestBase {

    /**
     * Among several entities, some of which overlap the point, {@code findEntityAtPoint()} must
     * return the one whose collider contains the point and whose center is closest to it.
     */
    @Test
    void selectsTheClosestOverlappingEntityUnderThePoint() {
      Point point = new Point(5f, 5f);

      // Collider spans [0, 2) x [0, 2) -> does not contain the point, must be excluded.
      spawn(
          new PositionComponent(0f, 0f),
          new CollideComponent(Vector2.of(0f, 0f), Vector2.of(2f, 2f)));

      // Collider spans [0, 10) x [0, 10) -> contains the point, center is exactly the point.
      Entity nearest =
          spawn(
              new PositionComponent(0f, 0f),
              new CollideComponent(Vector2.of(0f, 0f), Vector2.of(10f, 10f)));

      // Collider spans [0, 20) x [0, 20) -> also contains the point, but its center is farther
      // away.
      spawn(
          new PositionComponent(0f, 0f),
          new CollideComponent(Vector2.of(0f, 0f), Vector2.of(20f, 20f)));

      Optional<Entity> result = EntityUtils.findEntityAtPoint(point, Game.levelEntities());

      assertTrue(result.isPresent(), "an entity containing the point must be found");
      assertSame(
          nearest,
          result.get(),
          "the entity whose center is closest to the point must be selected among overlapping ones");
    }
  }
}
