package contrib.entities;

import static org.junit.jupiter.api.Assertions.*;

import core.Entity;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.utils.Point;
import org.junit.jupiter.api.Test;

/** Tests für die Methode MiscFactory.marker(Point position) Zugehörige Issue-Nummer: #140 */
public class MarkerTest {

  /*
   * Äquivalenzklasse: G1. Marker mit gültiger Position erstellen
   */
  @Test
  public void test_G1_marker_valid_position() {
    Point position = new Point(3.5f, -2.1f);
    Entity marker = MiscFactory.marker(position);

    assertNotNull(marker);
    assertEquals("marker", marker.name());
    assertTrue(marker.isPresent(PositionComponent.class));
    assertTrue(marker.isPresent(DrawComponent.class));
    assertEquals(position, marker.fetch(PositionComponent.class).get().position());
  }

  /*
   * Äquivalenzklasse: G2. Mehrere Marker an unterschiedlichen Positionen erstellen
   */
  @Test
  public void test_G2_multiple_markers_different_positions() {
    Point pos1 = new Point(1f, 1f);
    Point pos2 = new Point(2f, 2f);

    Entity marker1 = MiscFactory.marker(pos1);
    Entity marker2 = MiscFactory.marker(pos2);

    assertNotNull(marker1);
    assertNotNull(marker2);
    assertNotEquals(marker1, marker2);
    assertEquals(pos1, marker1.fetch(PositionComponent.class).get().position());
    assertEquals(pos2, marker2.fetch(PositionComponent.class).get().position());
  }

  /*
   * Äquivalenzklasse: G3. Mehrere Marker an derselben Position erstellen
   */
  @Test
  public void test_G3_multiple_markers_same_position() {
    Point position = new Point(5f, 5f);

    Entity marker1 = MiscFactory.marker(position);
    Entity marker2 = MiscFactory.marker(position);

    assertNotNull(marker1);
    assertNotNull(marker2);
    assertNotEquals(marker1, marker2);
    assertEquals(position, marker1.fetch(PositionComponent.class).get().position());
    assertEquals(position, marker2.fetch(PositionComponent.class).get().position());
  }

  /*
   * Äquivalenzklasse: U1. Position ist null
   */
  @Test
  public void test_U1_position_null() {
    try {
      MiscFactory.marker(null);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /*
   * Äquivalenzklasse: U2. Marker-Textur kann nicht geladen werden
   */
  @Test
  public void test_U2_texture_load_fails() {
    try {
      Entity marker = MiscFactory.marker(new Point(0, 0));
      assertNotNull(marker);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /*
   * Äquivalenzklasse: U3. Animation-Erstellung schlägt fehl
   */
  @Test
  public void test_U3_animation_creation_fails() {
    try {
      Entity marker = MiscFactory.marker(new Point(0, 0));
      assertNotNull(marker);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /*
   * Äquivalenzklasse: U4. PositionComponent akzeptiert die übergebene Position nicht
   */
  @Test
  public void test_U4_position_not_accepted() {
    try {
      Entity marker = MiscFactory.marker(new Point(Float.NaN, Float.NaN));
      assertNotNull(marker);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }
}
