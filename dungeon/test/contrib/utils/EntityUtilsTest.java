package contrib.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import contrib.components.CollideComponent;
import contrib.entities.HeroController;
import contrib.modules.interaction.IInteractable;
import contrib.modules.interaction.Interaction;
import contrib.modules.interaction.InteractionComponent;
import core.Entity;
import core.Game;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.utils.Point;
import core.utils.Vector2;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import testingUtils.GameTestBase;
import testingUtils.TestGame;

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

  /** Tests for {@link HeroController#findInteractablesInRange(Entity)}. */
  @Nested
  class FindInteractablesInRange extends GameTestBase {

    private Entity createInteractable(float x, float y, float range) {
      IInteractable interactable =
          new IInteractable() {
            @Override
            public Interaction interact() {
              return new Interaction((e, w) -> {}, range);
            }
          };
      return spawn(new PositionComponent(x, y), new InteractionComponent(interactable));
    }

    /** U1: Hero ist null -> NullPointerException bei EntityUtils.getPosition(hero) */
    @Test
    void throwsNullPointerException_whenHeroIsNull() {
      org.junit.jupiter.api.Assertions.assertThrows(
          NullPointerException.class,
          () -> HeroController.findInteractablesInRange(null),
          "findInteractablesInRange must throw NullPointerException if hero is null");
    }

    /** G1: Keine interagierbaren Entities vorhanden -> Leere Liste wird zurückgegeben */
    @Test
    void returnsEmptyList_whenNoInteractableEntitiesExist() {
      Entity hero = spawn(new PositionComponent(0f, 0f));
      // No interactables in the game
      List<Entity> result = HeroController.findInteractablesInRange(hero);
      assertTrue(result.isEmpty(), "Result must be empty when no interactables exist");
    }

    /** G2: Ein Entity innerhalb der Reichweite -> Liste enthält genau dieses Entity */
    @Test
    void returnsEntity_whenOneEntityIsWithinRange() {
      Entity hero = spawn(new PositionComponent(0f, 0f));
      Entity interactable = createInteractable(1f, 0f, 2f); // distance=1, range=2

      List<Entity> result = HeroController.findInteractablesInRange(hero);

      assertEquals(1, result.size(), "Result must contain exactly one entity");
      assertTrue(
          result.contains(interactable), "Result must contain the interactable entity in range");
    }

    /**
     * G3: Mehrere Entities innerhalb der Reichweite -> Alle passenden Entities werden zurückgegeben
     */
    @Test
    void returnsAllEntities_whenMultipleEntitiesAreWithinRange() {
      Entity hero = spawn(new PositionComponent(0f, 0f));
      Entity i1 = createInteractable(1f, 0f, 2f);
      Entity i2 = createInteractable(0f, 1f, 2f);

      List<Entity> result = HeroController.findInteractablesInRange(hero);

      assertEquals(2, result.size(), "Result must contain all entities within range");
      assertTrue(
          result.contains(i1) && result.contains(i2),
          "Result must contain both interactable entities");
    }

    /**
     * G4: Mischung aus Entities innerhalb und außerhalb der Reichweite -> Nur Entities innerhalb
     * der Reichweite werden zurückgegeben
     */
    @Test
    void returnsOnlyEntitiesWithinRange_whenMixedEntitiesExist() {
      Entity hero = spawn(new PositionComponent(0f, 0f));
      Entity inRange = createInteractable(1f, 0f, 2f); // distance=1, range=2
      Entity outOfRange = createInteractable(3f, 0f, 2f); // distance=3, range=2

      List<Entity> result = HeroController.findInteractablesInRange(hero);

      assertEquals(1, result.size(), "Result must contain only the entity within range");
      assertTrue(result.contains(inRange), "Result must contain the entity within range");
    }

    /** G5: Entity genau auf der Reichweitengrenze -> Entity wird zurückgegeben */
    @Test
    void returnsEntity_whenEntityIsExactlyOnRangeBorder() {
      Entity hero = spawn(new PositionComponent(0f, 0f));
      Entity exactRange = createInteractable(2f, 0f, 2f); // distance=2, range=2

      List<Entity> result = HeroController.findInteractablesInRange(hero);

      assertEquals(1, result.size(), "Result must contain the entity exactly on the range border");
      assertTrue(
          result.contains(exactRange),
          "Result must contain the entity exactly on the range border");
    }

    /**
     * G6: Mehrere gültige Entities mit unterschiedlichen Reichweiten -> Für jedes Entity wird die
     * individuelle Reichweite verwendet
     */
    @Test
    void evaluatesIndividualRanges_whenEntitiesHaveDifferentRanges() {
      Entity hero = spawn(new PositionComponent(0f, 0f));
      // Entity at distance 3, range 4 -> should be included
      Entity i1 = createInteractable(3f, 0f, 4f);
      // Entity at distance 3, range 2 -> should be excluded
      Entity i2 = createInteractable(0f, 3f, 2f);

      List<Entity> result = HeroController.findInteractablesInRange(hero);

      assertEquals(
          1,
          result.size(),
          "Result must only contain the entity whose individual range condition is met");
      assertTrue(result.contains(i1), "Result must contain the entity with the larger range");
    }

    /**
     * U2: Entity ohne InteractionComponent trotz Vorfilter -> orElseThrow() wird ausgelöst
     * (NoSuchElementException)
     */
    @Test
    void throwsNoSuchElementException_whenEntityLacksInteractionComponent() {
      Entity hero = new Entity();
      hero.add(new PositionComponent(0f, 0f));

      Entity faultyEntity = new Entity();
      faultyEntity.add(new PositionComponent(1f, 0f));
      // Missing InteractionComponent

      try (org.mockito.MockedStatic<Game> mockedGame = TestGame.withEntities(faultyEntity)) {
        org.junit.jupiter.api.Assertions.assertThrows(
            NoSuchElementException.class,
            () -> HeroController.findInteractablesInRange(hero),
            "Method must throw NoSuchElementException if an entity in the stream lacks InteractionComponent");
      }
    }

    /**
     * U3: Entity ohne PositionComponent trotz Vorfilter -> NoSuchElementException beim Zugriff auf
     * die Position
     */
    @Test
    void throwsNoSuchElementException_whenEntityLacksPositionComponent() {
      Entity hero = new Entity();
      hero.add(new PositionComponent(0f, 0f));

      Entity faultyEntity = new Entity();
      IInteractable interactable =
          new IInteractable() {
            @Override
            public Interaction interact() {
              return new Interaction((e, w) -> {}, 2f);
            }
          };
      faultyEntity.add(new InteractionComponent(interactable));
      // Missing PositionComponent

      try (org.mockito.MockedStatic<Game> mockedGame = TestGame.withEntities(faultyEntity)) {
        org.junit.jupiter.api.Assertions.assertThrows(
            NoSuchElementException.class,
            () -> HeroController.findInteractablesInRange(hero),
            "Method must throw NoSuchElementException if an entity in the stream lacks PositionComponent");
      }
    }

    /**
     * U4: Negative Reichweite -> Die Berechnung verwendet dennoch range * range (wird quadriert)
     * und bewertet das Entity als gültig.
     */
    @Test
    void includesEntity_whenRangeIsNegativeButSquaredDistanceIsSmaller() {
      Entity hero = spawn(new PositionComponent(0f, 0f));
      // Range is -2, distance is 1. Squared distance is 1, squared range is 4.
      Entity negativeRangeEntity = createInteractable(1f, 0f, -2f);

      List<Entity> result = HeroController.findInteractablesInRange(hero);

      assertEquals(
          1,
          result.size(),
          "Result must contain the entity since the negative range is squared and therefore positive");
      assertTrue(
          result.contains(negativeRangeEntity),
          "Entity with negative range is included due to squaring");
    }
  }
}
