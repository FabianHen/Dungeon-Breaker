package contrib.systems;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import core.utils.Direction;
import core.components.DrawComponent;
import core.components.PositionComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/** test class */
public class HealthSystemTest {
  private FakeGame game;
  private MockHealthSystem system;
  private Entity entity;

  @BeforeEach
  public void setUp() {
    game = new FakeGame();
    system = new MockHealthSystem(game);
    entity = new Entity("test");
  }

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

  /** G1: Observer wird erfolgreich registriert. */
  @Test
  void registerObserverAddsObserver() {
    FakeHealthObserver observer = new FakeHealthObserver();

    system.registerObserver(observer);

    assertTrue(system.observers.contains(observer));
    assertEquals(1, system.observers.size());
  }

  /** G2: Registrierter Observer wird entfernt. */
  @Test
  void removeObserverRemovesRegisteredObserver() {
    FakeHealthObserver observer = new FakeHealthObserver();
    system.registerObserver(observer);

    system.removeObserver(observer);

    assertFalse(system.observers.contains(observer));
    assertTrue(system.observers.isEmpty());
  }

  /** G3: Entfernte Observer erhalten keine Benachrichtigungen mehr. */
  @Test
  void removedObserverReceivesNoHealthEvents() {
    FakeHealthObserver observer = new FakeHealthObserver();
    system.registerObserver(observer);
    system.removeObserver(observer);

    Entity entity = new Entity();

    HealthComponent hc = new HealthComponent(100);
    hc.receiveHit(new Damage(10, DamageType.PHYSICAL, null));

    DrawComponent dc = mock(DrawComponent.class);

    HealthSystem.HSData data = new HealthSystem.HSData(entity, hc, dc);

    system.applyDamagePublic(data);

    assertFalse(observer.eventReceived());
    assertNull(observer.lastEvent());
    assertNull(observer.lastData());
  }

  /** U1: Registrierung eines Null-Observers. */
  @Test
  void registerNullObserver() {

    assertDoesNotThrow(() -> system.registerObserver(null));

    assertTrue(system.observers.contains(null));
    assertEquals(1, system.observers.size());
  }

  /** U2: Entfernen eines nicht registrierten Observers verändert die Liste nicht. */
  @Test
  void removeObserverThatIsNotRegisteredDoesNothing() {
    FakeHealthObserver observer = new FakeHealthObserver();
    FakeHealthObserver otherObserver = new FakeHealthObserver();
    system.registerObserver(observer);
    system.removeObserver(otherObserver);

    assertEquals(1, system.observers.size());
    assertTrue(system.observers.contains(observer));
  }

  /**
   * G1:
   * Verifies that an entity with a {@link PositionComponent} triggers the
   * death animation including its current view direction.
   *
   * <p>Expected:
   * <ul>
   *     <li>The death signal is sent exactly once.</li>
   *     <li>The current view direction is passed to the DrawComponent.</li>
   * </ul>
   */
  @Test
  void activateDeathAnimationWithPositionComponent() {

    Entity entity = new Entity();

    PositionComponent positionComponent = Mockito.mock(PositionComponent.class);
    Mockito.when(positionComponent.viewDirection()).thenReturn(Direction.UP);

    DrawComponent drawComponent = Mockito.mock(DrawComponent.class);

    entity.add(positionComponent);

    HealthComponent healthComponent = new HealthComponent(100);

    HealthSystem.HSData data =
      new HealthSystem.HSData(entity, healthComponent, drawComponent);

    system.activateDeathAnimationPublic(data);

    Mockito.verify(drawComponent, Mockito.times(1))
      .sendSignal(HealthSystem.DEATH_SIGNAL, Direction.UP);
  }

  /**
   * G2:
   * Verifies that an entity without a {@link PositionComponent}
   * still triggers the death animation.
   *
   * <p>Expected:
   * <ul>
   *     <li>The death signal is sent exactly once.</li>
   *     <li>No direction is passed to the DrawComponent.</li>
   * </ul>
   */
  @Test
  void activateDeathAnimationWithoutPositionComponent() {

    Entity entity = new Entity();

    DrawComponent drawComponent = Mockito.mock(DrawComponent.class);

    HealthComponent healthComponent = new HealthComponent(100);

    HealthSystem.HSData data =
      new HealthSystem.HSData(entity, healthComponent, drawComponent);

    system.activateDeathAnimationPublic(data);

    Mockito.verify(drawComponent, Mockito.times(1))
      .sendSignal(HealthSystem.DEATH_SIGNAL);

    Mockito.verify(drawComponent, Mockito.never())
      .sendSignal(Mockito.eq(HealthSystem.DEATH_SIGNAL), Mockito.any());
  }

  /**
   * U1:
   * Verifies that a {@link NullPointerException} is thrown if the
   * {@link DrawComponent} is missing.
   *
   * <p>Expected:
   * <ul>
   *     <li>The method cannot send the death signal.</li>
   *     <li>A {@link NullPointerException} is thrown.</li>
   * </ul>
   */
  @Test
  void activateDeathAnimationWithoutDrawComponentThrowsException() {

    Entity entity = new Entity();

    HealthComponent healthComponent = new HealthComponent(100);

    HealthSystem.HSData data =
      new HealthSystem.HSData(entity, healthComponent, null);

    assertThrows(
      NullPointerException.class,
      () -> system.activateDeathAnimationPublic(data));
  }

  /**
   * U2:
   * Verifies that passing {@code null} as HSData causes a
   * {@link NullPointerException}.
   *
   * <p>Expected:
   * <ul>
   *     <li>The method throws a {@link NullPointerException}.</li>
   * </ul>
   */
  @Test
  void activateDeathAnimationWithNullHSDataThrowsException() {

    assertThrows(
      NullPointerException.class,
      () -> system.activateDeathAnimationPublic(null));
  }
}




