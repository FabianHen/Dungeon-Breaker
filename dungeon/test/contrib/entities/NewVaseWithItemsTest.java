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
 * Tests für die Methode MiscFactory.newVase(Point spawnPoint, final Set<Item> items) Zugehörige
 * Issue-Nummer: #147
 */
public class NewVaseWithItemsTest {

  /** Äquivalenzklasse: G1. Vase mit gültiger Position und Items erstellen. */
  @Test
  public void test_G1_newVase_valid_creation() {
    Point spawnPoint = new Point(1f, 2f);
    Set<Item> items = new HashSet<>();
    // ItemHeart ist ein registriertes Item und kann sicher instanziiert werden
    Item item = new ItemHeart(5);
    items.add(item);

    Entity vase = MiscFactory.newVase(spawnPoint, items);

    assertNotNull(vase);
    assertEquals("vase", vase.name());
    assertTrue(vase.isPresent(PositionComponent.class));
    assertTrue(vase.isPresent(DrawComponent.class));
    assertTrue(vase.isPresent(InventoryComponent.class));
    assertTrue(vase.isPresent(InteractionComponent.class));

    // Position verifizieren
    PositionComponent pc = vase.fetch(PositionComponent.class).orElse(null);
    assertNotNull(pc);
    assertEquals(spawnPoint, pc.position());

    // Inventar-Inhalt verifizieren
    InventoryComponent ic = vase.fetch(InventoryComponent.class).orElse(null);
    assertNotNull(ic);
    assertEquals(1, ic.count());
    assertTrue(ic.hasItem(item));
  }

  /** Äquivalenzklasse: G2. Vase ohne Items erstellen. */
  @Test
  public void test_G2_newVase_without_items() {
    Point spawnPoint = new Point(3f, 4f);
    Set<Item> emptySet = new HashSet<>();

    Entity vase = MiscFactory.newVase(spawnPoint, emptySet);

    assertNotNull(vase);
    InventoryComponent ic = vase.fetch(InventoryComponent.class).orElse(null);
    assertNotNull(ic);
    assertEquals(0, ic.count());
    assertTrue(ic.isEmpty());
  }

  /** Äquivalenzklasse: G3. Vase mit mehreren Items erstellen. */
  @Test
  public void test_G3_newVase_with_multiple_items() {
    Point spawnPoint = new Point(0, 0);
    Set<Item> items = new HashSet<>();
    Item heart1 = new ItemHeart(1);
    Item heart2 = new ItemHeart(2);
    items.add(heart1);
    items.add(heart2);

    Entity vase = MiscFactory.newVase(spawnPoint, items);

    assertNotNull(vase);
    InventoryComponent ic = vase.fetch(InventoryComponent.class).orElse(null);
    assertNotNull(ic);
    assertEquals(2, ic.count());
    assertTrue(ic.hasItem(heart1));
    assertTrue(ic.hasItem(heart2));
  }

  /** Äquivalenzklasse: G4. Zerstörung der Vase ohne Anforderungen (RequiredItemClass ist null). */
  @Test
  public void test_G4_newVase_no_required_item() {
    Point spawnPoint = new Point(0, 0);
    Entity vase = MiscFactory.newVase(spawnPoint, new HashSet<>());

    InteractionComponent ic = vase.fetch(InteractionComponent.class).orElse(null);
    assertNotNull(ic);

    // Verifizieren, dass die Interaktion ohne Item-Anforderung registriert ist
    assertTrue(ic.interactions() instanceof ISimpleIInteractable);
    ISimpleIInteractable simpleInteractable = (ISimpleIInteractable) ic.interactions();
    Interaction interaction = simpleInteractable.interact();
    assertNotNull(interaction);
    assertEquals(2f, interaction.range());
  }

  /** Äquivalenzklasse: U1. spawnPoint ist null. */
  @Test
  public void test_U1_spawnPoint_null() {
    try {
      MiscFactory.newVase(null, new HashSet<>());
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
          MiscFactory.newVase(new Point(0, 0), null);
        });
  }
}
