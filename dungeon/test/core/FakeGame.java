package core;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A lightweight game implementation used for testing purposes.
 *
 * <p>This class stores entities independently of the game framework and provides
 * methods to add, remove, filter and clear entities.
 */
public class FakeGame {

  private final Set<Entity> entities = new HashSet<>();

  /**
   * Adds an entity to the fake game.
   *
   * @param entity The entity to add.
   * @return The added entity.
   */
  public Entity add(Entity entity) {
    entities.add(entity);
    return entity;
  }
  /**
   * Removes an entity from the fake game.
   *
   * @param entity The entity to remove.
   * @return The removed entity.
   */
  public Entity remove(Entity entity) {
    entities.remove(entity);
    return entity;
  }

  /**
   * Returns a stream containing all entities currently stored in the fake game.
   *
   * @return A stream of all entities.
   */
  public Stream<Entity> allEntities() {
    return entities.stream();
  }

  /**
   * Returns a stream of all entities that contain all specified component types.
   *
   * @param components The component types an entity must contain.
   * @return A stream of entities matching the given component requirements.
   */
  public final Stream<Entity> filteredEntityStream(Class<? extends Component>... components) {

    return entities.stream()
        .filter(entity -> java.util.Arrays.stream(components).allMatch(entity::isPresent));
  }

  /**
   * Removes all entities from the fake game.
   */
  public void clear() {
    entities.clear();
  }
}
