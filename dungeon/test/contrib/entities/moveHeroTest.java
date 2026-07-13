package contrib.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import core.configuration.KeyboardConfig;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
import core.utils.components.MissingComponentException;

/**Test for {@link HeroController#moveHero(Entity, core.utils.Direction, core.utils.Vector2)} */
public class moveHeroTest {

    private Entity mockHero;
    private VelocityComponent mockVelocityComponent;
    private Map<Integer, InputData> mockKeyBindings;
    private InputComponent mockInputComponent;

    //helper function to compare two vectors of Vector2 class. there is no equals method in Vector2 class
    private boolean compareVector2(Vector2 a, Vector2 b){
        return (a != null && b != null && (a.x() == b.x()) && (a.y() == b.y()));
    }

    //helper function to set applied force of hero. there is no setPosition method to set parameter for movement
    private void setAppliedForceOfVelocityComponentOfHeroBeforeMovement(Vector2 forceToApply){
        this.mockVelocityComponent.applyForce(HeroController.MOVEMENT_ID, forceToApply);
    }

    //helper function to get applied force of hero. there is no getPosition method to get information from movement
    private Vector2 getAppliedForceFromVelocityComponentOfHeroAfterMovement(){
        Map<String, Vector2> forces = this.mockVelocityComponent.appliedForces();
        Vector2 forceOfInterest = forces.get(HeroController.MOVEMENT_ID);
        return forceOfInterest;
    }
    
    @BeforeEach
    void setUp() {
        this.mockHero = mock(Entity.class);
        
        this.mockVelocityComponent = new VelocityComponent();

        this.mockInputComponent = mock(InputComponent.class);
    }
    
    //G1. Hero besitzt kein InputComponent und ein VelocityComponent
    @Test
    void moveHero_DirectionUpSpeedOne_ForceAppliedToHeroEqualsDirection(){
        //arrange
        Direction direction = Direction.UP;
        Vector2 speed = Vector2.ONE;
        Vector2 forceOfNoMovement = Vector2.ZERO;
        this.setAppliedForceOfVelocityComponentOfHeroBeforeMovement(forceOfNoMovement);
        when(this.mockHero.isPresent(InputComponent.class)).thenReturn(false);
        when(this.mockHero.fetch(VelocityComponent.class)).thenReturn(Optional.of(this.mockVelocityComponent));

        //act
        HeroController.moveHero(this.mockHero, direction, speed);

        //assert
        Vector2 appliedForceAfterMovement = this.getAppliedForceFromVelocityComponentOfHeroAfterMovement();
        assertTrue(this.compareVector2(appliedForceAfterMovement, direction), "force applied to hero is not direction up");
    }

    //G2. InputComponent vorhanden, Steuerung aktiviert, Bewegungsrichtung erlaubt
    @Test
    void moveHero_InputComponentAllowedUpMovement_DirectionUpSpeedOne_ForceAppliedToHeroEqualsDirection(){
        //arrange
        Direction direction = Direction.UP;
        Vector2 speed = Vector2.ONE;
        Vector2 forceOfNoMovement = Vector2.ZERO;
        this.mockKeyBindings = new HashMap<>();
        this.mockKeyBindings.put(KeyboardConfig.MOVEMENT_UP.value(), null);
        this.setAppliedForceOfVelocityComponentOfHeroBeforeMovement(forceOfNoMovement);
        when(this.mockHero.fetch(InputComponent.class)).thenReturn(Optional.of(this.mockInputComponent));
        when(this.mockInputComponent.deactivateControls()).thenReturn(false);
        when(this.mockHero.isPresent(InputComponent.class)).thenReturn(true);
        when(this.mockInputComponent.callbacks()).thenReturn(this.mockKeyBindings);
        when(this.mockHero.fetch(VelocityComponent.class)).thenReturn(Optional.of(this.mockVelocityComponent));

        //act
        HeroController.moveHero(this.mockHero, direction, speed);

        //assert
        Vector2 appliedForceAfterMovement = this.getAppliedForceFromVelocityComponentOfHeroAfterMovement();
        assertTrue(this.compareVector2(appliedForceAfterMovement, direction), "force applied to hero is not direction up");  
    }

    //G3. Keine bestehende Bewegungs-Kraft vorhanden
    @Test
    void moveHero_DirectionUpSpeedOneVelocityComponentEmpty_ForceAppliedToHeroEqualsDirection(){
        //arrange
        Direction direction = Direction.UP;
        Vector2 speed = Vector2.ONE;
        when(this.mockHero.fetch(VelocityComponent.class)).thenReturn(Optional.of(this.mockVelocityComponent));

        //act
        HeroController.moveHero(this.mockHero, direction, speed);

        //assert
        Vector2 appliedForceAfterMovement = this.getAppliedForceFromVelocityComponentOfHeroAfterMovement();
        assertTrue(this.compareVector2(appliedForceAfterMovement, direction), "force applied to hero is not direction up");  
    }

    //G4. Bereits vorhandene Bewegungs-Kraft vorhanden
    @Test
    void moveHero_DirectionUpSpeedOneVelocityComponentContainsDiagonalForce_ForceAppliedToHeroEqualSumOfInput(){
        //arrange
        Direction direction = Direction.UP;
        Vector2 speed = Vector2.ONE;
        Vector2 forceInX1Y1Diagonal = Vector2.of(1.0f, 1.0f);
        this.setAppliedForceOfVelocityComponentOfHeroBeforeMovement(forceInX1Y1Diagonal);
        when(this.mockHero.fetch(VelocityComponent.class)).thenReturn(Optional.of(this.mockVelocityComponent));

        //act
        HeroController.moveHero(this.mockHero, direction, speed);
        
        //assert
        Vector2 normalizedForceInX1Y2Direction = Vector2.of(1.0f/Math.sqrt(5.0f), 2.0f/Math.sqrt(5.0f));
        Vector2 appliedForceAfterMovement = this.getAppliedForceFromVelocityComponentOfHeroAfterMovement();
        assertTrue(this.compareVector2(appliedForceAfterMovement, normalizedForceInX1Y2Direction), "force applied to hero is not the sum of directions");
    }

    //G5. Bewegung entlang einer einzelnen Achse
    @Test
    void moveHero_DirectionUpSpeedOneVelocityComponentContainsUpForce_ForceAppliedToHeroEqualsUpForce(){
        //arrange
        Direction direction = Direction.UP;
        Vector2 speed = Vector2.ONE;
        Vector2 forceInX0Y1Direction = Vector2.of(0.0f, 1.0f);
        this.setAppliedForceOfVelocityComponentOfHeroBeforeMovement(forceInX0Y1Direction);
        when(this.mockHero.fetch(VelocityComponent.class)).thenReturn(Optional.of(this.mockVelocityComponent));

        //act
        HeroController.moveHero(this.mockHero, direction, speed);

        //assert
        Vector2 appliedForceAfterMovement = this.getAppliedForceFromVelocityComponentOfHeroAfterMovement();
        assertTrue(this.compareVector2(appliedForceAfterMovement, forceInX0Y1Direction), "force applied to hero is not direction up");
    }

    //G6. Vorhandene Kraft und neue Kraft ergeben eine diagonale Bewegung
    @Test
    void moveHero_DirectionUpSpeedOneVelocityComponentContainsRightForce_ForceAppliedToHeroEqualsDiagonalUpRight(){
        //arrange
        Direction direction = Direction.UP;
        Vector2 speed = Vector2.ONE;
        Vector2 forceInX1Y0Direction = Vector2.of(1.0f, 0.0f);
        this.setAppliedForceOfVelocityComponentOfHeroBeforeMovement(forceInX1Y0Direction);
        when(this.mockHero.fetch(VelocityComponent.class)).thenReturn(Optional.of(this.mockVelocityComponent));

        //act
        HeroController.moveHero(this.mockHero, direction, speed);

        //assert
        Vector2 normalizedDiagonal = Vector2.of(1.0f/Math.sqrt(2.0f), 1.0f/Math.sqrt(2.0f));
        Vector2 appliedForceAfterMovement = this.getAppliedForceFromVelocityComponentOfHeroAfterMovement();
        assertTrue(this.compareVector2(appliedForceAfterMovement, normalizedDiagonal), "force applied to hero is not the sum of directions");
    }

    //U1. Steuerung des Helden deaktiviert
    @Test
    void moveHero_InputComponentDeactivateControls_FunctionEndsWithoutReturnValue(){
        //arrange
        Direction direction = Direction.UP;
        Vector2 speed = Vector2.ONE;
        when(this.mockHero.fetch(InputComponent.class)).thenReturn(Optional.of(this.mockInputComponent));
        when(this.mockInputComponent.deactivateControls()).thenReturn(true);

        //act
        HeroController.moveHero(this.mockHero, direction, speed);

        //assert
        verify(this.mockHero, never()).isPresent(InputComponent.class);
    }

    //U2. Bewegungsrichtung im InputComponent deaktiviert
    @Test
    void moveHero_InputComponentWithoutCallbacks_FunctionEndsWithoutReturnValue(){
        //arrange
        Direction direction = Direction.UP;
        Vector2 speed = Vector2.ONE;
        this.mockKeyBindings = new HashMap<>();
        when(this.mockHero.fetch(InputComponent.class)).thenReturn(Optional.of(this.mockInputComponent));
        when(this.mockInputComponent.deactivateControls()).thenReturn(false);
        when(this.mockHero.isPresent(InputComponent.class)).thenReturn(true);
        when(this.mockInputComponent.callbacks()).thenReturn(this.mockKeyBindings);

        //act
        HeroController.moveHero(this.mockHero, direction, speed);

        //assert
        verify(this.mockHero, never()).fetch(VelocityComponent.class);
    }

    //U3. VelocityComponent fehlt
    @Test
    void moveHero_NoVelocityComponent_MissingComponentException(){
        //arrange
        Direction direction = Direction.UP;
        Vector2 speed = Vector2.ONE;
        when(this.mockHero.fetch(VelocityComponent.class)).thenReturn(Optional.empty());

        //assert
        assertThrows(
            MissingComponentException.class,
            () -> {HeroController.moveHero(this.mockHero, direction, speed);},
            "no velocity component expected"
        );
    }

    //U4. Geschwindigkeit ergibt Nullkraft
    @Test
    void moveHero_DirectionAndSpeedFormZeroVectorNoForcesInVelocityComponent_VelocityComponentRemainsEmpty(){
        //arrange
        Direction direction = Direction.UP;
        Vector2 speed = Vector2.ZERO;
        when(this.mockHero.fetch(VelocityComponent.class)).thenReturn(Optional.of(this.mockVelocityComponent));

        //act
        HeroController.moveHero(this.mockHero, direction, speed);

        //assert
        Vector2 appliedForceAfterMovement = this.getAppliedForceFromVelocityComponentOfHeroAfterMovement();
        assertNull(appliedForceAfterMovement,"force applied to hero is not null");
    }

    //U5. Vorhandene Kraft und neue Kraft heben sich gegenseitig auf
    //Beim Testen ist aufgefallen dass dem nicht so ist. Die vorhandene Kraft bleibt erhalten.
    @Test
    void moveHero_DirectionUpSpeedOneVelocityComponentContainsDirectionDown_NOChangesInVelocityComponent(){
        //arrange
        Direction direction = Direction.UP;
        Vector2 speed = Vector2.ONE;
        Vector2 directionDown = Vector2.of(0.0f, -1.0f);
        this.setAppliedForceOfVelocityComponentOfHeroBeforeMovement(directionDown);
        when(this.mockHero.fetch(VelocityComponent.class)).thenReturn(Optional.of(this.mockVelocityComponent));

        //act
        HeroController.moveHero(this.mockHero, direction, speed);
        
        //assert
        Vector2 appliedForceAfterMovement = this.getAppliedForceFromVelocityComponentOfHeroAfterMovement();
        assertTrue(this.compareVector2(appliedForceAfterMovement, directionDown), "force applied to hero is not direction down");
    }

    //U6. Hero nicht vorhanden
    @Test
    void moveHero_HeroIsNull_NullPointerException(){
        //arrange
        Direction direction = Direction.UP;
        Vector2 speed = Vector2.ONE;

        //assert
        assertThrows(
            NullPointerException.class,
            () -> {HeroController.moveHero(null, direction, speed);},
            "no hero expected"
        );
    }

    //U7. Direction nicht vorhanden
    @Test
    void moveHero_DirectionIsNull_NullPointerException(){
        //arrange
        Vector2 speed = Vector2.ONE;
        when(this.mockHero.fetch(VelocityComponent.class)).thenReturn(Optional.of(this.mockVelocityComponent));

        //assert
        assertThrows(
            NullPointerException.class,
            () -> {HeroController.moveHero(this.mockHero, null, speed);},
            "no direction expected"
        );
    }

    //U8. Speed nicht vorhanden
    @Test
    void moveHero_SpeedIsNull_NullPointerException(){
        //arrange
        Direction direction = Direction.UP;
        when(this.mockHero.fetch(VelocityComponent.class)).thenReturn(Optional.of(this.mockVelocityComponent));

        //assert
        assertThrows(
            NullPointerException.class,
            () -> {HeroController.moveHero(this.mockHero, direction, null);},
            "no speed expected"
        );
    }
}
