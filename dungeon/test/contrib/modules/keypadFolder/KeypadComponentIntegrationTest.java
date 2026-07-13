package contrib.modules.keypadFolder;

import static org.junit.jupiter.api.Assertions.assertTrue;

import contrib.modules.keypad.KeypadComponent;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

class KeypadComponentIntegrationTest {

  @Test
  void enteringCorrectCodeExecutesAction() {

    AtomicBoolean executed = new AtomicBoolean(false);

    KeypadComponent keypad =
        new KeypadComponent(List.of(1, 2, 3, 4), () -> executed.set(true), false);

    keypad.addDigit(1);
    keypad.addDigit(2);
    keypad.addDigit(3);
    keypad.addDigit(4);

    keypad.checkUnlock();

    assertTrue(keypad.isUnlocked());
    assertTrue(executed.get());
  }
}
