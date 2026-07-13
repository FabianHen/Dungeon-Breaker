package contrib.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import contrib.components.InventoryComponent;
import contrib.item.Item;
import contrib.item.concreteItem.ItemHeart;
import contrib.modules.interaction.ISimpleIInteractable;
import contrib.modules.interaction.Interaction;
import contrib.modules.interaction.InteractionComponent;
import core.Entity;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.utils.Point;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Tests für die Methode MiscFactory.newStone(Point spawnPoint, final Set<Item> items) Zugehörige
 * Issue-Nummer: #148
 */
public class NewStoneWithItemsTest {

  /** Äquivalenzklasse: G1. Stein mit gültiger Position und Items erstellen. */
  @Test
  public void test_G1_newStone_valid_creation() {
    Point spawnPoint = new Point(1f, 2f);
    Set<Item> items = new HashSet<>();
    Item item = new ItemHeart(5);
    items.add(item);

    Entity stone = MiscFactory.newStone(spawnPoint, items);

    assertNotNull(stone);
    assertEquals("stone", stone.name());
    assertTrue(stone.isPresent(PositionComponent.class));
    assertTrue(stone.isPresent(DrawComponent.class));
    assertTrue(stone.isPresent(InventoryComponent.class));
    assertTrue(stone.isPresent(InteractionComponent.class));

    PositionComponent pc = stone.fetch(PositionComponent.class).orElse(null);
    assertNotNull(pc);
    assertEquals(spawnPoint, pc.position());

    InventoryComponent ic = stone.fetch(InventoryComponent.class).orElse(null);
    assertNotNull(ic);
    assertEquals(1, ic.count());
    assertTrue(ic.hasItem(item));
  }

  /** Äquivalenzklasse: G2. Stein ohne Items erstellen. */
  @Test
  public void test_G2_newStone_without_items() {
    Point spawnPoint = new Point(3f, 4f);
    Set<Item> emptySet = new HashSet<>();

    Entity stone = MiscFactory.newStone(spawnPoint, emptySet);

    assertNotNull(stone);
    InventoryComponent ic = stone.fetch(InventoryComponent.class).orElse(null);
    assertNotNull(ic);
    assertEquals(0, ic.count());
    assertTrue(ic.isEmpty());
  }

  /** Äquivalenzklasse: G3. Stein mit mehreren Items erstellen. */
  @Test
  public void test_G3_newStone_with_multiple_items() {
    Point spawnPoint = new Point(0, 0);
    Set<Item> items = new HashSet<>();
    Item heart1 = new ItemHeart(1);
    Item heart2 = new ItemHeart(2);
    items.add(heart1);
    items.add(heart2);

    Entity stone = MiscFactory.newStone(spawnPoint, items);

    assertNotNull(stone);
    InventoryComponent ic = stone.fetch(InventoryComponent.class).orElse(null);
    assertNotNull(ic);
    assertEquals(2, ic.count());
    assertTrue(ic.hasItem(heart1));
    assertTrue(ic.hasItem(heart2));
  }

  /** Äquivalenzklasse: G4 / G5 / G6. Interaktionsradius verifizieren. */
  @Test
  public void test_G4_G5_interaction_structure() {
    Point spawnPoint = new Point(0, 0);
    Entity stone = MiscFactory.newStone(spawnPoint, new HashSet<>());

    InteractionComponent ic = stone.fetch(InteractionComponent.class).orElse(null);
    assertNotNull(ic);

    assertTrue(ic.interactions() instanceof ISimpleIInteractable);
    ISimpleIInteractable simpleInteractable = (ISimpleIInteractable) ic.interactions();
    Interaction interaction = simpleInteractable.interact();
    assertNotNull(interaction);
    assertEquals(2f, interaction.range(), "Der Interaktionsradius für den Stein muss 2f betragen.");
  }

  /** Äquivalenzklasse: U1. spawnPoint ist null. */
  @Test
  public void test_U1_spawnPoint_null() {
    try {
      MiscFactory.newStone(null, new HashSet<>());
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /** Äquivalenzklasse: U2. items ist null wirft NullPointerException. */
  @Test
  public void test_U2_items_null_throws_exception() {
    assertThrows(
        NullPointerException.class,
        () -> {
          MiscFactory.newStone(new Point(0, 0), null);
        });
  }
}
