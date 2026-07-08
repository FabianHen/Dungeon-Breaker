package contrib.systems;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class EventSchedulerTest {

  @BeforeEach
  void setUp() {
    EventScheduler.clear();
    EventScheduler.setMillisForTesting(1000);
  }

  @AfterEach
  void tearDown() {
    EventScheduler.clear();
    EventScheduler.resetMillisForTesting();
    EventScheduler.setPausable(true);
  }
}
