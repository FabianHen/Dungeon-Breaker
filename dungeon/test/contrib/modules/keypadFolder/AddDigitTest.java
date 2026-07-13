package contrib.modules.keypadFolder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import contrib.modules.keypad.KeypadComponent;
import java.util.List;
import org.junit.jupiter.api.Test;

class AddDigitTest {

  @Test
  void addDigitAddsDigit() {

    KeypadComponent keypad = new KeypadComponent(List.of(1, 2, 3, 4), () -> {}, false);

    keypad.addDigit(5);

    assertEquals(List.of(5), keypad.enteredDigits());
  }

  @Test
  void addDigitDoesNothingWhenUnlocked() {

    KeypadComponent keypad = new KeypadComponent(List.of(1, 2, 3, 4), () -> {}, false);

    keypad.isUnlocked(true);

    keypad.addDigit(5);

    assertEquals(0, keypad.enteredDigits().size());
  }
}
