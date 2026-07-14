package contrib.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import core.Entity;
import core.components.InputComponent;
import core.components.VelocityComponent;
import core.utils.Direction;
import core.utils.Vector2;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/** Test for {@link HeroController#moveHero(Entity, core.utils.Direction, core.utils.Vector2)} */
public class moveHeroTest {

  @Test
  void moveHero_DirectionUpSpeedOne_ForceAppliedToHeroEqualsDirection() {
    // --- ARRANGE ---
    Direction direction = Direction.UP;
    Vector2 speed = Vector2.ONE;
    Vector2 mockForce = Vector2.ZERO;

    VelocityComponent mockVelocityComponent = mock(VelocityComponent.class);
    when(mockVelocityComponent.force(HeroController.MOVEMENT_ID))
        .thenReturn(Optional.of(mockForce));

    Entity hero = mock(Entity.class);
    when(hero.fetch(VelocityComponent.class)).thenReturn(Optional.of(mockVelocityComponent));

    when(hero.isPresent(InputComponent.class)).thenReturn(false);
    when(hero.fetch(InputComponent.class)).thenReturn(Optional.empty());

    // --- ACT ---
    HeroController.moveHero(hero, direction, speed);

    // --- ASSERT ---
    ArgumentCaptor<Vector2> forceCaptor = ArgumentCaptor.forClass(Vector2.class);
    verify(mockVelocityComponent).applyForce(eq(HeroController.MOVEMENT_ID), forceCaptor.capture());

    Vector2 forceOfInterest = forceCaptor.getValue();

    assertEquals(
        direction.x() * speed.x(),
        forceOfInterest.x(),
        "Direction of the hero in x axis is not equal to expected x axis");
    assertEquals(
        direction.y() * speed.y(),
        forceOfInterest.y(),
        "Direction of the hero in y axis is not equal to expected y axis");
  }
}
