package contrib.modules.worldTimer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import core.Entity;
import core.Game;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.utils.Point;
import core.utils.components.draw.state.StateMachine;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/** Tests for {@link WorldTimerSystem}. */
class WorldTimerSystemTest {

  @AfterEach
  void cleanup() {
    Game.removeAllEntities();
    Game.removeAllSystems();
  }

  @Test
  void onTimerExpiredRegistersCallback() {
    TestableWorldTimerSystem system = new TestableWorldTimerSystem(200);
    AtomicInteger callbackCount = new AtomicInteger();
    Entity entity = newTimerEntity(100, 50);
    Game.add(entity);

    assertSame(
        system,
        system.onTimerExpired(callbackCount::incrementAndGet),
        "onTimerExpired should return the same system instance for chaining.");

    system.execute();

    assertEquals(
        1,
        callbackCount.get(),
        "Registered expired callback should run when the timer reaches zero.");
  }

  @Test
  void formatsPositiveRemainingTime() {
    TestableWorldTimerSystem system = new TestableWorldTimerSystem(160);
    Game.add(newTimerEntity(100, 125));

    system.execute();

    assertEquals(
        "01:05", system.lastTimerString, "Positive remaining time should be rendered as mm:ss.");
  }

  @Test
  void clampsNegativeRemainingTimeToZero() {
    TestableWorldTimerSystem system = new TestableWorldTimerSystem(200);
    Game.add(newTimerEntity(100, 50));

    system.execute();

    assertEquals(
        "00:00", system.lastTimerString, "Negative remaining time should be clamped to 00:00.");
  }

  @Test
  void executesExpiredCallbackWhenTimerExpires() {
    TestableWorldTimerSystem system = new TestableWorldTimerSystem(200);
    AtomicInteger callbackCount = new AtomicInteger();
    Game.add(newTimerEntity(100, 50));
    system.onTimerExpired(callbackCount::incrementAndGet);

    system.execute();

    assertEquals(
        1, callbackCount.get(), "Expired callback should run when the timer first expires.");
  }

  @Test
  void executesExpiredCallbackOnlyOnce() {
    TestableWorldTimerSystem system = new TestableWorldTimerSystem(200);
    AtomicInteger callbackCount = new AtomicInteger();
    Game.add(newTimerEntity(100, 50));
    system.onTimerExpired(callbackCount::incrementAndGet);

    system.execute();
    system.execute();

    assertEquals(
        1,
        callbackCount.get(),
        "Expired callback should not run more than once after the timer has elapsed.");
  }

  @Test
  void replacesExistingDrawComponentOnUpdate() {
    TestableWorldTimerSystem system = new TestableWorldTimerSystem(160);
    Entity entity = newTimerEntity(100, 125);
    DrawComponent initialDrawComponent = newDrawComponent();
    entity.add(initialDrawComponent);
    Game.add(entity);

    system.execute();

    DrawComponent updatedDrawComponent = entity.fetch(DrawComponent.class).orElseThrow();
    assertNotSame(
        initialDrawComponent,
        updatedDrawComponent,
        "Existing DrawComponent should be replaced during the update.");
    assertSame(
        system.lastDrawComponent,
        updatedDrawComponent,
        "System should add the replacement DrawComponent to the entity.");
  }

  @Test
  void entityReceivesTimerDrawComponentAndExecutesExpiryCallback() {
    TestableWorldTimerSystem system = new TestableWorldTimerSystem(200);
    AtomicInteger callbackCount = new AtomicInteger();
    Entity entity = newTimerEntity(100, 50);
    Game.add(system);
    Game.add(entity);
    system.onTimerExpired(callbackCount::incrementAndGet);

    system.execute();

    assertTrue(
        entity.isPresent(DrawComponent.class),
        "Entity should receive a DrawComponent after the timer update.");
    assertSame(
        system.lastDrawComponent,
        entity.fetch(DrawComponent.class).orElseThrow(),
        "Entity should receive the DrawComponent created by the system.");
    assertEquals("00:00", system.lastTimerString, "Expired timer should render as 00:00.");
    assertEquals(
        1,
        callbackCount.get(),
        "Expired callback should execute during the component interaction test.");
  }

  private static Entity newTimerEntity(int timestamp, int duration) {
    Entity entity = new Entity("world-timer-test");
    entity.add(new WorldTimerComponent(timestamp, duration));
    entity.add(new PositionComponent(new Point(0, 0)));
    return entity;
  }

  private static DrawComponent newDrawComponent() {
    return new DrawComponent(Mockito.mock(StateMachine.class));
  }

  private static final class TestableWorldTimerSystem extends WorldTimerSystem {

    private final int fixedUnixTimeSeconds;
    private String lastTimerString;
    private DrawComponent lastDrawComponent;

    private TestableWorldTimerSystem(int fixedUnixTimeSeconds) {
      this.fixedUnixTimeSeconds = fixedUnixTimeSeconds;
    }

    @Override
    protected int currentUnixTimeSeconds() {
      return fixedUnixTimeSeconds;
    }

    @Override
    protected void updateDrawComponent(Entity entity, String timerString) {
      lastTimerString = timerString;
      lastDrawComponent = newDrawComponent();
      entity.remove(DrawComponent.class);
      entity.add(lastDrawComponent);
    }
  }
}
