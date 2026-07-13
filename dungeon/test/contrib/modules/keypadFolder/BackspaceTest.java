package contrib.modules.keypadFolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import contrib.modules.keypad.KeypadComponent;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class BackspaceTest {

    @Test
    void backspaceRemovesLastDigit() {

        KeypadComponent keypad = new KeypadComponent(
                List.of(1,2,3),
                new ArrayList<>(List.of(1,2,3)),
                false,
                false);

        keypad.backspace();

        assertEquals(List.of(1,2), keypad.enteredDigits());
    }

    @Test
    void backspaceDoesNothingWhenUnlocked() {

        KeypadComponent keypad = new KeypadComponent(
                List.of(1,2,3),
                new ArrayList<>(List.of(1,2,3)),
                true,
                false);

        keypad.backspace();

        assertEquals(List.of(1,2,3), keypad.enteredDigits());
    }
}