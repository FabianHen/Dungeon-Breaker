package contrib.systems;

import contrib.components.HealthComponent;
import core.FakeGame;
import core.components.DrawComponent;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Fake HealthSystemClass for test purposes */
public class MockHealthSystem extends HealthSystem {

  private final FakeGame game;

  /**
   * Creates a mock health system for testing purposes.
   *
   * @param game The fake game instance whose entities are processed by this system.
   */
  public MockHealthSystem(FakeGame game) {
    this.game = game;
  }
  /**
   * Exposes the protected {@link HealthSystem#applyDamage(HSData)} method for testing.
   *
   * @param data The health system data containing the entity and its components.
   * @return The processed {@link HSData} instance.
   */
  public HSData applyDamagePublic(HSData data) {
    return super.applyDamage(data);
  }
  /**
   * Exposes the protected {@link HealthSystem#calculateDamage(HSData)} method for testing.
   *
   * @param data The health system data containing the entity and its components.
   * @return The total calculated damage.
   */
  public int calculateDamagePublic(HSData data) {
    return super.calculateDamage(data);
  }
  /**
   * Exposes the protected {@link HealthSystem#triggerOnDeath(HSData)} method for testing.
   *
   * @param data The health system data of the entity whose death should be triggered.
   */
  public void triggerOnDeathPublic(HSData data) {
    super.triggerOnDeath(data);
  }
  /**
   * Executes the mock health system on all entities of the associated {@link FakeGame}.
   *
   * <p>Alive entities receive pending damage, while dead entities are processed for death
   * animations and death handling.
   */
  @Override
  public void execute() {

    Map<Boolean, List<HSData>> deadOrAlive =
        game.allEntities()
            .filter(e -> e.isPresent(HealthComponent.class))
            .filter(e -> e.isPresent(HealthComponent.class))
            .map(
                e ->
                    new HSData(
                        e,
                        e.fetch(HealthComponent.class).orElseThrow(),
                        e.fetch(DrawComponent.class).orElseThrow()))
            .collect(Collectors.partitioningBy(hsd -> hsd.hc().isDead()));

    deadOrAlive.get(false).forEach(this::applyDamagePublic);

    deadOrAlive.get(true).stream()
        .map(this::activateDeathAnimation)
        .filter(this::isDeathAnimationFinished)
        .filter(hsd -> !hsd.hc().alreadyDead())
        .forEach(this::triggerOnDeathPublic);
  }
}
