package contrib.modules.keypadFolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import contrib.modules.keypad.KeypadComponent;
import core.Entity;
import java.util.List;
import org.junit.jupiter.api.Test;

class GetterSetterTest {

  @Test
  void testGetterSetter() {

    KeypadComponent keypad = new KeypadComponent(List.of(1, 2, 3), () -> {});

    Runnable action = () -> {};
    Entity overlay = new Entity();

    keypad.isUIOpen(true);
    keypad.isUnlocked(true);
    keypad.showDigitCount(false);
    keypad.action(action);
    keypad.overlay(overlay);

    assertTrue(keypad.isUIOpen());
    assertTrue(keypad.isUnlocked());
    assertFalse(keypad.showDigitCount());
    assertEquals(action, keypad.action());
    assertEquals(overlay, keypad.overlay());
  }
}
