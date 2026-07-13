package contrib.components.healthcomponent;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import contrib.components.HealthComponent;
import org.junit.jupiter.api.Test;

/** Tests für das Aktivieren und Deaktivieren des GodModes in der HealthComponent. */
public class GodModeTest {

  /** G1: GodMode aktivieren. */
  @Test
  public void testGodModeG1_Activate_SetsToTrue() {
    // Arrange
    HealthComponent health = new HealthComponent(100);
    // Default ist false, wir müssen nichts weiter vorbereiten

    // Act
    health.godMode(true);

    // Assert
    assertTrue(health.godMode(), "Der GodMode sollte nach der Aktivierung true sein.");
  }

  /** G2: GodMode deaktivieren. */
  @Test
  public void testGodModeG2_Deactivate_SetsToFalse() {
    // Arrange
    HealthComponent health = new HealthComponent(100);
    health.godMode(true); // Erst einschalten, um den Wechsel zu garantieren

    // Act
    health.godMode(false);

    // Assert
    assertFalse(health.godMode(), "Der GodMode sollte nach der Deaktivierung wieder false sein.");
  }
}
