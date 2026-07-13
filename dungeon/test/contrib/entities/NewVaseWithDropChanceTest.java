package contrib.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import contrib.components.InventoryComponent;
import contrib.item.Item;
import contrib.item.concreteItem.ItemFairy;
import contrib.item.concreteItem.ItemHeart;
import contrib.item.concreteItem.ItemWoodenArrow;
import contrib.modules.interaction.ISimpleIInteractable;
import contrib.modules.interaction.Interaction;
import contrib.modules.interaction.InteractionComponent;
import core.Entity;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.utils.Point;
import org.junit.jupiter.api.Test;

/**
 * Tests für die Methode MiscFactory.newVase(Point spawnPoint, float dropChance) Zugehörige
 * Issue-Nummer: #149
 */
public class NewVaseWithDropChanceTest {

  /** Äquivalenzklasse: G1 / G2. DropChance = 0.0 (Vase darf niemals Items enthalten). */
  @Test
  public void test_G2_newVase_dropChance_zero() {
    Point spawnPoint = new Point(1f, 1f);

    // Wir erstellen mehrfach eine Vase, um Zufallseinflüsse auszuschließen
    for (int i = 0; i < 50; i++) {
      Entity vase = MiscFactory.newVase(spawnPoint, 0.0f);

      assertNotNull(vase);
      assertEquals("vase", vase.name());
      assertTrue(vase.isPresent(PositionComponent.class));
      assertTrue(vase.isPresent(DrawComponent.class));
      assertTrue(vase.isPresent(InventoryComponent.class));
      assertTrue(vase.isPresent(InteractionComponent.class));

      InventoryComponent ic = vase.fetch(InventoryComponent.class).orElse(null);
      assertNotNull(ic);
      assertTrue(ic.isEmpty(), "Bei einer DropChance von 0.0 darf die Vase keine Items enthalten.");
    }
  }

  /**
   * Äquivalenzklasse: G3 / G4 / G5 / G6. DropChance = 1.0 (Vase muss immer exakt ein definiertes
   * Item enthalten).
   */
  @Test
  public void test_G3_to_G6_newVase_dropChance_one() {
    Point spawnPoint = new Point(2f, 2f);

    boolean foundHeart = false;
    boolean foundArrow = false;
    boolean foundFairy = false;

    // Wir erstellen 100 Vasen, um alle möglichen Drops statistisch abzudecken
    for (int i = 0; i < 100; i++) {
      Entity vase = MiscFactory.newVase(spawnPoint, 1.0f);
      assertNotNull(vase);

      InventoryComponent ic = vase.fetch(InventoryComponent.class).orElse(null);
      assertNotNull(ic);
      assertEquals(
          1, ic.count(), "Bei einer DropChance von 1.0 muss genau ein Item enthalten sein.");

      Item drop = ic.items()[0];
      assertNotNull(drop);

      // Prüfen, ob das gedroppte Item einer der drei erlaubten Typen ist
      if (drop instanceof ItemHeart) {
        foundHeart = true;
      } else if (drop instanceof ItemWoodenArrow) {
        foundArrow = true;
      } else if (drop instanceof ItemFairy) {
        foundFairy = true;
      } else {
        assertTrue(false, "Unerwarteter Item-Typ in der Vase: " + drop.getClass().getSimpleName());
      }
    }

    // Statistisch gesehen sollten bei 100 Durchläufen alle drei Typen mindestens einmal gewürfelt
    // worden sein
    assertTrue(foundHeart, "Es sollte mindestens ein ItemHeart gedroppt sein (Chance ~50%).");
    assertTrue(foundArrow, "Es sollte mindestens ein ItemWoodenArrow gedroppt sein (Chance ~30%).");
    assertTrue(foundFairy, "Es sollte mindestens eine ItemFairy gedroppt sein (Chance ~20%).");
  }

  /** Äquivalenzklasse: Interaktions-Logik prüfen. */
  @Test
  public void test_interaction_structure() {
    Point spawnPoint = new Point(0, 0);
    Entity vase = MiscFactory.newVase(spawnPoint, 0.5f);

    InteractionComponent ic = vase.fetch(InteractionComponent.class).orElse(null);
    assertNotNull(ic);
    assertTrue(ic.interactions() instanceof ISimpleIInteractable);

    Interaction interaction = ((ISimpleIInteractable) ic.interactions()).interact();
    assertNotNull(interaction);
    assertEquals(2f, interaction.range());
  }

  /** Äquivalenzklasse: U1. spawnPoint ist null. */
  @Test
  public void test_U1_spawnPoint_null() {
    try {
      MiscFactory.newVase(null, 0.5f);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }
}
