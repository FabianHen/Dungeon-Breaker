package contrib.systems;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import contrib.components.CollideComponent;
import contrib.entities.deco.Deco;
import core.components.PositionComponent;
import core.utils.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DecoTestSystemCreateTestEntityTest {

  private DecoTestSystem system;

  @BeforeEach
  void setUp() {
    system = new DecoTestSystem();
  }

  @Test
  void shouldCreateEntity() {
    system.createTestEntity(new Point(0, 0), Deco.Cat);

    assertNotNull(system.getTestEntity());
  }

  @Test
  void createdEntityShouldContainCollider() {
    system.createTestEntity(new Point(0, 0), Deco.Cat);

    assertTrue(system.getTestEntity().fetch(CollideComponent.class).isPresent());
  }

  @Test
  void createdEntityShouldContainPosition() {
    system.createTestEntity(new Point(0, 0), Deco.Cat);

    assertTrue(system.getTestEntity().fetch(PositionComponent.class).isPresent());
  }
}
