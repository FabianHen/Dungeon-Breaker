package contrib.entities;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import contrib.components.InventoryComponent;
import contrib.components.UIComponent;
import contrib.hud.UIUtils;
import core.Entity;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

public class CloseInventoryTest {
  @Mock private Entity mockHero;

  @Mock private InventoryComponent inventoryComponent;

  @Mock private InventoryComponent otherInventoryComponent;

  @Mock private UIComponent uiComponent;

  private AutoCloseable mocks;

  @BeforeEach
  void setUp() {
    mocks = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    mocks.close();
  }

  @Test
  void closeInventory_WithMatchingInventoryInUI_ClosesDialog() {
    when(mockHero.fetch(InventoryComponent.class)).thenReturn(Optional.of(inventoryComponent));
    when(mockHero.fetch(UIComponent.class)).thenReturn(Optional.of(uiComponent));

    try (MockedStatic<UIUtils> uiUtils = mockStatic(UIUtils.class)) {
      uiUtils
          .when(() -> UIUtils.getFirstInventoryFromUI(uiComponent))
          .thenReturn(Optional.of(inventoryComponent));

      HeroController.closeInventory(mockHero);

      uiUtils.verify(() -> UIUtils.closeDialog(uiComponent));
    }
  }

  @Test
  void closeInventory_WithoutInventoryInUI_DoesNothing() {
    when(mockHero.fetch(InventoryComponent.class)).thenReturn(Optional.of(inventoryComponent));
    when(mockHero.fetch(UIComponent.class)).thenReturn(Optional.of(uiComponent));

    try (MockedStatic<UIUtils> uiUtils = mockStatic(UIUtils.class)) {
      uiUtils.when(() -> UIUtils.getFirstInventoryFromUI(uiComponent)).thenReturn(Optional.empty());

      HeroController.closeInventory(mockHero);

      uiUtils.verify(() -> UIUtils.closeDialog(uiComponent), never());
    }
  }

  @Test
  void closeInventory_WithDifferentInventoryInUI_DoesNothing() {
    when(mockHero.fetch(InventoryComponent.class)).thenReturn(Optional.of(inventoryComponent));
    when(mockHero.fetch(UIComponent.class)).thenReturn(Optional.of(uiComponent));

    try (MockedStatic<UIUtils> uiUtils = mockStatic(UIUtils.class)) {
      uiUtils
          .when(() -> UIUtils.getFirstInventoryFromUI(uiComponent))
          .thenReturn(Optional.of(otherInventoryComponent));

      HeroController.closeInventory(mockHero);

      uiUtils.verify(() -> UIUtils.closeDialog(uiComponent), never());
    }
  }

  @Test
  void closeInventory_WithoutInventoryComponent_DoesNotAccessUI() {
    when(mockHero.fetch(InventoryComponent.class)).thenReturn(Optional.empty());

    try (MockedStatic<UIUtils> uiUtils = mockStatic(UIUtils.class)) {
      HeroController.closeInventory(mockHero);

      uiUtils.verifyNoInteractions();
    }
  }

  @Test
  void closeInventory_WithoutUIComponent_DoesNotCloseDialog() {
    when(mockHero.fetch(InventoryComponent.class)).thenReturn(Optional.of(inventoryComponent));
    when(mockHero.fetch(UIComponent.class)).thenReturn(Optional.empty());

    try (MockedStatic<UIUtils> uiUtils = mockStatic(UIUtils.class)) {
      HeroController.closeInventory(mockHero);

      uiUtils.verifyNoInteractions();
    }
  }

  @Test
  void closeInventory_WithNullHero_ThrowsNullPointerException() {
    assertThrows(NullPointerException.class, () -> HeroController.closeInventory(null));
  }
}
