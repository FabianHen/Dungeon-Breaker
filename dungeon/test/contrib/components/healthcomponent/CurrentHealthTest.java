package contrib.components.healthcomponent;

import static org.junit.jupiter.api.Assertions.assertEquals;

import contrib.components.HealthComponent;
import org.junit.jupiter.api.Test;

/** Tests für das Setzen der aktuellen Lebenspunkte in der HealthComponent. */
public class CurrentHealthTest {

  // =========================================================================
  // Block 1: Tests für currentHealthpoints(int amount)
  // =========================================================================

  /**
   * G1: Lebenspunkte innerhalb des gültigen Bereichs (GodMode aus).
   */
  @Test
  public void testCurrentHealthpointsG1_ValidRange_NoGodMode() {
    // Arrange
    int maxHp = 100;
    HealthComponent health = new HealthComponent(maxHp);
    health.godMode(false);

    // Act
    health.currentHealthpoints(50);

    // Assert
    assertEquals(50, health.currentHealthpoints(),
      "Die Lebenspunkte sollten exakt dem übergebenen Wert entsprechen.");
  }

  /**
   * G2: Lebenspunkte größer als die maximalen Lebenspunkte (GodMode aus).
   */
  @Test
  public void testCurrentHealthpointsG2_ExceedsMax_NoGodMode() {
    // Arrange
    int maxHp = 100;
    HealthComponent health = new HealthComponent(maxHp);
    health.godMode(false);

    // Act
    health.currentHealthpoints(150);

    // Assert
    assertEquals(maxHp, health.currentHealthpoints(),
      "Die Lebenspunkte dürfen das Maximum nicht überschreiten.");
  }

  /**
   * G3: Lebenspunkte kleiner oder gleich 0 bei aktiviertem GodMode.
   */
  @Test
  public void testCurrentHealthpointsG3_BelowZero_WithGodMode() {
    // Arrange
    int maxHp = 100;
    HealthComponent health = new HealthComponent(maxHp);
    health.godMode(true);

    // Act
    health.currentHealthpoints(-10);

    // Assert
    assertEquals(1, health.currentHealthpoints(),
      "Im GodMode dürfen die Lebenspunkte nicht unter 1 fallen.");
  }

  /**
   * G4: Lebenspunkte innerhalb des gültigen Bereichs bei aktiviertem GodMode.
   */
  @Test
  public void testCurrentHealthpointsG4_ValidRange_WithGodMode() {
    // Arrange
    int maxHp = 100;
    HealthComponent health = new HealthComponent(maxHp);
    health.godMode(true);

    // Act
    health.currentHealthpoints(75);

    // Assert
    assertEquals(75, health.currentHealthpoints(),
      "Auch im GodMode sollten gültige Zuweisungen normal übernommen werden.");
  }

  /**
   * Bonus-Test (Ergänzung zu G1):
   * Prüft, dass negative Werte ohne GodMode tatsächlich zugelassen werden.
   */
  @Test
  public void testCurrentHealthpoints_NegativeValue_NoGodMode() {
    // Arrange
    int maxHp = 100;
    HealthComponent health = new HealthComponent(maxHp);
    health.godMode(false);

    // Act
    health.currentHealthpoints(-50);

    // Assert
    assertEquals(-50, health.currentHealthpoints(),
      "Ohne GodMode können Lebenspunkte ins Negative fallen (Overkill).");
  }
}
