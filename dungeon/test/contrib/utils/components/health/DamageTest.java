package contrib.utils.components.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import contrib.components.HealthComponent;
import core.Entity;
import org.junit.jupiter.api.Test;

/** Tests for {@link Damage}. */
public class DamageTest {

  @Test
  void recordMethodsReturnConstructorValues() {
    Entity cause = new Entity();
    Damage damage = new Damage(7, DamageType.FIRE, cause);

    assertEquals(7, damage.damageAmount());
    assertEquals(DamageType.FIRE, damage.damageType());
    assertSame(cause, damage.cause());
  }

  @Test
  void nullCauseIsAccepted() {
    Damage damage = new Damage(3, DamageType.PHYSICAL, null);

    assertNull(damage.cause());
  }

  @Test
  void allDamageTypesAreStoredCorrectly() {
    for (DamageType damageType : DamageType.values()) {
      Damage damage = new Damage(1, damageType, null);

      assertEquals(damageType, damage.damageType());
    }
  }

  @Test
  void damageCanBePassedToHealthComponent() {
    HealthComponent healthComponent = new HealthComponent();
    Entity cause = new Entity();
    Damage damage = new Damage(4, DamageType.MAGIC, cause);

    healthComponent.receiveHit(damage);

    assertEquals(4, healthComponent.calculateDamageOf(DamageType.MAGIC));
    assertSame(cause, healthComponent.lastDamageCause().orElseThrow());
  }
}