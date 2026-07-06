package contrib.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import core.configuration.KeyboardConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import core.Entity;
import core.components.InputComponent;
import core.components.VelocityComponent;
import core.components.InputComponent.InputData;
import core.utils.Direction;
import core.utils.Vector2;

/**Test for {@link HeroController#moveHero(Entity, core.utils.Direction, core.utils.Vector2)} */
public class moveHeroTest {

    private Entity mockHero;
    private VelocityComponent mockVelocityComponent;
    private Map<Integer, InputData> mockKeyBindings;
    private InputComponent mockInputComponent;
    
    @BeforeEach
    void setUp() {
        this.mockHero = mock(Entity.class);
        
        Vector2 mockForce = Vector2.ZERO;
        this.mockVelocityComponent = new VelocityComponent();
        this.mockVelocityComponent.applyForce(HeroController.MOVEMENT_ID, mockForce);

        this.mockKeyBindings = new HashMap<>();
        this.mockKeyBindings.put(KeyboardConfig.MOVEMENT_UP.value(), null);

        this.mockInputComponent = mock(InputComponent.class);
    }
    
    
    //G1. Hero besitzt kein InputComponent und ein VelocityComponent
    @Test
    void moveHero_DirectionUpSpeedOne_ForceAppliedToHeroEqualsDirection(){
        //arrange
        Direction direction = Direction.UP;
        Vector2 speed = Vector2.ONE;
        when(this.mockHero.fetch(VelocityComponent.class)).thenReturn(Optional.of(this.mockVelocityComponent));

        //act
        HeroController.moveHero(this.mockHero, direction, speed);

        //assert
        Map<String, Vector2> forces = this.mockVelocityComponent.appliedForces();
        Vector2 forceOfInterest = forces.get(HeroController.MOVEMENT_ID);
        assertEquals(forceOfInterest.x(), direction.x(), "dierection of the hero in x axis is not equals directiun up x axis");
        assertEquals(forceOfInterest.y(), direction.y(), "dierection of the hero in y axis is not equals directiun up y axis");
    }

    //G2. InputComponent vorhanden, Steuerung aktiviert, Bewegungsrichtung erlaubt
    @Test
    void moveHero_InputComponentWithUPKeyBinding_DirectionUpSpeedOne_ForceAppliedToHeroEqualsDirection(){
        //arrange
        Direction direction = Direction.UP;
        Vector2 speed = Vector2.ONE;
        when(this.mockHero.fetch(InputComponent.class)).thenReturn(Optional.of(this.mockInputComponent));
        when(this.mockInputComponent.deactivateControls()).thenReturn(false);
        when(this.mockInputComponent.callbacks()).thenReturn(this.mockKeyBindings);
        when(this.mockHero.fetch(VelocityComponent.class)).thenReturn(Optional.of(this.mockVelocityComponent));

        //act
        HeroController.moveHero(this.mockHero, direction, speed);

        //assert
        Map<String, Vector2> forces = this.mockVelocityComponent.appliedForces();
        Vector2 forceOfInterest = forces.get(HeroController.MOVEMENT_ID);
        assertEquals(forceOfInterest.x(), direction.x(), "dierection of the hero in x axis is not equals directiun up x axis");
        assertEquals(forceOfInterest.y(), direction.y(), "dierection of the hero in y axis is not equals directiun up y axis");    
    }
}
