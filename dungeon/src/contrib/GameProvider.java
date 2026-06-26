package contrib;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.scenes.scene2d.Stage;
import core.Entity;
import core.System;
import core.Component;
import core.level.Tile;
import core.level.elements.ILevel;
import core.level.elements.tile.ExitTile;
import core.level.utils.Coordinate;
import core.level.utils.LevelElement;
import core.network.handler.INetworkHandler;
import core.sound.AudioApi;
import core.sound.player.ISoundPlayer;
import core.utils.Direction;
import core.utils.IVoidFunction;
import core.utils.Point;
import core.utils.components.path.IPath;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Vollständige Schnittstelle für alle Kernfunktionen des Spiels.
 * Entkoppelt Systeme und Komponenten von der statischen Game-Klasse.
 */
public interface GameProvider {

  // --- Lifecycle & Window ---
  void run();
  void exit();
  void exit(String reason);
  int windowWidth();
  void windowWidth(int windowWidth);
  int windowHeight();
  void windowHeight(int windowHeight);
  int frameRate();
  void frameRate(int frameRate);
  boolean resizeable();
  void resizeable(boolean resizeable);
  String windowTitle();
  void windowTitle(final String newTitle);
  IPath logoPath();
  void logoPath(IPath logoPath);
  boolean isHeadless();
  int currentTick();

  // --- Configuration & Settings ---
  void disableAudio(boolean disableAudio);
  void enableCheckPattern(boolean enabled);
  boolean isCheckPatternEnabled();
  void loadConfig(final IPath path, final Class<?>... keyboardConfigClass) throws IOException;
  void userOnFrame(final IVoidFunction userOnFrame);
  void userOnSetup(final IVoidFunction userOnSetup);
  void userOnLevelLoad(final Consumer<Boolean> userOnLevelLoad);

  // --- Networking ---
  boolean isMultiplayerClient();
  void initializeNetwork();
  INetworkHandler network();

  // --- UI ---
  Optional<Stage> stage();

  // --- Entity & System Management (ECS) ---
  void add(final Entity entity);
  void remove(final Entity entity);
  void removeAllEntities();
  Optional<System> add(final System system);
  void remove(final Class<? extends System> system);
  void removeAllSystems();
  Map<Class<? extends System>, System> systems();
  <T extends System> void system(Class<T> s, Consumer<T> c);

  Stream<Entity> allEntities();
  Stream<Entity> levelEntities();
  Stream<Entity> levelEntities(final System system);
  Stream<Entity> levelEntities(final Set<Class<? extends Component>> filter);

  Optional<Entity> player();
  Stream<Entity> allPlayers();
  Optional<Entity> findEntityById(int entityId);
  Optional<Entity> findInAll(final Component component);
  Optional<Entity> findInLevel(final Component component);
  boolean existInLevel(Entity entity);
  boolean existInAll(final Entity entity);

  // --- Level & Tiles ---
  Optional<ILevel> currentLevel();
  void currentLevel(final ILevel level);

  Optional<Tile> tileAt(final Point point);
  Optional<Tile> tileAt(final Coordinate coordinate);
  Optional<Tile> tileAt(final Coordinate coordinate, Direction direction);
  Optional<Tile> tileAt(final Point point, Direction direction);
  Optional<Tile> tileAtEntity(final Entity entity);

  Optional<Tile> randomTile();
  Optional<Tile> randomTile(final LevelElement elementType);
  Optional<Point> randomTilePoint();
  Optional<Point> randomTilePoint(final LevelElement elementTyp);

  Set<Tile> neighbours(final Tile tile);
  Optional<Tile> startTile();
  @Deprecated Optional<Tile> endTile();
  Set<ExitTile> endTiles();

  Set<Tile> allTiles();
  Set<Tile> allTiles(Predicate<Tile> filterRule);
  Set<Tile> allTiles(final LevelElement elementTyp);
  Set<Tile> allFreeTiles();
  Optional<Tile> freeTile();
  Optional<Point> freePosition();
  boolean isFreeTile(Tile tile);

  List<Tile> accessibleTilesInRange(final Point center, float radius);
  Optional<GraphPath<Tile>> findPath(final Tile start, final Tile end);
  Stream<Entity> entityAtTile(final Tile check);
  Stream<Entity> entityAtPoint(Point point);
  Optional<Point> positionOf(final Entity entity);

  // --- Audio ---
  AudioApi audio();
  ISoundPlayer soundPlayer();
}
