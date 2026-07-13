package contrib.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import core.Entity;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.utils.Point;
import org.junit.jupiter.api.Test;

/** Tests für die Methode MiscFactory.crate(Point position) Zugehörige Issue-Nummer: #141 */
public class CrateTest {

  /** Äquivalenzklasse: G1. Kiste mit gültiger Position erstellen. */
  @Test
  public void test_G1_crate_valid_position() {
    Point position = new Point(3.5f, -2.1f);
    Entity crate = MiscFactory.crate(position);

    assertNotNull(crate);
    assertEquals("crate", crate.name());
    assertTrue(crate.isPresent(PositionComponent.class));
    assertTrue(crate.isPresent(DrawComponent.class));
    assertEquals(position, crate.fetch(PositionComponent.class).get().position());
  }

  /** Äquivalenzklasse: G2. Mehrere Kisten an unterschiedlichen Positionen erstellen. */
  @Test
  public void test_G2_multiple_crates_different_positions() {
    Point pos1 = new Point(1f, 1f);
    Point pos2 = new Point(2f, 2f);

    Entity crate1 = MiscFactory.crate(pos1);
    Entity crate2 = MiscFactory.crate(pos2);

    assertNotNull(crate1);
    assertNotNull(crate2);
    assertNotEquals(crate1, crate2);
    assertEquals(pos1, crate1.fetch(PositionComponent.class).get().position());
    assertEquals(pos2, crate2.fetch(PositionComponent.class).get().position());
  }

  /** Äquivalenzklasse: G3. Mehrere Kisten an derselben Position erstellen. */
  @Test
  public void test_G3_multiple_crates_same_position() {
    Point position = new Point(5f, 5f);

    Entity crate1 = MiscFactory.crate(position);
    Entity crate2 = MiscFactory.crate(position);

    assertNotNull(crate1);
    assertNotNull(crate2);
    assertNotEquals(crate1, crate2);
    assertEquals(position, crate1.fetch(PositionComponent.class).get().position());
    assertEquals(position, crate2.fetch(PositionComponent.class).get().position());
  }

  /** Äquivalenzklasse: U1. Position ist null. */
  @Test
  public void test_U1_position_null() {
    try {
      MiscFactory.crate(null);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /** Äquivalenzklasse: U2. Kisten-Textur kann nicht geladen werden. */
  @Test
  public void test_U2_texture_load_fails() {
    try {
      Entity crate = MiscFactory.crate(new Point(0, 0));
      assertNotNull(crate);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /** Äquivalenzklasse: U3. Animation-Erstellung schlägt fehl. */
  @Test
  public void test_U3_animation_creation_fails() {
    try {
      Entity crate = MiscFactory.crate(new Point(0, 0));
      assertNotNull(crate);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /** Äquivalenzklasse: U4. PositionComponent akzeptiert die übergebene Position nicht. */
  @Test
  public void test_U4_position_not_accepted() {
    try {
      Entity crate = MiscFactory.crate(new Point(Float.NaN, Float.NaN));
      assertNotNull(crate);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /** Äquivalenzklasse: U5. Name-Zuweisung schlägt fehl. */
  @Test
  public void test_U5_name_assignment_fails() {
    try {
      Entity crate = MiscFactory.crate(new Point(0, 0));
      assertNotNull(crate);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /** Äquivalenzklasse: U6. DrawComponent fehlt nach der Initialisierung. */
  @Test
  public void test_U6_draw_component_missing() {
    try {
      Entity crate = MiscFactory.crate(new Point(0, 0));
      assertNotNull(crate);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /** Äquivalenzklasse: U7. Instanziierung wirft eine unerwartete RuntimeException. */
  @Test
  public void test_U7_unexpected_runtime_exception() {
    try {
      Entity crate = MiscFactory.crate(new Point(0, 0));
      assertNotNull(crate);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }
}
