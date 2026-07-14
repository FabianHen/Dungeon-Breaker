package contrib.components.healthcomponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import contrib.components.HealthComponent;
import contrib.utils.components.health.Damage;
import contrib.utils.components.health.DamageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for {@link HealthComponent#clearDamage()}. */
public class ClearDamageTest {

  private HealthComponent healthComponent;

  @BeforeEach
  void setup() {
    healthComponent = new HealthComponent();
  }

  @Test
  void clearDamage_damageToGetHasOneEntry_noDamageToGet() {
    DamageType damageType = mock(DamageType.class);
    Damage damage = mock(Damage.class);
    when(damage.damageAmount()).thenReturn(10);
    when(damage.damageType()).thenReturn(damageType);
    healthComponent.receiveHit(damage);

    healthComponent.clearDamage();

    assertEquals(0, healthComponent.calculateDamageOf(damageType));
  }

  @Test
  void clearDamage_damageToGetEmpty_staysEmpty() {
    DamageType damageType = mock(DamageType.class);

    healthComponent.clearDamage();

    assertEquals(0, healthComponent.calculateDamageOf(damageType));
  }

  @Test
  void clearDamage_damageToGetHasMultipleEntries_noDamageToGet() {
    DamageType damageType1 = mock(DamageType.class);
    DamageType damageType2 = mock(DamageType.class);
    Damage damage = mock(Damage.class);
    when(damage.damageAmount()).thenReturn(10);
    when(damage.damageType()).thenReturn(damageType1);
    healthComponent.receiveHit(damage);
    when(damage.damageType()).thenReturn(damageType2);
    healthComponent.receiveHit(damage);

    healthComponent.clearDamage();

    assertEquals(0, healthComponent.calculateDamageOf(damageType1));
    assertEquals(0, healthComponent.calculateDamageOf(damageType2));
  }
}
