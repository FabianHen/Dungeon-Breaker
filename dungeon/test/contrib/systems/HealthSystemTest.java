package contrib.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import contrib.components.HealthComponent;
import contrib.utils.components.health.Damage;
import contrib.utils.components.health.DamageType;
import contrib.utils.components.health.FakeHealthObserver;
import core.Entity;
import core.FakeGame;
import core.components.DrawComponent;
import org.junit.jupiter.api.Test;

/** test class */
public class HealthSystemTest {
  private final FakeGame game = new FakeGame();
  private final MockHealthSystem system = new MockHealthSystem(game);
  private final Entity entity = new Entity("test");

  @Test
  void calculateDamage_singleDamage_returnsDamage() {

    HealthComponent hc = new HealthComponent(100);
    hc.receiveHit(new Damage(10, DamageType.PHYSICAL, null));

    DrawComponent dc = mock(DrawComponent.class);

    MockHealthSystem.HSData data = new MockHealthSystem.HSData(entity, hc, dc);

    assertEquals(10, system.calculateDamagePublic(data));
  }

  @Test
  void calculateDamage_multipleDamage_returnsSum() {

    HealthComponent hc = new HealthComponent(100);
    hc.receiveHit(new Damage(10, DamageType.PHYSICAL, null));
    hc.receiveHit(new Damage(15, DamageType.FIRE, null));

    DrawComponent dc = mock(DrawComponent.class);

    MockHealthSystem.HSData data = new MockHealthSystem.HSData(entity, hc, dc);

    assertEquals(25, system.calculateDamagePublic(data));
  }

  @Test
  void calculateDamage_noDamage_returnsZero() {

    HealthComponent hc = new HealthComponent(100);

    DrawComponent dc = mock(DrawComponent.class);

    MockHealthSystem.HSData data = new MockHealthSystem.HSData(entity, hc, dc);

    assertEquals(0, system.calculateDamagePublic(data));
  }

  @Test
  void calculateDamage_positiveAndNegativeDamage_returnsSum() {

    HealthComponent hc = new HealthComponent(100);

    hc.receiveHit(new Damage(20, DamageType.PHYSICAL, null));
    hc.receiveHit(new Damage(-5, DamageType.FIRE, null));

    DrawComponent dc = mock(DrawComponent.class);

    MockHealthSystem.HSData data = new MockHealthSystem.HSData(entity, hc, dc);

    assertEquals(15, system.calculateDamagePublic(data));
  }

  @Test
  void calculateDamage_nullHSData_throwsException() {

    assertThrows(NullPointerException.class, () -> system.calculateDamagePublic(null));
  }

  /**
   * Tests that applying damage without any pending damage does not change the health points and
   * does not trigger a damage signal.
   *
   * <p>The observer should still receive a damage event notification.
   */
  @Test
  void applyDamageNoDamage() {

    HealthComponent hc = new HealthComponent(100);
    DrawComponent dc = mock(DrawComponent.class);

    MockHealthSystem.HSData data = new MockHealthSystem.HSData(entity, hc, dc);

    FakeHealthObserver observer = new FakeHealthObserver();
    system.registerObserver(observer);

    system.applyDamagePublic(data);

    assertEquals(100, hc.currentHealthpoints());

    verify(dc, never()).sendSignal(MockHealthSystem.DAMAGE_SIGNAL);

    for (DamageType type : DamageType.values()) {
      assertEquals(0, hc.calculateDamageOf(type));
    }

    assertTrue(observer.eventReceived());
    assertEquals(FakeHealthObserver.HealthEvent.DAMAGE, observer.lastEvent());
    assertEquals(data, observer.lastData());
  }

  /**
   * Tests that a single damage instance is correctly applied.
   *
   * <p>The received damage should be removed from the HealthComponent, the current health points
   * should decrease, and the DrawComponent should receive a damage signal.
   */
  @Test
  void applyDamageSingleDamage() {

    HealthComponent hc = new HealthComponent(100);
    hc.receiveHit(new Damage(25, DamageType.PHYSICAL, null));

    DrawComponent dc = mock(DrawComponent.class);

    MockHealthSystem.HSData data = new MockHealthSystem.HSData(entity, hc, dc);

    FakeHealthObserver observer = new FakeHealthObserver();
    system.registerObserver(observer);

    system.applyDamagePublic(data);

    assertEquals(75, hc.currentHealthpoints());

    verify(dc).sendSignal(MockHealthSystem.DAMAGE_SIGNAL);

    for (DamageType type : DamageType.values()) {
      assertEquals(0, hc.calculateDamageOf(type));
    }

    assertTrue(observer.eventReceived());
    assertEquals(FakeHealthObserver.HealthEvent.DAMAGE, observer.lastEvent());
    assertEquals(data, observer.lastData());
  }

  /**
   * Tests that multiple damage instances of different damage types are correctly processed.
   *
   * <p>The total damage amount should be subtracted from the health points, all stored damage
   * values should be cleared, and the observer should receive a damage event.
   */
  @Test
  void applyDamageMultipleDamage() {

    HealthComponent hc = new HealthComponent(100);

    hc.receiveHit(new Damage(20, DamageType.PHYSICAL, null));
    hc.receiveHit(new Damage(15, DamageType.FIRE, null));
    hc.receiveHit(new Damage(5, DamageType.MAGIC, null));

    DrawComponent dc = mock(DrawComponent.class);

    MockHealthSystem.HSData data = new MockHealthSystem.HSData(entity, hc, dc);

    FakeHealthObserver observer = new FakeHealthObserver();
    system.registerObserver(observer);

    system.applyDamagePublic(data);

    assertEquals(60, hc.currentHealthpoints());

    verify(dc).sendSignal(MockHealthSystem.DAMAGE_SIGNAL);

    for (DamageType type : DamageType.values()) {
      assertEquals(0, hc.calculateDamageOf(type));
    }

    assertTrue(observer.eventReceived());
    assertEquals(FakeHealthObserver.HealthEvent.DAMAGE, observer.lastEvent());
    assertEquals(data, observer.lastData());
  }

  /**
   * Tests that applying damage without a DrawComponent throws a NullPointerException.
   *
   * <p>The HealthSystem requires a DrawComponent to send damage signals.
   */
  @Test
  void applyDamageWithoutDrawComponent() {
    HealthComponent hc = new HealthComponent(100);
    hc.receiveHit(new Damage(10, DamageType.PHYSICAL, null));

    MockHealthSystem.HSData data = new MockHealthSystem.HSData(entity, hc, null);

    assertThrows(NullPointerException.class, () -> system.applyDamagePublic(data));
  }

  /**
   * Tests that applying damage without a HealthComponent throws a NullPointerException.
   *
   * <p>A HealthComponent is required because it stores and processes the entity's health and damage
   * values.
   */
  @Test
  void applyDamageWithoutHealthComponent() {

    DrawComponent dc = mock(DrawComponent.class);

    MockHealthSystem.HSData data = new MockHealthSystem.HSData(entity, null, dc);

    assertThrows(NullPointerException.class, () -> system.applyDamagePublic(data));
  }

  /**
   * Tests that passing null HealthSystem data causes a NullPointerException.
   *
   * <p>The HealthSystem requires valid HSData to process damage.
   */
  @Test
  void applyDamageWithNullHSData() {
    assertThrows(NullPointerException.class, () -> system.applyDamagePublic(null));
  }

  /**
   * Tests that an entity is marked as already dead after the death trigger.
   *
   * <p>The HealthComponent should update its alreadyDead state to prevent multiple death
   * executions.
   */
  @Test
  void triggerOnDeathMarksEntityAsAlreadyDead() {

    HealthComponent hc = new HealthComponent(0);
    DrawComponent dc = mock(DrawComponent.class);

    MockHealthSystem.HSData data = new MockHealthSystem.HSData(entity, hc, dc);

    system.triggerOnDeathPublic(data);

    assertTrue(hc.alreadyDead());
  }

  /**
   * Tests that all registered observers receive a DEATH event.
   *
   * <p>The observer should receive the same HSData instance that was passed to the HealthSystem.
   */
  @Test
  void triggerOnDeathNotifiesObservers() {

    HealthComponent hc = new HealthComponent(0);
    DrawComponent dc = mock(DrawComponent.class);

    MockHealthSystem.HSData data = new MockHealthSystem.HSData(entity, hc, dc);

    FakeHealthObserver observer = new FakeHealthObserver();
    system.registerObserver(observer);

    system.triggerOnDeathPublic(data);

    assertTrue(observer.eventReceived());
    assertEquals(FakeHealthObserver.HealthEvent.DEATH, observer.lastEvent());
    assertEquals(data, observer.lastData());
  }

  /**
   * Tests that the registered On-Death callback is executed.
   *
   * <p>The callback should be called when the death handling is triggered.
   */
  @Test
  void triggerOnDeathExecutesCallback() {

    HealthComponent hc = new HealthComponent(0);
    DrawComponent dc = mock(DrawComponent.class);

    boolean[] callbackExecuted = {false};

    hc.onDeath(entity -> callbackExecuted[0] = true);

    MockHealthSystem.HSData data = new MockHealthSystem.HSData(entity, hc, dc);

    system.triggerOnDeathPublic(data);

    assertTrue(callbackExecuted[0]);
  }

  /**
   * Tests that death handling works without registered observers.
   *
   * <p>The entity should still be marked as dead and the On-Death callback should still be executed
   * even when no observer exists.
   */
  @Test
  void triggerOnDeathWithoutObservers() {

    HealthComponent hc = new HealthComponent(0);
    DrawComponent dc = mock(DrawComponent.class);

    boolean[] callbackExecuted = {false};

    hc.onDeath(entity -> callbackExecuted[0] = true);

    MockHealthSystem.HSData data = new MockHealthSystem.HSData(entity, hc, dc);

    system.triggerOnDeathPublic(data);

    assertTrue(hc.alreadyDead());
    assertTrue(callbackExecuted[0]);
  }

  /**
   * Tests that triggering death without a HealthComponent throws an exception.
   *
   * <p>A HealthComponent is required because the death state and callback are stored inside it.
   */
  @Test
  void triggerOnDeathWithoutHealthComponent() {

    DrawComponent dc = mock(DrawComponent.class);

    MockHealthSystem.HSData data = new MockHealthSystem.HSData(entity, null, dc);

    assertThrows(NullPointerException.class, () -> system.triggerOnDeathPublic(data));
  }

  /**
   * Tests that triggering death with null HSData throws an exception.
   *
   * <p>The HealthSystem requires valid data to process entity death.
   */
  @Test
  void triggerOnDeathWithNullHSData() {

    assertThrows(NullPointerException.class, () -> system.triggerOnDeathPublic(null));
  }
}
