package contrib.entities;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import contrib.components.SkillComponent;
import contrib.utils.components.skill.Skill;
import core.Entity;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Test für HeroController.changeSecondSkill() */
public class ChangeSecondSkillTest {
  @Mock private Entity mockHero;

  @Mock private SkillComponent mockSkillComponent;

  private Entity heroWithRealSkillComponent;
  private SkillComponent realSkillComponent;
  private Skill firstSkill;
  private Skill lastSkill;

  private AutoCloseable mocks;

  @BeforeEach
  void setUp() {
    mocks = MockitoAnnotations.openMocks(this);

    firstSkill = mock(Skill.class);
    lastSkill = mock(Skill.class);
    Skill middleSkill = mock(Skill.class);

    realSkillComponent = new SkillComponent(firstSkill, middleSkill, lastSkill);
    heroWithRealSkillComponent = mock(Entity.class);

    when(heroWithRealSkillComponent.fetch(SkillComponent.class))
        .thenReturn(Optional.of(realSkillComponent));
    when(heroWithRealSkillComponent.id()).thenReturn(1);
  }

  @AfterEach
  void tearDown() throws Exception {
    mocks.close();
  }

  @Test
  void changeSecondSkill_WithSkillComponentAndNextSkill_CallsNextSecondSkill() {
    // Arrange
    when(mockHero.fetch(SkillComponent.class)).thenReturn(Optional.of(mockSkillComponent));
    when(mockHero.id()).thenReturn(1);

    // Act
    HeroController.changeSecondSkill(mockHero, true);

    // Assert
    verify(mockSkillComponent, times(1)).nextSecondSkill();
    verify(mockSkillComponent, never()).prevSecondSkill();
  }

  @Test
  void changeSecondSkill_WithSkillComponentAndPrevSkill_CallsPrevSecondSkill() {
    // Arrange
    when(mockHero.fetch(SkillComponent.class)).thenReturn(Optional.of(mockSkillComponent));
    when(mockHero.id()).thenReturn(1);

    // Act
    HeroController.changeSecondSkill(mockHero, false);

    // Assert
    verify(mockSkillComponent, times(1)).prevSecondSkill();
    verify(mockSkillComponent, never()).nextSecondSkill();
  }

  @Test
  void changeSecondSkill_WithoutSkillComponent_DoesNothing() {
    // Arrange
    when(mockHero.fetch(SkillComponent.class)).thenReturn(Optional.empty());
    when(mockHero.id()).thenReturn(1);

    // Act
    HeroController.changeSecondSkill(mockHero, true);

    // Assert
    verify(mockHero, times(1)).fetch(SkillComponent.class);
    verifyNoInteractions(mockSkillComponent);
  }

  @Test
  void changeSecondSkill_MultipleCalls_InvokesMethodsCorrectly() {
    // Arrange
    when(mockHero.fetch(SkillComponent.class)).thenReturn(Optional.of(mockSkillComponent));
    when(mockHero.id()).thenReturn(1);

    // Act
    HeroController.changeSecondSkill(mockHero, true);
    HeroController.changeSecondSkill(mockHero, true);
    HeroController.changeSecondSkill(mockHero, false);

    // Assert
    verify(mockSkillComponent, times(2)).nextSecondSkill();
    verify(mockSkillComponent, times(1)).prevSecondSkill();
  }

  @Test
  void changeSecondSkill_LastSkillAndNextSkill_WrapsFirstSkill() {
    // Arrange
    realSkillComponent.nextSecondSkill();
    realSkillComponent.nextMainSkill();

    // Act
    HeroController.changeSecondSkill(heroWithRealSkillComponent, true);

    // Assert
    assertSame(firstSkill, realSkillComponent.activeSecondSkill().orElseThrow());
  }

  @Test
  void changeSecondSkill_FirstSkillAndPrevSkill_WrapToLastSkill() {
    // Arrange
    realSkillComponent.nextSecondSkill();
    realSkillComponent.nextMainSkill();
    realSkillComponent.prevSecondSkill();

    // Act
    HeroController.changeSecondSkill(heroWithRealSkillComponent, false);

    // Assert
    assertSame(lastSkill, realSkillComponent.activeSecondSkill().orElseThrow());
  }

  @Test
  void changeSecondSkill_WithNullHero_ThrowsNullPointerException() {
    // Arrange
    Entity nullHero = null;

    // Act & Assert
    assertThrows(
        NullPointerException.class, () -> HeroController.changeSecondSkill(nullHero, true));
  }
}
