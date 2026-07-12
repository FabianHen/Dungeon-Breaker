package contrib.components.healthcomponent;

import static org.junit.jupiter.api.Assertions.assertEquals;

import contrib.components.HealthComponent;
import org.junit.jupiter.api.Test;

/** Tests für das Setzen der maximalen Lebenspunkte in der HealthComponent. */
public class MaximalHealthTest {

  /** G1: Neuer Maximalwert größer als die aktuellen Lebenspunkte. */
  @Test
  public void testMaximalHealthpointsG1_NewMaxGreater_CurrentUnchanged() {
    // Arrange
    HealthComponent health = new HealthComponent(100);
    health.currentHealthpoints(50);

    // Act
    health.maximalHealthpoints(120);

    // Assert
    assertEquals(120, health.maximalHealthpoints(), "Maximalwert sollte auf 120 steigen.");
    assertEquals(50, health.currentHealthpoints(), "Aktuelle Lebenspunkte sollten bei 50 bleiben.");
  }

  /** G2: Neuer Maximalwert kleiner als die aktuellen Lebenspunkte. */
  @Test
  public void testMaximalHealthpointsG2_NewMaxSmaller_CurrentReduced() {
    // Arrange
    HealthComponent health = new HealthComponent(100);
    health.currentHealthpoints(80);

    // Act
    health.maximalHealthpoints(50);

    // Assert
    assertEquals(50, health.maximalHealthpoints(), "Maximalwert sollte auf 50 sinken.");
    assertEquals(
        50,
        health.currentHealthpoints(),
        "Aktuelle Lebenspunkte sollten auf den neuen Maximalwert (50) reduziert werden.");
  }

  /** G3: Neuer Maximalwert entspricht den aktuellen Lebenspunkten. */
  @Test
  public void testMaximalHealthpointsG3_NewMaxEqualsCurrent_CurrentUnchanged() {
    // Arrange
    HealthComponent health = new HealthComponent(100);
    health.currentHealthpoints(50);

    // Act
    health.maximalHealthpoints(50);

    // Assert
    assertEquals(50, health.maximalHealthpoints(), "Maximalwert sollte auf 50 gesetzt werden.");
    assertEquals(50, health.currentHealthpoints(), "Aktuelle Lebenspunkte sollten bei 50 bleiben.");
  }

  /** G4: Maximalwert wird auf 0 gesetzt. */
  @Test
  public void testMaximalHealthpointsG4_SetToZero() {
    // Arrange
    HealthComponent health = new HealthComponent(100);
    health.currentHealthpoints(50);

    // Act
    health.maximalHealthpoints(0);

    // Assert
    assertEquals(0, health.maximalHealthpoints(), "Maximalwert sollte 0 sein.");
    assertEquals(
        0, health.currentHealthpoints(), "Aktuelle Lebenspunkte sollten auf 0 reduziert werden.");
  }

  /** U1: Negativer Maximalwert (Fachlich ungültig, aber technisch verarbeitet). */
  @Test
  public void testMaximalHealthpointsU1_NegativeMax() {
    // Arrange
    HealthComponent health = new HealthComponent(100);
    health.currentHealthpoints(50);

    // Act
    health.maximalHealthpoints(-10);

    // Assert
    assertEquals(-10, health.maximalHealthpoints(), "Maximalwert sollte auf -10 gesetzt werden.");
    assertEquals(
        -10,
        health.currentHealthpoints(),
        "Aktuelle Lebenspunkte sollten ebenfalls auf den negativen Maximalwert sinken.");
  }
}
