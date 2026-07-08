package contrib.item.concreteItem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import contrib.components.InventoryComponent;
import core.Entity;
import core.components.PositionComponent;
import core.utils.Point;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Tests für die Klasse {@link ItemBigKey}. */
public class ItemBigKeyTest {

  // --- Konstruktor ---

  /**
   * G1: Prüft die Standard-Initialisierung durch den parameterlosen Konstruktor.
   */
  @Test
  public void testConstructorG1_StandardInitialisierung() {
    // Arrange (nichts vorzubereiten)

    // Act
    ItemBigKey bigKey = new ItemBigKey();

    // Assert
    assertNotNull(bigKey);
    assertEquals("Großer Schlüssel", bigKey.displayName());
    assertEquals("Ein großer goldener Schlüssel. Was er wohl öffnet?", bigKey.description());
    assertEquals("items/key/big_key.png", ItemBigKey.BIG_KEY_TEXTURE.pathString());
  }

  // --- use(Entity user) ---

  /**
   * G1: Prüft den Normalfall mit Mocks (Position und Inventar vorhanden).
   */
  @Test
  public void testUseG1_EntityWithPositionAndInventory() {
    // Arrange
    ItemBigKey spyKey = spy(new ItemBigKey());
    doReturn(Optional.empty()).when(spyKey).drop(any());

    Entity mockUser = mock(Entity.class);
    PositionComponent mockPos = mock(PositionComponent.class);
    InventoryComponent mockInv = mock(InventoryComponent.class);
    Point testPoint = new Point(1, 1);

    when(mockPos.position()).thenReturn(testPoint);
    when(mockUser.fetch(PositionComponent.class)).thenReturn(Optional.of(mockPos));
    when(mockUser.fetch(InventoryComponent.class)).thenReturn(Optional.of(mockInv));

    // Act
    spyKey.use(mockUser);

    // Assert
    verify(spyKey).drop(testPoint);
    verify(mockInv).remove(spyKey);
    assertTrue(true); // Workaround: DesigniteJava False Positive "Missing Assertion"
  }

  /**
   * G2: Prüft das Verhalten ohne Inventarkomponente.
   */
  @Test
  public void testUseG2_EntityWithPositionNoInventory() {
    // Arrange
    ItemBigKey spyKey = spy(new ItemBigKey());
    doReturn(Optional.empty()).when(spyKey).drop(any());

    Entity mockUser = mock(Entity.class);
    PositionComponent mockPos = mock(PositionComponent.class);
    Point testPoint = new Point(2, 2);

    when(mockPos.position()).thenReturn(testPoint);
    when(mockUser.fetch(PositionComponent.class)).thenReturn(Optional.of(mockPos));
    when(mockUser.fetch(InventoryComponent.class)).thenReturn(Optional.empty());

    // Act
    spyKey.use(mockUser);

    // Assert
    verify(spyKey).drop(testPoint);
    assertTrue(true); // Workaround: DesigniteJava False Positive
  }

  /**
   * G3: Prüft das Verhalten ohne Positionskomponente.
   */
  @Test
  public void testUseG3_EntityWithoutPosition() {
    // Arrange
    ItemBigKey spyKey = spy(new ItemBigKey());
    doReturn(Optional.empty()).when(spyKey).drop(any());

    Entity mockUser = mock(Entity.class);
    when(mockUser.fetch(PositionComponent.class)).thenReturn(Optional.empty());

    // Act
    spyKey.use(mockUser);

    // Assert
    verify(spyKey, never()).drop(any());
    assertTrue(true); // Workaround: DesigniteJava False Positive
  }

  /**
   * U1: Prüft die Null-Referenz-Behandlung.
   */
  @Test
  public void testUseU1_NullUserThrowsNPE() {
    // Arrange
    ItemBigKey bigKey = new ItemBigKey();

    // Act & Assert
    assertThrows(NullPointerException.class, () -> bigKey.use(null));
  }
}
