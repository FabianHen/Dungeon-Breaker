package contrib.modules.keypadFolder;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import contrib.modules.keypad.KeypadComponent;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

class CheckUnlockTest {

    @Test
    void checkUnlockUnlocksCorrectCode() {

        AtomicBoolean executed = new AtomicBoolean(false);

        KeypadComponent keypad =
                new KeypadComponent(List.of(1,2,3),
                        () -> executed.set(true),
                        false);

        keypad.enteredDigits().addAll(List.of(1,2,3));

        keypad.checkUnlock();

        assertTrue(keypad.isUnlocked());
        assertTrue(executed.get());
    }

    @Test
    void checkUnlockRejectsWrongCode() {

        AtomicBoolean executed = new AtomicBoolean(false);

        KeypadComponent keypad =
                new KeypadComponent(List.of(1,2,3),
                        () -> executed.set(true),
                        false);

        keypad.enteredDigits().addAll(List.of(1,2,4));

        keypad.checkUnlock();

        assertFalse(keypad.isUnlocked());
        assertFalse(executed.get());
    }
}