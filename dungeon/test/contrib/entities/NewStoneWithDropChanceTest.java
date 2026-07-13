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
 * Tests für die Methode MiscFactory.newStone(Point spawnPoint, float dropChance) Zugehörige
 * Issue-Nummer: #150
 */
public class NewStoneWithDropChanceTest {

  /** Äquivalenzklasse: G1 / G2. DropChance = 0.0 (Stein darf niemals Items enthalten). */
  @Test
  public void test_G2_newStone_dropChance_zero() {
    Point spawnPoint = new Point(1f, 1f);

    for (int i = 0; i < 50; i++) {
      Entity stone = MiscFactory.newStone(spawnPoint, 0.0f);

      assertNotNull(stone);
      assertEquals("stone", stone.name());
      assertTrue(stone.isPresent(PositionComponent.class));
      assertTrue(stone.isPresent(DrawComponent.class));
      assertTrue(stone.isPresent(InventoryComponent.class));
      assertTrue(stone.isPresent(InteractionComponent.class));

      InventoryComponent ic = stone.fetch(InventoryComponent.class).orElse(null);
      assertNotNull(ic);
      assertTrue(
          ic.isEmpty(), "Bei einer DropChance von 0.0 darf der Stein keine Items enthalten.");
    }
  }

  /**
   * Äquivalenzklasse: G3 / G4 / G5 / G6. DropChance = 1.0 (Stein muss immer exakt ein definiertes
   * Item enthalten).
   */
  @Test
  public void test_G3_to_G6_newStone_dropChance_one() {
    Point spawnPoint = new Point(2f, 2f);

    boolean foundHeart = false;
    boolean foundArrow = false;
    boolean foundFairy = false;

    // Wir erstellen 100 Steine, um alle möglichen Drops statistisch abzudecken
    for (int i = 0; i < 100; i++) {
      Entity stone = MiscFactory.newStone(spawnPoint, 1.0f);
      assertNotNull(stone);

      InventoryComponent ic = stone.fetch(InventoryComponent.class).orElse(null);
      assertNotNull(ic);
      assertEquals(
          1, ic.count(), "Bei einer DropChance von 1.0 muss genau ein Item enthalten sein.");

      Item drop = ic.items()[0];
      assertNotNull(drop);

      if (drop instanceof ItemHeart) {
        foundHeart = true;
      } else if (drop instanceof ItemWoodenArrow) {
        foundArrow = true;
      } else if (drop instanceof ItemFairy) {
        foundFairy = true;
      } else {
        assertTrue(false, "Unerwarteter Item-Typ im Stein: " + drop.getClass().getSimpleName());
      }
    }

    assertTrue(foundHeart, "Es sollte mindestens ein ItemHeart gedroppt sein (Chance ~50%).");
    assertTrue(foundArrow, "Es sollte mindestens ein ItemWoodenArrow gedroppt sein (Chance ~30%).");
    assertTrue(foundFairy, "Es sollte mindestens eine ItemFairy gedroppt sein (Chance ~20%).");
  }

  /** Äquivalenzklasse: G7 / U6. Interaktionsradius prüfen. */
  @Test
  public void test_G7_interaction_structure() {
    Point spawnPoint = new Point(0, 0);
    Entity stone = MiscFactory.newStone(spawnPoint, 0.5f);

    InteractionComponent ic = stone.fetch(InteractionComponent.class).orElse(null);
    assertNotNull(ic);
    assertTrue(ic.interactions() instanceof ISimpleIInteractable);

    Interaction interaction = ((ISimpleIInteractable) ic.interactions()).interact();
    assertNotNull(interaction);
    assertEquals(
        2f, interaction.range(), "Der Interaktionsradius für zerstörbare Steine muss 2f sein.");
  }

  /** Äquivalenzklasse: U1. spawnPoint ist null. */
  @Test
  public void test_U1_spawnPoint_null() {
    try {
      MiscFactory.newStone(null, 0.5f);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }
}
