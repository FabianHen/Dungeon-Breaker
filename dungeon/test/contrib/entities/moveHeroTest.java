package contrib.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        Entity hero = HeroBuilder.builder().build();//TODO
        Direction direction = Direction.UP;
        Vector2 speed = Vector2.ONE;

        //act
        HeroController.moveHero(hero, direction, speed);

        //assert
        Optional<VelocityComponent> ovc = hero.fetch(VelocityComponent.class);
        assertTrue(ovc.isPresent(), "hero has no vilocity component");
        VelocityComponent vc = ovc.get();
        Map<String, Vector2> forces = vc.appliedForces();
        Vector2 forceOfInterest = forces.get(HeroController.MOVEMENT_ID);
        assertEquals(forceOfInterest.x(), direction.x(), "dierection of the hero in x axis is not equals directiun up x axis");
        assertEquals(forceOfInterest.y(), direction.y(), "dierection of the hero in y axis is not equals directiun up y axis");

    }
}
