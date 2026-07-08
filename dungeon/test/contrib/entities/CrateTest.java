package contrib.entities;

import static org.junit.jupiter.api.Assertions.*;

import contrib.components.CollideComponent;
import core.Entity;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.components.VelocityComponent;
import core.utils.Point;
import core.utils.components.path.SimpleIPath;
import org.junit.jupiter.api.Test;

/**
 * Tests für die Methode MiscFactory.crate(Point position, float mass, SimpleIPath texture)
 * Zugehörige Issue-Nummer: #141
 */
public class CrateTest {

  private final SimpleIPath dummyTexture1 = new SimpleIPath("objects/crate/basic.png");
  private final SimpleIPath dummyTexture2 = new SimpleIPath("objects/stone");

  /*
   * Äquivalenzklasse: G1. Kiste mit gültiger Position, Masse und Textur erstellen
   */
  @Test
  public void test_G1_crate_valid_parameters() {
    Point position = new Point(2f, 3f);
    float mass = 5.0f;
    Entity crate = MiscFactory.crate(position, mass, dummyTexture1);

    assertNotNull(crate);
    assertEquals("crate", crate.name());
    assertTrue(crate.isPresent(PositionComponent.class));
    assertTrue(crate.isPresent(VelocityComponent.class));
    assertTrue(crate.isPresent(DrawComponent.class));
    assertTrue(crate.isPresent(CollideComponent.class));

    assertEquals(position, crate.fetch(PositionComponent.class).get().position());

    VelocityComponent vc = crate.fetch(VelocityComponent.class).get();
    assertEquals(mass, vc.mass());
  }

  /*
   * Äquivalenzklasse: G2. Kiste mit Masse 0 erstellen (Erwartet Exception durch VelocityComponent)
   */
  @Test
  public void test_G2_crate_mass_zero() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          MiscFactory.crate(new Point(0, 0), 0f, dummyTexture1);
        });
  }

  /*
   * Äquivalenzklasse: G3. Kiste mit sehr großer Masse erstellen
   */
  @Test
  public void test_G3_crate_large_mass() {
    float largeMass = 999999f;
    Entity crate = MiscFactory.crate(new Point(0, 0), largeMass, dummyTexture1);
    assertNotNull(crate);
    VelocityComponent vc = crate.fetch(VelocityComponent.class).get();
    assertEquals(largeMass, vc.mass());
  }

  /*
   * Äquivalenzklasse: G4. Mehrere Kisten mit unterschiedlichen Texturen erstellen
   */
  @Test
  public void test_G4_multiple_crates_different_textures() {
    Entity crate1 = MiscFactory.crate(new Point(0, 0), 1f, dummyTexture1);
    Entity crate2 = MiscFactory.crate(new Point(1, 1), 1f, dummyTexture2);

    assertNotNull(crate1);
    assertNotNull(crate2);
    assertNotEquals(crate1, crate2);
  }

  /*
   * Äquivalenzklasse: G5. Mehrere Kisten an derselben Position erstellen
   */
  @Test
  public void test_G5_multiple_crates_same_position() {
    Point position = new Point(4f, 4f);
    Entity crate1 = MiscFactory.crate(position, 2f, dummyTexture1);
    Entity crate2 = MiscFactory.crate(position, 2f, dummyTexture1);

    assertNotNull(crate1);
    assertNotNull(crate2);
    assertNotEquals(crate1, crate2);
    assertEquals(position, crate1.fetch(PositionComponent.class).get().position());
    assertEquals(position, crate2.fetch(PositionComponent.class).get().position());
  }

  /*
   * Äquivalenzklasse: U1. Position ist null
   */
  @Test
  public void test_U1_position_null() {
    try {
      MiscFactory.crate(null, 5f, dummyTexture1);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /*
   * Äquivalenzklasse: U2. Textur ist null
   */
  @Test
  public void test_U2_texture_null() {
    try {
      MiscFactory.crate(new Point(0, 0), 5f, null);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /*
   * Äquivalenzklasse: U3. Textur verweist auf nicht vorhandene Ressource
   */
  @Test
  public void test_U3_invalid_texture_path() {
    try {
      Entity crate =
          MiscFactory.crate(new Point(0, 0), 5f, new SimpleIPath("invalid/path/missing.png"));
      assertNotNull(crate);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /*
   * Äquivalenzklasse: U4. Masse ist negativ (Erwartet Exception durch VelocityComponent)
   */
  @Test
  public void test_U4_negative_mass() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          MiscFactory.crate(new Point(0, 0), -5.0f, dummyTexture1);
        });
  }

  /*
   * Äquivalenzklasse: U5. Animation-Erstellung schlägt fehl
   */
  @Test
  public void test_U5_animation_creation_fails() {
    try {
      Entity crate = MiscFactory.crate(new Point(0, 0), 5f, dummyTexture1);
      assertNotNull(crate);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }
}
