package contrib.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;

import contrib.components.CollideComponent;
import contrib.entities.deco.Deco;
import core.utils.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DecoTestSystemModifySizeTest {

  private DecoTestSystem system;
  private CollideComponent collideComponent;

  @BeforeEach
  void setUp() {
    system = new DecoTestSystem();
    system.createTestEntity(new Point(0, 0), Deco.Cat);

    collideComponent = system.getTestEntity().fetch(CollideComponent.class).orElseThrow();
  }

  @Test
  void shouldIncreaseWidth() {
    float oldWidth = collideComponent.collider().width();

    system.modifySize(true, 1);

    assertEquals(oldWidth + 0.05f, collideComponent.collider().width(), 0.001f);
  }

  @Test
  void shouldDecreaseHeight() {
    float oldHeight = collideComponent.collider().height();

    system.modifySize(false, -1);

    assertEquals(oldHeight - 0.05f, collideComponent.collider().height(), 0.001f);
  }

  @Test
  void zeroChangeShouldNotModifyWidth() {
    float oldWidth = collideComponent.collider().width();

    system.modifySize(true, 0);

    assertEquals(oldWidth, collideComponent.collider().width(), 0.001f);
  }
}
