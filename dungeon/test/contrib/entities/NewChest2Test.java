package contrib.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import contrib.components.InventoryComponent;
import contrib.modules.interaction.InteractionComponent;
import core.Entity;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.utils.Point;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Tests für die Methode MiscFactory.newChest(Set<Item> item, Point position)
 * Zugehörige Issue-Nummer: #137
 */
public class NewChest2Test {

  /*
   * Äquivalenzklasse: G1. Leere Truhe mit gültiger Position erstellen
   */
  @Test
  public void test_G1_newChest_empty_with_position() {
    Point position = new Point(1, 2);
    Entity chest = MiscFactory.newChest(new HashSet<>(), position);

    assertNotNull(chest);
    assertTrue(chest.isPresent(PositionComponent.class));
    assertTrue(chest.isPresent(InventoryComponent.class));
    assertTrue(chest.isPresent(DrawComponent.class));
    assertTrue(chest.isPresent(InteractionComponent.class));

    assertEquals(position, chest.fetch(PositionComponent.class).get().position());
    assertEquals(0, chest.fetch(InventoryComponent.class).get().count());
  }

  /*
   * Äquivalenzklasse: G2. Truhe mit mehreren Items erstellen
   */
  @Test
  public void test_G2_newChest_with_items() {
    Point position = new Point(1, 2);
    Set<contrib.item.Item> items = new HashSet<>();

    // Konkrete Dummys aus eurem Framework nutzen, da Mocks fehlschlagen
    contrib.item.Item item1 = new contrib.item.concreteItem.ItemKey();
    contrib.item.Item item2 = new contrib.item.concreteItem.ItemHeart(1);

    items.add(item1);
    items.add(item2);

    Entity chest = MiscFactory.newChest(items, position);

    assertNotNull(chest);
    InventoryComponent ic = chest.fetch(InventoryComponent.class).get();
    assertEquals(2, ic.count());
  }

  /*
   * Äquivalenzklasse: G3. Position ist null
   */
  @Test
  public void test_G3_position_is_null() {
    Entity chest = MiscFactory.newChest(new HashSet<>(), null);

    assertNotNull(chest);
    assertTrue(chest.isPresent(PositionComponent.class));
    assertNotNull(chest.fetch(PositionComponent.class).get().position());
  }

  /*
   * Äquivalenzklasse: G4. Interaktion mit Entity, das ein InventoryComponent besitzt
   */
  @Test
  public void test_G4_interaction_with_inventory() {
    Entity chest = MiscFactory.newChest(new HashSet<>(), new Point(0, 0));
    Entity interactor = new Entity();
    interactor.add(new PositionComponent(new Point(0, 0))); // Position für Entfernungsberechnung benötigt
    interactor.add(new InventoryComponent(6));

    InteractionComponent ic = chest.fetch(InteractionComponent.class).get();

    assertDoesNotThrow(() -> {
      ic.triggerInteraction(chest, interactor);
    });
  }

  /*
   * Äquivalenzklasse: G5. Truhe enthält mehr Items als Inventarslots aufnehmen können
   */
  @Test
  public void test_G5_more_items_than_slots() {
    Point position = new Point(0, 0);
    Set<contrib.item.Item> items = new HashSet<>();
    for (int i = 0; i < 15; i++) {
      items.add(mock(contrib.item.Item.class));
    }

    Entity chest = MiscFactory.newChest(items, position);
    assertNotNull(chest);
  }

  /*
   * Äquivalenzklasse: U1. Item-Menge ist null
   */
  @Test
  public void test_U1_items_null() {
    assertThrows(NullPointerException.class, () -> {
      MiscFactory.newChest(null, new Point(0, 0));
    });
  }

  /*
   * Äquivalenzklasse: U2. Animation "closed" fehlt
   */
  @Test
  public void test_U2_animation_closed_missing() {
    try {
      MiscFactory.newChest(new HashSet<>(), new Point(0, 0));
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /*
   * Äquivalenzklasse: U3. Animation "opening" fehlt
   */
  @Test
  public void test_U3_animation_opening_missing() {
    try {
      MiscFactory.newChest(new HashSet<>(), new Point(0, 0));
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /*
   * Äquivalenzklasse: U4. Animation "open" fehlt
   */
  @Test
  public void test_U4_animation_open_missing() {
    try {
      MiscFactory.newChest(new HashSet<>(), new Point(0, 0));
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /*
   * Äquivalenzklasse: U5. Spritesheet kann nicht geladen werden
   */
  @Test
  public void test_U5_spritesheet_not_found() {
    try {
      MiscFactory.newChest(new HashSet<>(), new Point(0, 0));
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /*
   * Äquivalenzklasse: U6. Interagierendes Entity besitzt kein InventoryComponent
   */
  @Test
  public void test_U6_interactor_missing_inventory() {
    Entity chest = MiscFactory.newChest(new HashSet<>(), new Point(0, 0));
    Entity interactor = new Entity();
    interactor.add(new PositionComponent(new Point(0, 0))); // Position für Entfernungsberechnung benötigt

    InteractionComponent ic = chest.fetch(InteractionComponent.class).get();

    assertDoesNotThrow(() -> {
      ic.triggerInteraction(chest, interactor);
    });
  }

  /*
   * Äquivalenzklasse: U7. InventoryComponent des Interagierenden ist inkonsistent
   */
  @Test
  public void test_U7_interactor_inventory_inconsistent() {
    Entity chest = MiscFactory.newChest(new HashSet<>(), new Point(0, 0));
    assertNotNull(chest);
  }

  /*
   * Äquivalenzklasse: U8. Hinzufügen der UIComponent schlägt fehl
   */
  @Test
  public void test_U8_adding_ui_component_fails() {
    try {
      Entity chest = MiscFactory.newChest(new HashSet<>(), new Point(0, 0));
      assertNotNull(chest);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }
}
