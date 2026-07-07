package contrib.utils.components.health;

import contrib.systems.HealthSystem;

/**
 * Fake implementation of {@link IHealthObserver} used for testing purposes.
 *
 * <p>This observer records the last received health event and the associated
 * {@link HealthSystem.HSData} so that tests can verify observer notifications.
 */
public class FakeHealthObserver implements IHealthObserver {


  private boolean eventReceived = false;

  private HealthEvent lastEvent = null;

  private HealthSystem.HSData lastData = null;

  /**
   * Records the received health event and its associated data.
   *
   * @param hsData The health system data associated with the event.
   * @param healthEvent The received health event.
   */
  @Override
  public void onHealthEvent(
    HealthSystem.HSData hsData,
    HealthEvent healthEvent
  ) {

    this.eventReceived = true;
    this.lastEvent = healthEvent;
    this.lastData = hsData;
  }

  /**
   * Indicates whether a health event has been received.
   *
   * @return {@code true} if an event has been received, otherwise {@code false}.
   */
  public boolean eventReceived() {
    return eventReceived;
  }

  /**
   * Returns the last received health event.
   *
   * @return The last received {@link HealthEvent}, or {@code null} if no event has
   *     been received.
   */
  public HealthEvent lastEvent() {
    return lastEvent;
  }

  /**
   * Returns the data associated with the last received health event.
   *
   * @return The last received {@link HealthSystem.HSData}, or {@code null} if no
   *     event has been received.
   */
  public HealthSystem.HSData lastData() {
    return lastData;
  }
}
