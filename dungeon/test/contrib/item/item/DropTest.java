package contrib.item.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import contrib.entities.WorldItemBuilder;
import contrib.item.Item;
import core.Entity;
import core.Game;
import core.level.Tile;
import core.level.elements.tile.FloorTile;
import core.level.elements.tile.WallTile;
import core.utils.Point;
import core.utils.components.draw.animation.Animation;
import core.utils.components.path.SimpleIPath;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/** Tests for {@link Item#drop(Point)}. */
public class DropTest {

  private static final Animation ANIMATION =
      new Animation(new SimpleIPath("animation/missing_texture.png"));

  private Item item;
  private Point position;

  /** Set up the test environment. */
  @BeforeEach
  public void setUp() {
    item = new Item("Test", "Test description", ANIMATION);
    position = new Point(1f, 1f);
  }

  // G1: position is on a FloorTile

  /** Dropping on a FloorTile builds a world item via the {@link WorldItemBuilder}. */
  @Test
  public void drop_positionOnFloorTile_createsWorldItemViaBuilder() {
    FloorTile floorTile = mock(FloorTile.class);
    Entity worldItemEntity = mock(Entity.class);

    try (MockedStatic<Game> game = mockStatic(Game.class);
        MockedStatic<WorldItemBuilder> builder = mockStatic(WorldItemBuilder.class)) {
      game.when(() -> Game.tileAt(position)).thenReturn(Optional.of(floorTile));
      builder
          .when(() -> WorldItemBuilder.buildWorldItemSimpleInteraction(item, position))
          .thenReturn(worldItemEntity);

      item.drop(position);

      builder.verify(
          () -> WorldItemBuilder.buildWorldItemSimpleInteraction(item, position), times(1));
    }
  }

  /** Dropping on a FloorTile adds the created entity to the game. */
  @Test
  public void drop_positionOnFloorTile_addsEntityToGame() {
    FloorTile floorTile = mock(FloorTile.class);
    Entity worldItemEntity = mock(Entity.class);

    try (MockedStatic<Game> game = mockStatic(Game.class);
        MockedStatic<WorldItemBuilder> builder = mockStatic(WorldItemBuilder.class)) {
      game.when(() -> Game.tileAt(position)).thenReturn(Optional.of(floorTile));
      builder
          .when(() -> WorldItemBuilder.buildWorldItemSimpleInteraction(any(), any()))
          .thenReturn(worldItemEntity);

      item.drop(position);

      game.verify(() -> Game.add(worldItemEntity), times(1));
    }
  }

  /** Dropping on a FloorTile returns a filled {@link Optional} containing the created entity. */
  @Test
  public void drop_positionOnFloorTile_returnsFilledOptional() {
    FloorTile floorTile = mock(FloorTile.class);
    Entity worldItemEntity = mock(Entity.class);

    try (MockedStatic<Game> game = mockStatic(Game.class);
        MockedStatic<WorldItemBuilder> builder = mockStatic(WorldItemBuilder.class)) {
      game.when(() -> Game.tileAt(position)).thenReturn(Optional.of(floorTile));
      builder
          .when(() -> WorldItemBuilder.buildWorldItemSimpleInteraction(any(), any()))
          .thenReturn(worldItemEntity);

      Optional<Entity> result = item.drop(position);

      assertTrue(result.isPresent(), "Optional should be filled for a FloorTile position.");
      assertEquals(worldItemEntity, result.get());
    }
  }

  // U1: position is not on a FloorTile

  /** Dropping on a non-FloorTile tile does not build a world item. */
  @Test
  public void drop_positionOnNonFloorTile_doesNotCreateWorldItem() {
    WallTile wallTile = mock(WallTile.class);

    try (MockedStatic<Game> game = mockStatic(Game.class);
        MockedStatic<WorldItemBuilder> builder = mockStatic(WorldItemBuilder.class)) {
      game.when(() -> Game.tileAt(position)).thenReturn(Optional.of(wallTile));

      item.drop(position);

      builder.verify(() -> WorldItemBuilder.buildWorldItemSimpleInteraction(any(), any()), never());
      builder.verify(() -> WorldItemBuilder.buildWorldItem(any(), any()), never());
    }
  }

  /** Dropping on a non-FloorTile tile does not add anything to the game. */
  @Test
  public void drop_positionOnNonFloorTile_doesNotAddEntityToGame() {
    WallTile wallTile = mock(WallTile.class);

    try (MockedStatic<Game> game = mockStatic(Game.class)) {
      game.when(() -> Game.tileAt(position)).thenReturn(Optional.of(wallTile));

      item.drop(position);

      game.verify(() -> Game.add(any(Entity.class)), never());
    }
  }

  /** Dropping on a non-FloorTile tile returns {@link Optional#empty()}. */
  @Test
  public void drop_positionOnNonFloorTile_returnsEmptyOptional() {
    WallTile wallTile = mock(WallTile.class);

    try (MockedStatic<Game> game = mockStatic(Game.class)) {
      game.when(() -> Game.tileAt(position)).thenReturn(Optional.of(wallTile));

      Optional<Entity> result = item.drop(position);

      assertTrue(result.isEmpty(), "Optional should be empty for a non-FloorTile position.");
    }
  }

  /**
   * A position without any tile (e.g. outside the map) is a second flavour of U1: {@link
   * Game#tileAt(Point)} returns {@link Optional#empty()}, so the {@code tile instanceof FloorTile}
   * check must also handle {@code null} safely.
   */
  @Test
  public void drop_positionWithoutTile_returnsEmptyOptional() {
    try (MockedStatic<Game> game = mockStatic(Game.class);
        MockedStatic<WorldItemBuilder> builder = mockStatic(WorldItemBuilder.class)) {
      game.when(() -> Game.tileAt(position)).thenReturn(Optional.<Tile>empty());

      Optional<Entity> result = item.drop(position);

      assertTrue(result.isEmpty(), "Optional should be empty when no tile exists.");
      game.verify(() -> Game.add(any(Entity.class)), never());
      builder.verify(() -> WorldItemBuilder.buildWorldItemSimpleInteraction(any(), any()), never());
    }
  }
}
