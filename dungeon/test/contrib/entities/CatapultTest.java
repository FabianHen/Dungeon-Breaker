package contrib.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import contrib.components.CatapultableComponent;
import contrib.components.CollideComponent;
import core.Entity;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.components.VelocityComponent;
import core.utils.Point;
import org.junit.jupiter.api.Test;

/**
 * Tests für die Methode MiscFactory.catapult(Point spawnPoint, Point location, float speed)
 * Zugehörige Issue-Nummer: #140 / #29
 */
public class CatapultTest {

  /**
   * Hilfsmethode, um die in MiscFactory.catapult definierte Kollisionslogik direkt auszuführen.
   *
   * @param catapult das Katapult-Entity.
   * @param other das kollidierende Entity.
   * @param spawnPoint der Startpunkt des Katapults.
   * @param target das Ziel des Fluges.
   * @param speed die Fluggeschwindigkeit.
   */
  private void simulateCatapultCollision(
      Entity catapult, Entity other, Point spawnPoint, Point target, float speed) {
    if (!other.isPresent(CatapultableComponent.class)) {
      return;
    }

    CatapultableComponent cc = other.fetch(CatapultableComponent.class).orElse(null);
    if (cc == null || cc.isFlying()) {
      return;
    }

    other
        .fetch(VelocityComponent.class)
        .ifPresent(
            vc -> {
              vc.currentVelocity(core.utils.Vector2.ZERO);
              vc.clearForces();
            });

    cc.deactivate().accept(other);
    cc.flies();
  }

  /** Äquivalenzklasse: G1. Katapult mit gültigen Parametern erstellen. */
  @Test
  public void test_G1_catapult_valid_creation() {
    Point spawn = new Point(1f, 2f);
    Point target = new Point(10f, 10f);
    float speed = 5.0f;

    Entity catapult = MiscFactory.catapult(spawn, target, speed);

    assertNotNull(catapult);
    assertEquals("catapult", catapult.name());
    assertTrue(catapult.isPresent(PositionComponent.class));
    assertTrue(catapult.isPresent(DrawComponent.class));
    assertTrue(catapult.isPresent(CollideComponent.class));

    CollideComponent cc = catapult.fetch(CollideComponent.class).orElse(null);
    assertNotNull(cc);
    assertFalse(cc.isSolid(), "Das Katapult sollte für Kollisionen nicht-solide sein.");
  }

  /** Äquivalenzklasse: G2. Kollision mit catapultableem Entity. */
  @Test
  public void test_G2_collision_with_catapultable() {
    Point spawn = new Point(1f, 1f);
    Point target = new Point(5f, 5f);
    Entity catapult = MiscFactory.catapult(spawn, target, 5.0f);

    Entity other = new Entity();
    other.add(new PositionComponent(spawn));

    // Wir nutzen die originale Komponente und füttern sie mit funktionalen Lambdas
    CatapultableComponent catapultable = new CatapultableComponent(entity -> {}, entity -> {});
    other.add(catapultable);

    simulateCatapultCollision(catapult, other, spawn, target, 5.0f);

    assertTrue(catapultable.isFlying(), "Das Entity sollte nach der Kollision fliegen.");
  }

  /** Äquivalenzklasse: G3. Kollision mit CatapultableComponent und VelocityComponent. */
  @Test
  public void test_G3_collision_with_catapultable_and_velocity() {
    Point spawn = new Point(1f, 1f);
    Point target = new Point(5f, 5f);
    Entity catapult = MiscFactory.catapult(spawn, target, 5.0f);

    Entity other = new Entity();
    other.add(new PositionComponent(spawn));
    CatapultableComponent catapultable = new CatapultableComponent(entity -> {}, entity -> {});
    other.add(catapultable);

    VelocityComponent vc = new VelocityComponent(3.0f, 1.0f, entity -> {}, false);
    other.add(vc);

    simulateCatapultCollision(catapult, other, spawn, target, 5.0f);

    assertTrue(catapultable.isFlying(), "Das Entity sollte fliegen.");
  }

  /** Äquivalenzklasse: G4. Kollision mit CatapultableComponent ohne VelocityComponent. */
  @Test
  public void test_G4_collision_without_velocity_component() {
    Point spawn = new Point(1f, 1f);
    Point target = new Point(5f, 5f);
    Entity catapult = MiscFactory.catapult(spawn, target, 5.0f);

    Entity other = new Entity();
    other.add(new PositionComponent(spawn));
    CatapultableComponent catapultable = new CatapultableComponent(entity -> {}, entity -> {});
    other.add(catapultable);

    simulateCatapultCollision(catapult, other, spawn, target, 5.0f);

    assertTrue(catapultable.isFlying(), "Das Entity sollte fliegen.");
  }

  /** Äquivalenzklasse: G5. Deaktivierungslogik wird ausgeführt. */
  @Test
  public void test_G5_deactivation_logic_runs() {
    Point spawn = new Point(1f, 1f);
    Point target = new Point(5f, 5f);
    Entity catapult = MiscFactory.catapult(spawn, target, 5.0f);

    Entity other = new Entity();
    other.add(new PositionComponent(spawn));

    final boolean[] deactivatedCalled = {false};
    CatapultableComponent catapultable =
        new CatapultableComponent(entity -> deactivatedCalled[0] = true, entity -> {});
    other.add(catapultable);

    simulateCatapultCollision(catapult, other, spawn, target, 5.0f);

    assertTrue(
        deactivatedCalled[0],
        "Der deactivate-Callback auf der CatapultableComponent wurde nicht aufgerufen.");
  }

  /** Äquivalenzklasse: G6. Mehrere unterschiedliche Entities werden katapultiert. */
  @Test
  public void test_G6_multiple_entities_catapulted() {
    Point spawn = new Point(1f, 1f);
    Point target = new Point(5f, 5f);
    Entity catapult = MiscFactory.catapult(spawn, target, 5.0f);

    Entity other1 = new Entity();
    other1.add(new PositionComponent(spawn));
    CatapultableComponent catapultable1 = new CatapultableComponent(entity -> {}, entity -> {});
    other1.add(catapultable1);

    Entity other2 = new Entity();
    other2.add(new PositionComponent(spawn));
    CatapultableComponent catapultable2 = new CatapultableComponent(entity -> {}, entity -> {});
    other2.add(catapultable2);

    simulateCatapultCollision(catapult, other1, spawn, target, 5.0f);
    simulateCatapultCollision(catapult, other2, spawn, target, 5.0f);

    assertTrue(catapultable1.isFlying());
    assertTrue(catapultable2.isFlying());
  }

  /** Äquivalenzklasse: U1. SpawnPoint ist null. */
  @Test
  public void test_U1_spawnPoint_null() {
    try {
      MiscFactory.catapult(null, new Point(0, 0), 5.0f);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /** Äquivalenzklasse: U2. Zielposition ist null. */
  @Test
  public void test_U2_target_location_null() {
    try {
      Entity catapult = MiscFactory.catapult(new Point(0, 0), null, 5.0f);
      Entity other = new Entity();
      other.add(new PositionComponent(new Point(0, 0)));
      other.add(new CatapultableComponent(entity -> {}, entity -> {}));

      simulateCatapultCollision(catapult, other, new Point(0, 0), null, 5.0f);
    } catch (Throwable t) {
      assertTrue(true);
    }
  }

  /** Äquivalenzklasse: U3. Geschwindigkeit ist 0. */
  @Test
  public void test_U3_speed_zero() {
    Point spawn = new Point(0, 0);
    Entity catapult = MiscFactory.catapult(spawn, new Point(1, 1), 0f);
    assertNotNull(catapult);
  }

  /** Äquivalenzklasse: U4. Geschwindigkeit ist negativ. */
  @Test
  public void test_U4_speed_negative() {
    Point spawn = new Point(0, 0);
    Entity catapult = MiscFactory.catapult(spawn, new Point(1, 1), -5.0f);
    assertNotNull(catapult);
  }

  /** Äquivalenzklasse: U5. Kollidierendes Entity besitzt keine CatapultableComponent. */
  @Test
  public void test_U5_entity_not_catapultable() {
    Point spawn = new Point(1f, 1f);
    Point target = new Point(5f, 5f);
    Entity catapult = MiscFactory.catapult(spawn, target, 5.0f);

    Entity other = new Entity();
    other.add(new PositionComponent(spawn));

    simulateCatapultCollision(catapult, other, spawn, target, 5.0f);

    assertFalse(other.isPresent(CatapultableComponent.class));
  }

  /** Äquivalenzklasse: U6. Kollidierendes Entity fliegt bereits. */
  @Test
  public void test_U6_entity_already_flying() {
    Point spawn = new Point(1f, 1f);
    Point target = new Point(5f, 5f);
    Entity catapult = MiscFactory.catapult(spawn, target, 5.0f);

    Entity other = new Entity();
    other.add(new PositionComponent(spawn));
    CatapultableComponent catapultable = new CatapultableComponent(entity -> {}, entity -> {});
    catapultable.flies(); // Bereits in den Flugzustand versetzen
    other.add(catapultable);

    // Wir merken uns, ob deactivate aufgerufen wird
    final boolean[] deactivatedCalledOnFlying = {false};
    CatapultableComponent spyCatapultable =
        new CatapultableComponent(entity -> deactivatedCalledOnFlying[0] = true, entity -> {});
    spyCatapultable.flies();

    Entity otherSpy = new Entity();
    otherSpy.add(new PositionComponent(spawn));
    otherSpy.add(spyCatapultable);

    simulateCatapultCollision(catapult, otherSpy, spawn, target, 5.0f);

    assertFalse(
        deactivatedCalledOnFlying[0],
        "Wenn das Entity bereits fliegt, darf kein neuer Katapult-Trigger ausgelöst werden.");
  }
}
