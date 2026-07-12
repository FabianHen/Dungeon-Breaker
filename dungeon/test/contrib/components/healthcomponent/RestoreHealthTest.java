package contrib.components.healthcomponent;

import static org.junit.jupiter.api.Assertions.assertEquals;

import contrib.components.HealthComponent;
import org.junit.jupiter.api.Test;

/** Tests für das Wiederherstellen von Lebenspunkten in der HealthComponent. */
public class RestoreHealthTest {

  /** G1: Positive Heilung innerhalb der maximalen Lebenspunkte. */
  @Test
  public void testRestoreHealthpointsG1_PositiveHeal_WithinMax() {
    // Arrange
    HealthComponent health = new HealthComponent(100);
    health.currentHealthpoints(50);

    // Act
    health.restoreHealthpoints(30);

    // Assert
    assertEquals(
        80, health.currentHealthpoints(), "Lebenspunkte sollten um exakt 30 auf 80 steigen.");
  }

  /** G2: Positive Heilung überschreitet die maximalen Lebenspunkte. */
  @Test
  public void testRestoreHealthpointsG2_PositiveHeal_ExceedsMax() {
    // Arrange
    HealthComponent health = new HealthComponent(100);
    health.currentHealthpoints(80);

    // Act
    health.restoreHealthpoints(50);

    // Assert
    assertEquals(
        100,
        health.currentHealthpoints(),
        "Lebenspunkte sollten das Maximum von 100 nicht überschreiten (Clamping).");
  }

  /** G3: Heilung um 0 Lebenspunkte. */
  @Test
  public void testRestoreHealthpointsG3_ZeroHeal_NoChange() {
    // Arrange
    HealthComponent health = new HealthComponent(100);
    health.currentHealthpoints(50);

    // Act
    health.restoreHealthpoints(0);

    // Assert
    assertEquals(
        50,
        health.currentHealthpoints(),
        "Bei einer Heilung um 0 sollten die Lebenspunkte unverändert bleiben.");
  }

  /** G4: Negativer Heilungswert. */
  @Test
  public void testRestoreHealthpointsG4_NegativeHeal_NoChange() {
    // Arrange
    HealthComponent health = new HealthComponent(100);
    health.currentHealthpoints(50);

    // Act
    health.restoreHealthpoints(-20);

    // Assert
    assertEquals(
        50, health.currentHealthpoints(), "Negative Heilungswerte sollten ignoriert werden.");
  }
}
