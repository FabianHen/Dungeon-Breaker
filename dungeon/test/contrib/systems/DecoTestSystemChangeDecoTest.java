package contrib.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import contrib.entities.deco.Deco;
import core.utils.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DecoTestSystemChangeDecoTest {

  private DecoTestSystem system;

  @BeforeEach
  void setUp() {
    system = new DecoTestSystem();
    system.createTestEntity(new Point(0, 0));
  }

  @Test
  void shouldSelectNextDeco() {
    Deco oldDeco = system.getCurrentDeco();

    system.changeDeco(1);

    assertNotEquals(oldDeco, system.getCurrentDeco());
  }

  @Test
  void shouldSelectPreviousDeco() {
    Deco oldDeco = system.getCurrentDeco();

    system.changeDeco(-1);

    assertNotEquals(oldDeco, system.getCurrentDeco());
  }

  @Test
  void changeByZeroShouldKeepCurrentDeco() {
    Deco oldDeco = system.getCurrentDeco();

    system.changeDeco(0);

    assertEquals(oldDeco, system.getCurrentDeco());
  }
}
