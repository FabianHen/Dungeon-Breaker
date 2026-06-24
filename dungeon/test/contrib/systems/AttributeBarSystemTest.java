package contrib.systems;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import contrib.components.HealthComponent;
import contrib.components.ManaComponent;
import contrib.components.StaminaComponent;
import core.Entity;
import core.Game;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.utils.Point;
import core.utils.components.draw.state.StateMachine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/** Unit tests for {@link AttributeBarSystem}. */
class AttributeBarSystemTest {

  private AttributeBarSystem system;
  private Entity entity;
  private PositionComponent positionComponent;
  private DrawComponent drawComponent;

  @BeforeEach
  void setUp() {
    Game.removeAllEntities();
    Game.removeAllSystems();

    system = new AttributeBarSystem();
    Game.add(system);

    entity = new Entity();
    positionComponent = new PositionComponent(new Point(0, 0));
    drawComponent = new DrawComponent(Mockito.mock(StateMachine.class));

    entity.add(positionComponent);
    entity.add(drawComponent);

    Game.add(entity);
  }

  @AfterEach
  void tearDown() {
    Game.removeAllEntities();
    Game.removeAllSystems();
    Game.currentLevel(null);
  }

  /**
   * Verifies that the system executes without crashing when no BarDisplayable components are
   * present on the entity.
   */
  @Test
  void testExecuteWithoutBars() {
    assertDoesNotThrow(() -> system.execute());
  }

  /**
   * Verifies that the system executes without crashing when a HealthComponent is added to the
   * entity (which triggers the creation of the bar in headless mode, logging a warning but not
   * crashing).
   */
  @Test
  void testExecuteWithHealthComponent() {
    HealthComponent hc = new HealthComponent(100, e -> {});
    entity.add(hc);

    assertDoesNotThrow(() -> system.execute());
  }

  /** Verifies that multiple BarDisplayable components are handled correctly without crashing. */
  @Test
  void testExecuteWithMultipleComponents() {
    HealthComponent hc = new HealthComponent(100, e -> {});
    ManaComponent mc = new ManaComponent(100f, 50f, 1f);
    StaminaComponent sc = new StaminaComponent(100f, 80f, 1f);

    entity.add(hc);
    entity.add(mc);
    entity.add(sc);

    assertDoesNotThrow(() -> system.execute());
  }

  /**
   * Verifies that when an entity is removed, the system cleanup runs without throwing any
   * exceptions.
   */
  @Test
  void testOnEntityRemoveCleanup() {
    HealthComponent hc = new HealthComponent(100, e -> {});
    entity.add(hc);

    // Run once to potentially trigger mapping (which fails gracefully in headless mode)
    system.execute();

    // Remove the entity and trigger cleanup
    Game.remove(entity);
    assertDoesNotThrow(() -> system.triggerOnRemove(entity));
  }
}
