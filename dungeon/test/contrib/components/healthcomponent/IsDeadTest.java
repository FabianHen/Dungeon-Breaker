package contrib.components.healthcomponent;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import contrib.components.HealthComponent;
import org.junit.jupiter.api.Test;

/** Tests für die isDead-Prüfung in der HealthComponent. */
public class IsDeadTest {

  /** G1: Positive Lebenspunkte. */
  @Test
  public void testIsDeadG1_PositiveHealth_ReturnsFalse() {
    // Arrange
    HealthComponent health = new HealthComponent(100);
    health.currentHealthpoints(50);

    // Act
    boolean dead = health.isDead();

    // Assert
    assertFalse(dead, "Bei positiven Lebenspunkten (50) sollte die Entity nicht tot sein.");
  }

  /** G2: Lebenspunkte gleich 0. */
  @Test
  public void testIsDeadG2_ZeroHealth_ReturnsTrue() {
    // Arrange
    HealthComponent health = new HealthComponent(100);
    health.currentHealthpoints(0);

    // Act
    boolean dead = health.isDead();

    // Assert
    assertTrue(dead, "Bei exakt 0 Lebenspunkten sollte die Entity als tot gelten.");
  }

  /** G3: Negative Lebenspunkte. */
  @Test
  public void testIsDeadG3_NegativeHealth_ReturnsTrue() {
    // Arrange
    HealthComponent health = new HealthComponent(100);
    health.currentHealthpoints(-15);

    // Act
    boolean dead = health.isDead();

    // Assert
    assertTrue(dead, "Bei negativen Lebenspunkten (-15) sollte die Entity als tot gelten.");
  }
}
