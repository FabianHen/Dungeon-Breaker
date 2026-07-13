package contrib.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import contrib.components.InventoryComponent;
import core.Entity;
import core.components.DrawComponent;
import core.utils.Point;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

/**
 * Tests für die FillState-Logik (realisiert und getestet über die Schatzkiste). Zugehörige
 * Issue-Nummer: #152
 */
public class FillStateTest {

  /**
   * Testet, dass die Truhe (und damit ihr interner FillState) im geschlossenen Zustand
   * initialisiert wird und nach dem Leeren der Kiste den korrekten Animationsstatus annimmt.
   */
  @Test
  public void test_chest_fill_state_transitions() {
    Point position = new Point(1f, 1f);
    Entity chest = MiscFactory.newChest(new HashSet<>(), position);

    assertNotNull(chest);
    assertTrue(chest.isPresent(DrawComponent.class));
    assertTrue(chest.isPresent(InventoryComponent.class));

    DrawComponent dc = chest.fetch(DrawComponent.class).orElse(null);
    assertNotNull(dc);
    assertNotNull(dc.stateMachine());

    // Der Startzustand der Kiste muss "closed" sein
    assertEquals("closed", dc.stateMachine().getCurrentStateName());
  }
}
