package contrib.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import core.Entity;
import core.components.VelocityComponent;
import core.utils.Direction;
import core.utils.Vector2;

/**Test for {@link HeroController#moveHero(Entity, core.utils.Direction, core.utils.Vector2)} */
public class moveHeroTest {

    //G1. Hero besitzt kein InputComponent und ein VelocityComponent
    @Test
    void moveHero_DirectionUpSpeedOne_ForceAppliedToHeroEqualsDirection(){
        //arrange
        Direction direction = Direction.UP;
        Vector2 speed = Vector2.ONE;
        Vector2 mockForce = Vector2.ZERO;
        VelocityComponent mockVelocityComponent = new VelocityComponent();
        mockVelocityComponent.applyForce(HeroController.MOVEMENT_ID, mockForce);
        Entity hero = mock(Entity.class);//mock of the hero, but there are stil dependencies on VelocityComponent
        when(hero.fetch(VelocityComponent.class)).thenReturn(Optional.of(mockVelocityComponent));

        //act
        HeroController.moveHero(hero, direction, speed);

        //assert
        Map<String, Vector2> forces = mockVelocityComponent.appliedForces();
        Vector2 forceOfInterest = forces.get(HeroController.MOVEMENT_ID);
        assertEquals(forceOfInterest.x(), direction.x(), "dierection of the hero in x axis is not equals directiun up x axis");
        assertEquals(forceOfInterest.y(), direction.y(), "dierection of the hero in y axis is not equals directiun up y axis");
    }
}
