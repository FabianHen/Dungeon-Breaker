package contrib.item.concreteItem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

import contrib.components.CollideComponent;
import contrib.entities.WorldItemBuilder;
import contrib.item.Item;
import core.Entity;
import core.Game;
import core.level.elements.tile.FloorTile;
import core.level.elements.tile.WallTile;
import core.utils.Point;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class ItemFairyDropTest {

  private ItemFairy itemFairy;
  private Point validPosition;

  @BeforeEach
  void setUp() {
    itemFairy = new ItemFairy();
    validPosition = new Point(2f, 2f);
  }

  /**
   * Verifiziert den Erfolgsfall: Wenn die Zielposition ein begehbarer Boden ist, wird das Item
   * korrekt als Entität erstellt und in die Spielwelt eingefügt.
   */
  @Test
  void testDropG1_PositionOnFloorTile() {
    // Arrange
    FloorTile mockFloorTile = mock(FloorTile.class);
    Entity localMockEntity = mock(Entity.class);

    try (MockedStatic<Game> mockedGame = mockStatic(Game.class);
        MockedStatic<WorldItemBuilder> mockedBuilder = mockStatic(WorldItemBuilder.class)) {

      mockedGame.when(() -> Game.tileAt(validPosition)).thenReturn(Optional.of(mockFloorTile));

      mockedBuilder
          .when(
              () ->
                  WorldItemBuilder.buildWorldItemSimpleInteraction(
                      any(Item.class), any(Point.class)))
          .thenReturn(localMockEntity);

      // Act
      Optional<Entity> result = itemFairy.drop(validPosition);

      // Assert
      assertEquals(true, result.isPresent(), "Das Optional sollte nicht leer sein.");
      assertEquals(
          localMockEntity,
          result.get(),
          "Das zurückgegebene Item entspricht nicht der erstellten Entität.");

      // Assert: Der Entität wird eine CollideComponent hinzugefügt
      verify(localMockEntity, times(1)).add(any(CollideComponent.class));

      // Assert: Die Entität wird dem Spiel hinzugefügt
      mockedGame.verify(() -> Game.add(localMockEntity), times(1));
    }
  }

  /**
   * Prüft, dass auf ungültigen Feldern (wie Wänden) der Drop abgebrochen wird und keine Entität im
   * Spiel landet.
   */
  @Test
  void testDropG2_PositionOnOtherTile() {
    // Arrange
    WallTile mockWallTile = mock(WallTile.class);

    try (MockedStatic<Game> mockedGame = mockStatic(Game.class);
        MockedStatic<WorldItemBuilder> mockedBuilder = mockStatic(WorldItemBuilder.class)) {

      mockedGame.when(() -> Game.tileAt(validPosition)).thenReturn(Optional.of(mockWallTile));

      // Act
      Optional<Entity> result = itemFairy.drop(validPosition);

      // Assert: Methode gibt Optional.empty() zurück
      assertEquals(
          true, result.isEmpty(), "Das Optional sollte leer sein, wenn es kein FloorTile ist.");

      // Assert: Es wird keine Entität dem Spiel hinzugefügt
      mockedGame.verify(() -> Game.add((Entity) any()), never());
      mockedBuilder.verify(
          () -> WorldItemBuilder.buildWorldItemSimpleInteraction(any(), any()), never());
    }
  }

  /**
   * Stellt sicher, dass Positionen außerhalb der Karte (wo gar keine Felder existieren) ebenfalls
   * sicher ignoriert werden.
   */
  @Test
  void testDropG3_PositionOutsideMap() {
    // Arrange
    try (MockedStatic<Game> mockedGame = mockStatic(Game.class);
        MockedStatic<WorldItemBuilder> mockedBuilder = mockStatic(WorldItemBuilder.class)) {
      mockedGame.when(() -> Game.tileAt(validPosition)).thenReturn(Optional.empty());

      // Act
      Optional<Entity> result = itemFairy.drop(validPosition);

      // Assert:
      assertEquals(
          true,
          result.isEmpty(),
          "Das Optional sollte leer sein, wenn die Position außerhalb der Map liegt (null).");

      // Assert: Es wird nichts gebaut oder hinzugefügt
      mockedGame.verify(() -> Game.add((Entity) any()), never());
      mockedBuilder.verify(
          () -> WorldItemBuilder.buildWorldItemSimpleInteraction(any(), any()), never());
    }
  }

  /**
   * Kontrolliert, dass eine komplett fehlende Positionsangabe (null) nicht heimlich verschluckt
   * wird, sondern die zugehörige Exception sauber nach oben durchgereicht wird.
   */
  @Test
  void testDropU1_NullReferenceForPosition() {
    // Arrange
    try (MockedStatic<Game> mockedGame = mockStatic(Game.class)) {
      mockedGame
          .when(() -> Game.tileAt((Point) null))
          .thenThrow(new NullPointerException("Position cannot be null"));

      // Act & Assert
      assertThrows(
          NullPointerException.class,
          () -> {
            itemFairy.drop(null);
          },
          "Wenn Game.tileAt null nicht verarbeiten kann, muss die NPE nach oben durchgereicht werden.");
    }
  }
}
