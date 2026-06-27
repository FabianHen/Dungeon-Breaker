package contrib.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;

import contrib.components.CollideComponent;
import contrib.entities.deco.Deco;
import core.utils.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DecoTestSystemModifyOffsetTest {

  private DecoTestSystem system;
  private CollideComponent collideComponent;

  @BeforeEach
  void setUp() {
    system = new DecoTestSystem();
    system.createTestEntity(new Point(0, 0), Deco.Cat);

    collideComponent = system.getTestEntity().fetch(CollideComponent.class).orElseThrow();
  }

  @Test
  void shouldIncreaseXOffset() {
    float oldX = collideComponent.collider().offset().x();

    system.modifyOffset(true, 1);

    assertEquals(oldX + 0.05f, collideComponent.collider().offset().x(), 0.001f);
  }

  @Test
  void shouldDecreaseYOffset() {
    float oldY = collideComponent.collider().offset().y();

    system.modifyOffset(false, -1);

    assertEquals(oldY - 0.05f, collideComponent.collider().offset().y(), 0.001f);
  }

  @Test
  void zeroChangeShouldNotModifyOffset() {
    float oldX = collideComponent.collider().offset().x();

    system.modifyOffset(true, 0);

    assertEquals(oldX, collideComponent.collider().offset().x(), 0.001f);
  }
}
