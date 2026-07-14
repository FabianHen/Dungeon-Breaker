package contrib.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import contrib.item.concreteItem.ItemBigKey;
import contrib.item.concreteItem.ItemKey;
import contrib.modules.interaction.ISimpleIInteractable;
import contrib.modules.interaction.Interaction;
import contrib.modules.interaction.InteractionComponent;
import core.Entity;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.level.elements.tile.DoorTile;
import core.utils.Point;
import org.junit.jupiter.api.Test;

/**
 * Tests für die Methode MiscFactory.createDoorBlocker(DoorTile door, Class<? extends Item>
 * requiredKeyType) Zugehörige Issue-Nummer: #29
 */
public class DoorBlockerTest {

  /**
   * Ein einfacher Stub für DoorTile, falls die Klasse im Testkontext nicht voll instanziiert werden
   * kann.
   */
  private static class DoorTileStub extends DoorTile {
    private boolean isOpen = false;
    private final Point position;

    public DoorTileStub(Point position) {
      super(null, null, null);
      this.position = position;
    }

    @Override
    public Point position() {
      return this.position;
    }

    @Override
    public void open() {
      this.isOpen = true;
    }

    @Override
    public void close() {
      this.isOpen = false;
    }

    public boolean isOpen() {
      return this.isOpen;
    }
  }

  /** Äquivalenzklasse: G1. DoorBlocker mit ItemKey erstellen. */
  @Test
  public void test_G1_createDoorBlocker_with_ItemKey() {
    Point doorPos = new Point(2f, 3f);
    DoorTileStub door = new DoorTileStub(doorPos);

    Entity blocker = MiscFactory.createDoorBlocker(door, ItemKey.class);

    assertNotNull(blocker);
    assertEquals("doorBlocker", blocker.name());
    assertTrue(blocker.isPresent(PositionComponent.class));
    assertTrue(blocker.isPresent(InteractionComponent.class));
    assertTrue(blocker.isPresent(DrawComponent.class));

    // Die Tür muss sofort geschlossen werden
    assertFalse(door.isOpen(), "Die Tür sollte sofort geschlossen sein.");

    // Die Y-Position des Blockers muss leicht versetzt sein (y - 0.4f)
    PositionComponent pc = blocker.fetch(PositionComponent.class).orElse(null);
    assertNotNull(pc);
    assertEquals(doorPos.x(), pc.position().x());
    assertEquals(doorPos.y() - 0.4f, pc.position().y(), 0.01f);
  }

  /** Äquivalenzklasse: G2. DoorBlocker mit ItemBigKey erstellen. */
  @Test
  public void test_G2_createDoorBlocker_with_ItemBigKey() {
    Point doorPos = new Point(5f, 5f);
    DoorTileStub door = new DoorTileStub(doorPos);

    Entity blocker = MiscFactory.createDoorBlocker(door, ItemBigKey.class);

    assertNotNull(blocker);
    assertFalse(door.isOpen());
  }

  /** Äquivalenzklasse: G3. Interaktion prüfen (Radius und Struktur der Interaction). */
  @Test
  public void test_G3_G4_interaction_structure() {
    Point doorPos = new Point(0, 0);
    DoorTileStub door = new DoorTileStub(doorPos);

    Entity blocker = MiscFactory.createDoorBlocker(door, ItemKey.class);
    InteractionComponent ic = blocker.fetch(InteractionComponent.class).orElse(null);
    assertNotNull(ic);

    assertInstanceOf(ISimpleIInteractable.class, ic.interactions());
    ISimpleIInteractable simpleInteractable = (ISimpleIInteractable) ic.interactions();

    Interaction interaction = simpleInteractable.interact();
    assertNotNull(interaction);
    assertEquals(
        2f, interaction.range(), "Der Interaktionsradius muss für den Blocker exakt 2f betragen.");
  }

  /** Äquivalenzklasse: U2. Ungültiger Key-Typ wirft IllegalArgumentException. */
  @Test
  public void test_U2_invalid_key_type_throws_exception() {
    DoorTileStub door = new DoorTileStub(new Point(0, 0));

    assertThrows(
        IllegalArgumentException.class,
        () -> MiscFactory.createDoorBlocker(door, contrib.item.Item.class));
  }

  /** Äquivalenzklasse: U3. door ist null. */
  @Test
  public void test_U3_door_null_throws_exception() {
    assertThrows(
        NullPointerException.class, () -> MiscFactory.createDoorBlocker(null, ItemKey.class));
  }
}
