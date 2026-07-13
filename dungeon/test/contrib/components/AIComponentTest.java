package contrib.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import contrib.systems.AISystem;
import contrib.utils.components.ai.fight.AIChaseBehaviour;
import contrib.utils.components.ai.idle.RadiusWalk;
import contrib.utils.components.ai.transition.RangeTransition;
import core.Entity;
import core.Game;
import core.utils.Vector2;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import testingUtils.GameTestBase;

/**
 * Tests for {@link AIComponent}.
 *
 * <p>{@code AIComponent} stores an idle behaviour, a fight behaviour, a transition function that
 * decides between the two, an activity flag and a movement force. While {@link
 * AIComponent#active()} is {@code true}, {@link AIComponent#shouldFight()} and {@link
 * AIComponent#idleBehavior()} delegate to the stored transition/idle behaviour unchanged (G1).
 * While inactive, {@link AIComponent#shouldFight()} always returns a function that yields {@code
 * false} and {@link AIComponent#idleBehavior()} returns a no-op consumer (G2); {@link
 * AIComponent#fightBehavior()} is unaffected by the activity flag either way.
 */
class AIComponentTest {

  /**
   * Creates a component with distinguishable stub behaviours and no side effects, useful when a
   * test only cares about a single method under test.
   *
   * @return a new component with distinct stub fight/idle/transition behaviours.
   */
  private static AIComponent stubComponent() {
    return new AIComponent(ignored -> {}, ignored -> {}, ignored -> false);
  }

  /** Tests that the constructor taking explicit behaviours stores exactly those references. */
  @Test
  void customConstructorStoresGivenBehaviors() {
    Consumer<Entity> fight = ignored -> {};
    Consumer<Entity> idle = ignored -> {};
    Function<Entity, Boolean> transition = ignored -> true;

    AIComponent ai = new AIComponent(fight, idle, transition);

    assertSame(fight, ai.fightBehavior(), "the given fight behavior must be stored unchanged");
    assertSame(idle, ai.idleBehavior(), "the given idle behavior must be stored unchanged");
    assertSame(
        transition, ai.shouldFight(), "the given transition function must be stored unchanged");
  }

  /**
   * Tests that the 3-argument constructor falls back to the documented default movement force of
   * {@code Vector2.of(5, 5)}, and that the 4-argument constructor stores a custom movement force
   * instead.
   */
  @Test
  void constructorsStoreExpectedMovementForce() {
    AIComponent withDefaultForce = new AIComponent(ignored -> {}, ignored -> {}, ignored -> false);
    assertEquals(
        Vector2.of(5, 5),
        withDefaultForce.movementForce(),
        "the 3-argument constructor must default to a movement force of (5, 5)");

    Vector2 customForce = Vector2.of(3f, 9f);
    AIComponent withCustomForce =
        new AIComponent(ignored -> {}, ignored -> {}, ignored -> false, customForce);
    assertEquals(
        customForce,
        withCustomForce.movementForce(),
        "the 4-argument constructor must store the given movement force");
  }

  /**
   * Tests that the no-argument constructor produces a fully usable component with the documented
   * default behaviours: active, {@link AIChaseBehaviour} as fight behaviour, {@link RadiusWalk} as
   * idle behaviour, {@link RangeTransition} as the transition function, and the default movement
   * force.
   */
  @Test
  void defaultConstructorCreatesValidComponentWithDefaults() {
    AIComponent ai = new AIComponent();

    assertTrue(ai.active(), "a freshly created component must be active by default");
    assertTrue(
        ai.fightBehavior() instanceof AIChaseBehaviour,
        "the default fight behaviour must be an AIChaseBehaviour");
    assertTrue(
        ai.idleBehavior() instanceof RadiusWalk, "the default idle behaviour must be a RadiusWalk");
    assertTrue(
        ai.shouldFight() instanceof RangeTransition,
        "the default transition function must be a RangeTransition");
    assertEquals(Vector2.of(5, 5), ai.movementForce(), "the default movement force must be (5, 5)");
  }

  /**
   * Tests that {@link AIComponent#active(boolean)} correctly changes the state that {@link
   * AIComponent#active()} reports, in both directions.
   */
  @Test
  void activeSetsAndReportsTheActivityState() {
    AIComponent ai = stubComponent();
    assertTrue(ai.active(), "a freshly created component must start out active");

    ai.active(false);
    assertFalse(ai.active(), "active(false) must be reflected by active()");

    ai.active(true);
    assertTrue(ai.active(), "active(true) must be reflected by active()");
  }

  /**
   * G1: while active, {@link AIComponent#shouldFight()} must return the stored transition function
   * unchanged, i.e. its result must depend on the entity exactly as the stored function specifies,
   * not a hardcoded value.
   */
  @Test
  void shouldFightUsesStoredTransitionLogicWhenActive() {
    Entity fighter = new Entity();
    Entity idler = new Entity();
    Function<Entity, Boolean> transition = entity -> entity == fighter;

    AIComponent ai = new AIComponent(ignored -> {}, ignored -> {}, transition);

    assertSame(
        transition, ai.shouldFight(), "shouldFight() must return the stored function reference");
    assertTrue(
        ai.shouldFight().apply(fighter), "the stored transition logic must decide per entity");
    assertFalse(
        ai.shouldFight().apply(idler), "the stored transition logic must decide per entity");
  }

  /**
   * G2: while inactive, {@link AIComponent#shouldFight()} must always yield {@code false}, even for
   * an entity for which the stored transition function would return {@code true}.
   */
  @Test
  void shouldFightAlwaysReturnsFalseWhenInactive() {
    Entity entity = new Entity();
    Function<Entity, Boolean> alwaysWantsToFight = ignored -> true;
    AIComponent ai = new AIComponent(ignored -> {}, ignored -> {}, alwaysWantsToFight);

    ai.active(false);

    assertFalse(
        ai.shouldFight().apply(entity),
        "an inactive component must never enter fight mode, regardless of the transition logic");
  }

  /**
   * G1: while active, {@link AIComponent#idleBehavior()} must return the stored idle behaviour, and
   * invoking it must actually run that behaviour.
   */
  @Test
  void idleBehaviorRunsStoredBehaviorWhenActive() {
    Entity entity = new Entity();
    AtomicReference<Entity> invokedWith = new AtomicReference<>();
    Consumer<Entity> idle = invokedWith::set;

    AIComponent ai = new AIComponent(ignored -> {}, idle, ignored -> false);
    ai.idleBehavior().accept(entity);

    assertSame(
        entity, invokedWith.get(), "the stored idle behaviour must be executed with the entity");
  }

  /**
   * G2: while inactive, {@link AIComponent#idleBehavior()} must return a behaviour that performs no
   * action, i.e. the stored idle behaviour must never be invoked.
   */
  @Test
  void idleBehaviorPerformsNoActionWhenInactive() {
    Entity entity = new Entity();
    AtomicReference<Entity> invokedWith = new AtomicReference<>();
    Consumer<Entity> idle = invokedWith::set;

    AIComponent ai = new AIComponent(ignored -> {}, idle, ignored -> false);
    ai.active(false);
    ai.idleBehavior().accept(entity);

    assertNull(
        invokedWith.get(), "an inactive component must not execute the stored idle behaviour");
  }

  /**
   * Tests that {@link AIComponent#movementForce()} returns exactly the vector given at
   * construction.
   */
  @Test
  void movementForceReturnsTheGivenVector() {
    Vector2 force = Vector2.of(-2.5f, 7f);
    AIComponent ai = new AIComponent(ignored -> {}, ignored -> {}, ignored -> false, force);

    assertEquals(
        force, ai.movementForce(), "movementForce() must return the vector given at construction");
  }

  /**
   * Component test: an entity with an {@link AIComponent} processed by the real {@link AISystem}
   * must execute exactly the fight behaviour or exactly the idle behaviour, depending on what
   * {@link AIComponent#shouldFight()} decides for that entity.
   */
  @Nested
  class AISystemIntegration extends GameTestBase {

    /**
     * When {@code shouldFight()} decides to fight, {@link AISystem#execute()} must run the fight
     * behaviour and must not run the idle behaviour.
     */
    @Test
    void executesFightBehaviorWhenShouldFightIsTrue() {
      AtomicReference<Entity> fightCalledWith = new AtomicReference<>();
      AtomicReference<Entity> idleCalledWith = new AtomicReference<>();
      AIComponent ai = new AIComponent(fightCalledWith::set, idleCalledWith::set, ignored -> true);
      Entity entity = spawn(ai);

      AISystem aiSystem = new AISystem();
      Game.add(aiSystem);
      aiSystem.execute();

      assertSame(entity, fightCalledWith.get(), "the fight behaviour must run for this entity");
      assertNull(
          idleCalledWith.get(), "the idle behaviour must not run while shouldFight() is true");
    }

    /**
     * When {@code shouldFight()} decides not to fight, {@link AISystem#execute()} must run the idle
     * behaviour and must not run the fight behaviour.
     */
    @Test
    void executesIdleBehaviorWhenShouldFightIsFalse() {
      AtomicReference<Entity> fightCalledWith = new AtomicReference<>();
      AtomicReference<Entity> idleCalledWith = new AtomicReference<>();
      AIComponent ai = new AIComponent(fightCalledWith::set, idleCalledWith::set, ignored -> false);
      Entity entity = spawn(ai);

      AISystem aiSystem = new AISystem();
      Game.add(aiSystem);
      aiSystem.execute();

      assertSame(entity, idleCalledWith.get(), "the idle behaviour must run for this entity");
      assertNull(
          fightCalledWith.get(), "the fight behaviour must not run while shouldFight() is false");
    }
  }
}
