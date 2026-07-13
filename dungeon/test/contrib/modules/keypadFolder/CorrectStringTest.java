package contrib.modules.keypadFolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import contrib.modules.keypad.KeypadComponent;

import java.util.List;
import org.junit.jupiter.api.Test;

class CorrectStringTest {

    @Test
    void correctStringReturnsCorrectDigits() {

        KeypadComponent keypad =
                new KeypadComponent(List.of(1,2,3,4), () -> {});

        assertEquals("1234", keypad.correctString());
    }
}