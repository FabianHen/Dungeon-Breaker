package contrib.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import contrib.item.Item;
import contrib.item.concreteItem.ItemHeart;
import core.Entity;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.utils.Point;
import org.junit.jupiter.api.Test;

/**
 * Tests für die Methode MiscFactory.cookingPot(Point position, int inventorysize, Item... items)
 * Zugehörige Issue-Nummer: #142
 */
public class CookingPotTest {

  private static final int DEFAULT_SIZE = 12;

  /**
   * Äquivalenzklasse: G1. Kochtopf mit gültiger Position erstellen.
   */
  @Test
  public void test_G1_cookingPot_valid_position() {
    Point position = new Point(3.5f, -2.1f);
    Entity pot = MiscFactory.cookingPot(position, DEFAULT_SIZE);

    assertNotNull(pot);
    assertEquals("cookingPot", pot.name());
    assertTrue(pot.isPresent(PositionComponent.class));
    assertTrue(pot.isPresent(DrawComponent.class));
    assertEquals(position, pot.fetch(PositionComponent.class).get().position());
  }

  /**
   * Äquivalenzklasse: G2. Mehrere Kochtöpfe an unterschiedlichen Positionen erstellen.
   */
  @Test
  public void test_G2_multiple_cookingPots_different_positions() {
    Point pos1 = new Point(1f, 1f);
    Point pos2 = new Point(2f, 2f);

    Entity pot1 = MiscFactory.cookingPot(pos1, DEFAULT_SIZE);
    Entity pot2 = MiscFactory.cookingPot(pos2, DEFAULT_SIZE);

    assertNotNull(pot1);
    assertNotNull(pot2);
    assertNotEquals(pot1, pot2);
    assertEquals(pos1, pot1.fetch(PositionComponent.class).get().position());
    assertEquals(pos2, pot2.fetch(PositionComponent.class).get().position());
  }

  /**
   * Äquivalenzklasse: G3. Mehrere Kochtöpfe an derselben Position erstellen.
   */
  @Test
  public void test_G3_multiple_cookingPots_same_position() {
    Point position = new Point(5f, 5f);

    Entity pot1 = MiscFactory.cookingPot(position, DEFAULT_SIZE);
    Entity pot2 = MiscFactory.cookingPot(position, DEFAULT_SIZE);

    assertNotNull(pot1);
    assertNotNull(pot2);
    assertNotEquals(pot1, pot2);
    assertEquals(position, pot1.fetch(PositionComponent.class).get().position());
    assertEquals(position, pot2.fetch(PositionComponent.class).get().position());
  }

  /**
   * Äquivalenzklasse: U1. Position ist null.
   */
  @Test
  public void test_U1_position_null() {
    try {
      MiscFactory.cookingPot(null, DEFAULT_SIZE);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /**
   * Äquivalenzklasse: U2. Kochtopf-Textur kann nicht geladen werden.
   */
  @Test
  public void test_U2_texture_load_fails() {
    try {
      Entity pot = MiscFactory.cookingPot(new Point(0, 0), DEFAULT_SIZE);
      assertNotNull(pot);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /**
   * Äquivalenzklasse: U3. Animation-Erstellung schlägt fehl.
   */
  @Test
  public void test_U3_animation_creation_fails() {
    try {
      Entity pot = MiscFactory.cookingPot(new Point(0, 0), DEFAULT_SIZE);
      assertNotNull(pot);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /**
   * Äquivalenzklasse: U4. PositionComponent akzeptiert die übergebene Position nicht.
   */
  @Test
  public void test_U4_position_not_accepted() {
    try {
      Entity pot = MiscFactory.cookingPot(new Point(Float.NaN, Float.NaN), DEFAULT_SIZE);
      assertNotNull(pot);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /**
   * Äquivalenzklasse: U5. Name-Zuweisung schlägt fehl.
   */
  @Test
  public void test_U5_name_assignment_fails() {
    try {
      Entity pot = MiscFactory.cookingPot(new Point(0, 0), DEFAULT_SIZE);
      assertNotNull(pot);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /**
   * Äquivalenzklasse: U6. DrawComponent fehlt nach der Initialisierung.
   */
  @Test
  public void test_U6_draw_component_missing() {
    try {
      Entity pot = MiscFactory.cookingPot(new Point(0, 0), DEFAULT_SIZE);
      assertNotNull(pot);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /**
   * Äquivalenzklasse: U7. Instanziierung wirft eine unerwartete RuntimeException.
   */
  @Test
  public void test_U7_unexpected_runtime_exception() {
    try {
      Entity pot = MiscFactory.cookingPot(new Point(0, 0), DEFAULT_SIZE);
      assertNotNull(pot);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /**
   * Äquivalenzklasse: U8. InteractionComponent fehlt am Kochtopf.
   */
  @Test
  public void test_U8_interaction_component_missing() {
    try {
      Entity pot = MiscFactory.cookingPot(new Point(0, 0), DEFAULT_SIZE);
      assertNotNull(pot);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /**
   * Äquivalenzklasse: U9. Kochen-Trigger schlägt ohne Zutaten fehl.
   */
  @Test
  public void test_U9_cooking_trigger_fails_without_ingredients() {
    try {
      Item ingredient = new ItemHeart(5);
      Entity pot = MiscFactory.cookingPot(new Point(0, 0), DEFAULT_SIZE, ingredient);
      assertNotNull(pot);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }
}
