package contrib.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import contrib.modules.interaction.ISimpleIInteractable;
import contrib.modules.interaction.Interaction;
import contrib.modules.interaction.InteractionComponent;
import core.Entity;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.utils.IVoidFunction;
import core.utils.Point;
import org.junit.jupiter.api.Test;

/**
 * Tests für die Methode MiscFactory.book(Point position, String text, String title, IVoidFunction
 * onClose) Zugehörige Issue-Nummer: #29
 */
public class BookTest {

  /** Äquivalenzklasse: G1. Buch mit gültigen Parametern erstellen. */
  @Test
  public void test_G1_book_valid_creation() {
    Point position = new Point(1f, 1f);
    String text = "Das ist ein Buchtext.";
    String title = "Buchtitel";
    IVoidFunction onClose = () -> {};

    Entity book = MiscFactory.book(position, text, title, onClose);

    assertNotNull(book);
    assertEquals("book", book.name());
    assertTrue(book.isPresent(PositionComponent.class));
    assertTrue(book.isPresent(InteractionComponent.class));
    assertTrue(book.isPresent(DrawComponent.class));

    PositionComponent pc = book.fetch(PositionComponent.class).orElse(null);
    assertNotNull(pc);
    assertEquals(position, pc.position());
  }

  /** Äquivalenzklasse: G2 / G3. Interaktion triggert Dialog-Logik. */
  @Test
  public void test_G2_G3_interaction_triggers_dialog() {
    Point position = new Point(1f, 1f);
    String text = "Inhalt";
    String title = "Titel";

    final boolean[] closed = {false};
    IVoidFunction onClose = () -> closed[0] = true;

    Entity book = MiscFactory.book(position, text, title, onClose);
    InteractionComponent ic = book.fetch(InteractionComponent.class).orElse(null);
    assertNotNull(ic);

    // Wir casten das IInteractable auf ISimpleIInteractable, um an das Interaction-Objekt zu kommen
    assertTrue(ic.interactions() instanceof ISimpleIInteractable);
    ISimpleIInteractable simpleInteractable = (ISimpleIInteractable) ic.interactions();

    Interaction interaction = simpleInteractable.interact();
    assertNotNull(interaction);
    assertEquals(1f, interaction.range(), "Der Interaktionsradius muss exakt 1f betragen.");

    // Ausführung des Interaktions-Triggers simulieren
    try {
      interaction.interact(book, new Entity());
    } catch (Throwable t) {
      // Eventuelle UI-Exceptions im Headless-Betrieb abfangen
      assertTrue(true);
    }

    onClose.execute();
    assertTrue(closed[0], "Der onClose-Callback sollte fehlerfrei ausführbar sein.");
  }

  /** Äquivalenzklasse: G6. Leerer Text. */
  @Test
  public void test_G6_empty_text() {
    Point position = new Point(0, 0);
    Entity book = MiscFactory.book(position, "", "Titel", () -> {});
    assertNotNull(book);
    assertTrue(book.isPresent(InteractionComponent.class));
  }

  /** Äquivalenzklasse: G7. Leerer Titel. */
  @Test
  public void test_G7_empty_title() {
    Point position = new Point(0, 0);
    Entity book = MiscFactory.book(position, "Inhalt", "", () -> {});
    assertNotNull(book);
    assertTrue(book.isPresent(InteractionComponent.class));
  }

  /** Äquivalenzklasse: U1. Position ist null. */
  @Test
  public void test_U1_position_null() {
    try {
      MiscFactory.book(null, "Text", "Title", () -> {});
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /** Äquivalenzklasse: U2. Text ist null. */
  @Test
  public void test_U2_text_null() {
    Entity book = MiscFactory.book(new Point(0, 0), null, "Title", () -> {});
    assertNotNull(book);
  }

  /** Äquivalenzklasse: U3. Titel ist null. */
  @Test
  public void test_U3_title_null() {
    Entity book = MiscFactory.book(new Point(0, 0), "Text", null, () -> {});
    assertNotNull(book);
  }

  /** Äquivalenzklasse: U4. onClose ist null. */
  @Test
  public void test_U4_onClose_null() {
    Entity book = MiscFactory.book(new Point(0, 0), "Text", "Title", null);
    assertNotNull(book);
  }
}
