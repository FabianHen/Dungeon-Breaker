package contrib.components.healthcomponent;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import contrib.components.HealthComponent;
import org.junit.jupiter.api.Test;

/** Tests für das Setzen und Abfragen des alreadyDead-Status in der HealthComponent. */
public class AlreadyDeadTest {

  /** G1: Status auf „bereits tot“ setzen. */
  @Test
  public void testAlreadyDeadG1_SetToTrue_ReturnsTrue() {
    // Arrange
    HealthComponent health = new HealthComponent(100);

    // Act
    health.alreadyDead(true);

    // Assert
    assertTrue(
        health.alreadyDead(), "Der Status sollte nach dem Setzen auf true auch true zurückgeben.");
  }

  /** G2: Status auf „nicht bereits tot“ setzen. */
  @Test
  public void testAlreadyDeadG2_SetToFalse_ReturnsFalse() {
    // Arrange
    HealthComponent health = new HealthComponent(100);
    health.alreadyDead(true); // Vorher auf true setzen, um den Wechsel zu garantieren

    // Act
    health.alreadyDead(false);

    // Assert
    assertFalse(
        health.alreadyDead(),
        "Der Status sollte nach dem Setzen auf false auch false zurückgeben.");
  }
}
