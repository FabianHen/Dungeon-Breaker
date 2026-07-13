package contrib.modules.keypadFolder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import contrib.modules.keypad.KeypadComponent;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class EnteredStringTest {

  @Test
  void enteredStringReturnsEnteredDigits() {
    KeypadComponent keypad =
        new KeypadComponent(List.of(1, 2, 3, 4), new ArrayList<>(List.of(1, 2)), false, false);

    assertEquals("12", keypad.enteredString());
  }

  @Test
  void enteredStringFillsMissingDigitsWithStars() {
    KeypadComponent keypad =
        new KeypadComponent(List.of(1, 2, 3, 4), new ArrayList<>(List.of(1, 2)), false, true);

    assertEquals("12**", keypad.enteredString());
  }
}
