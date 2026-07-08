package contrib.item.concreteItem;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import contrib.components.CollideComponent;
import contrib.components.HealthComponent;
import contrib.entities.WorldItemBuilder;
import core.Entity;
import core.Game;
import core.level.elements.tile.FloorTile;
import core.utils.Direction;
import core.utils.Point;
import core.utils.TriConsumer;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

class ItemFairyCollisionTest {

  private TriConsumer<Entity, Entity, Direction> onCollideHandler;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void setUp() {
    try (MockedStatic<Game> mockedGame = mockStatic(Game.class);
        MockedStatic<WorldItemBuilder> mockedBuilder = mockStatic(WorldItemBuilder.class);
        MockedConstruction<CollideComponent> mockedComponent =
            mockConstruction(
                CollideComponent.class,
                (mock, context) -> {
                  this.onCollideHandler =
                      (TriConsumer<Entity, Entity, Direction>) context.arguments().get(0);
                })) {

      mockedGame
          .when(() -> Game.tileAt((Point) any()))
          .thenReturn(Optional.of(mock(FloorTile.class)));
      mockedBuilder
          .when(() -> WorldItemBuilder.buildWorldItemSimpleInteraction(any(), any()))
          .thenReturn(mock(Entity.class));

      new ItemFairy().drop(new Point(0, 0));
    }

    // Sicherheits-Check, falls sich die Engine ändert
    assertNotNull(onCollideHandler, "Konnte den Kollisions-Listener nicht extrahieren!");
  }

  // --- GÜLTIGE ÄQUIVALENZKLASSEN ---

  /**
   * Prüft den perfekten Ablauf: Der Spieler berührt die Fee, seine Lebenspunkte werden auf das
   * Maximum gesetzt und die Fee verschwindet.
   */
  @Test
  void testOnCollideG1_PlayerWithHealth() {
    // Arrange
    Entity selfFairy = mock(Entity.class);
    Entity player = mock(Entity.class);
    HealthComponent healthMock = mock(HealthComponent.class);

    when(player.fetch(HealthComponent.class)).thenReturn(Optional.of(healthMock));
    when(healthMock.maximalHealthpoints()).thenReturn(100);

    try (MockedStatic<Game> mockedGame = mockStatic(Game.class)) {
      mockedGame.when(Game::player).thenReturn(Optional.of(player));

      // Act:
      onCollideHandler.accept(selfFairy, player, null);

      // Assert: Spieler wurde geheilt
      verify(healthMock, times(1)).restoreHealthpoints(100);

      // Assert: Fee wurde entfernt
      mockedGame.verify(() -> Game.remove(selfFairy), times(1));
    }
  }

  /**
   * Stellt sicher, dass die Fee auch dann aus dem Spiel entfernt wird, wenn der Spieler (z. B.
   * durch einen Bug) gar keine Lebenspunkte-Komponente besitzt.
   */
  @Test
  void testOnCollideG2_PlayerWithoutHealth() {
    // Arrange
    Entity selfFairy = mock(Entity.class);
    Entity player = mock(Entity.class);

    // Spieler hat KEINE HealthComponent
    when(player.fetch(HealthComponent.class)).thenReturn(Optional.empty());

    try (MockedStatic<Game> mockedGame = mockStatic(Game.class)) {
      mockedGame.when(Game::player).thenReturn(Optional.of(player));

      // Act
      onCollideHandler.accept(selfFairy, player, null);

      // Assert: Fee wurde entfernt (WICHTIG: Das muss trotzdem passieren!)
      mockedGame.verify(() -> Game.remove(selfFairy), times(1));
    }
  }

  /**
   * Verifiziert, dass Monster oder NPCs die Fee nicht aus Versehen einsammeln können und sie in
   * diesem Fall einfach liegen bleibt.
   */
  @Test
  void testOnCollideG3_OtherEntity() {
    // Arrange
    Entity selfFairy = mock(Entity.class);
    Entity player = mock(Entity.class);
    Entity otherMonster = mock(Entity.class);

    try (MockedStatic<Game> mockedGame = mockStatic(Game.class)) {
      mockedGame.when(Game::player).thenReturn(Optional.of(player));

      // Act: Kollision mit dem Monster
      onCollideHandler.accept(selfFairy, otherMonster, null);

      // Assert: Monster wird nicht geheilt (es erfolgt gar kein fetch)
      verify(otherMonster, never()).fetch(any());

      // Assert: Fee bleibt im Spiel
      mockedGame.verify(() -> Game.remove((Entity) any()), never());
    }
  }

  /**
   * Prüft, dass das Spiel nicht abstürzt, wenn im Moment der Kollision aus irgendeinem Grund kein
   * aktiver Spieler im System registriert ist.
   */
  @Test
  void testOnCollideG4_PlayerDoesNotExistInGame() {
    // Arrange
    Entity selfFairy = mock(Entity.class);
    Entity someEntity = mock(Entity.class);

    try (MockedStatic<Game> mockedGame = mockStatic(Game.class)) {
      // Game liefert keinen Spieler
      mockedGame.when(Game::player).thenReturn(Optional.empty());

      // Act
      onCollideHandler.accept(selfFairy, someEntity, null);

      // Assert: Es passiert absolut nichts
      verify(someEntity, never()).fetch(any());
      mockedGame.verify(() -> Game.remove((Entity) any()), never());
    }
  }

  // --- UNGÜLTIGE ÄQUIVALENZKLASSEN ---

  /**
   * Bestätigt, dass fehlerhafte Kollisionsaufrufe der Engine (bei denen der Kollisionspartner null
   * ist) sofort zu einer erwarteten Exception führen, statt stillschweigend Fehler zu verursachen.
   */
  @Test
  void testOnCollideU1_NullReferences() {
    // Arrange
    Entity player = mock(Entity.class);
    Entity selfFairy = mock(Entity.class);

    try (MockedStatic<Game> mockedGame = mockStatic(Game.class)) {
      mockedGame.when(Game::player).thenReturn(Optional.of(player));

      // Act & Assert: other ist null
      assertThrows(
          NullPointerException.class,
          () -> {
            onCollideHandler.accept(selfFairy, null, null);
          },
          "Der Aufruf other.equals(player) muss eine NPE werfen, wenn other null ist.");
    }
  }
}
