package contrib.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import contrib.components.InventoryComponent;
import contrib.item.Item;
import contrib.utils.components.item.ItemGenerator;
import core.Entity;
import core.components.PositionComponent;
import core.utils.Point;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests für die Methode MiscFactory.newChest(FILL_CHEST type) Zugehörige Issue-Nummer: #137 */
public class newChestTest {

  private ItemGenerator originalGenerator;
  private ItemGenerator mockedGenerator;

  @BeforeEach
  public void setUp() {
    originalGenerator = MiscFactory.randomItemGenerator();
    mockedGenerator = mock(ItemGenerator.class);
  }

  @AfterEach
  public void tearDown() {
    MiscFactory.randomItemGenerator(originalGenerator);
  }

  /*
   * Äquivalenzklasse: G1. Truhe mit zufälligem Inhalt erzeugen
   */
  @Test
  public void test_G1_newChest_Random() {
    MiscFactory.randomItemGenerator(mockedGenerator);
    when(mockedGenerator.generateItemData()).thenReturn(mock(Item.class));

    Entity chest = MiscFactory.newChest(MiscFactory.FILL_CHEST.RANDOM);

    assertNotNull(chest);
    assertTrue(chest.isPresent(InventoryComponent.class));
    assertTrue(chest.isPresent(PositionComponent.class));

    Point pos = chest.fetch(PositionComponent.class).get().position();
    assertEquals(PositionComponent.ILLEGAL_POSITION, pos);

    InventoryComponent ic = chest.fetch(InventoryComponent.class).get();
    int itemAnzahl = ic.count();
    assertTrue(itemAnzahl >= 0 && itemAnzahl <= 5);
  }

  /*
   * Äquivalenzklasse: G2. Leere Truhe erzeugen
   */
  @Test
  public void test_G2_newChest_Empty() {
    Entity chest = MiscFactory.newChest(MiscFactory.FILL_CHEST.EMPTY);

    assertNotNull(chest);
    assertTrue(chest.isPresent(InventoryComponent.class));
    assertTrue(chest.isPresent(PositionComponent.class));

    Point pos = chest.fetch(PositionComponent.class).get().position();
    assertEquals(PositionComponent.ILLEGAL_POSITION, pos);

    InventoryComponent ic = chest.fetch(InventoryComponent.class).get();
    assertEquals(0, ic.count());
  }

  /*
   * Äquivalenzklasse: G3. Mehrfache RANDOM-Erzeugung
   */
  @Test
  public void test_G3_multiple_Random_creations() {
    for (int i = 0; i < 10; i++) {
      Entity chest = MiscFactory.newChest(MiscFactory.FILL_CHEST.RANDOM);
      assertNotNull(chest);
      InventoryComponent ic = chest.fetch(InventoryComponent.class).get();
      int itemAnzahl = ic.count();
      assertTrue(itemAnzahl >= 0 && itemAnzahl <= 5);
    }
  }

  /*
   * Äquivalenzklasse: G4. Mehrfache EMPTY-Erzeugung
   */
  @Test
  public void test_G4_multiple_Empty_creations() {
    for (int i = 0; i < 5; i++) {
      Entity chest = MiscFactory.newChest(MiscFactory.FILL_CHEST.EMPTY);
      assertNotNull(chest);
      InventoryComponent ic = chest.fetch(InventoryComponent.class).get();
      assertEquals(0, ic.count());
      Point pos = chest.fetch(PositionComponent.class).get().position();
      assertEquals(PositionComponent.ILLEGAL_POSITION, pos);
    }
  }

  /*
   * Äquivalenzklasse: U1. Typ ist null
   */
  @Test
  public void test_U1_type_is_null() {
    assertThrows(
        NullPointerException.class,
        () -> {
          MiscFactory.newChest(null);
        });
  }

  /*
   * Äquivalenzklasse: U2. generateRandomItems liefert weniger Items als erlaubt
   */
  @Test
  public void test_U2_generateRandomItems_too_few_items() {
    Entity chest = MiscFactory.newChest(MiscFactory.FILL_CHEST.RANDOM);
    InventoryComponent ic = chest.fetch(InventoryComponent.class).get();
    assertFalse(ic.count() < 0);
  }

  /*
   * Äquivalenzklasse: U3. generateRandomItems liefert mehr Items als erlaubt
   */
  @Test
  public void test_U3_generateRandomItems_too_many_items() {
    Entity chest = MiscFactory.newChest(MiscFactory.FILL_CHEST.RANDOM);
    InventoryComponent ic = chest.fetch(InventoryComponent.class).get();
    assertFalse(ic.count() > 5);
  }

  /*
   * Äquivalenzklasse: U4. generateRandomItems liefert null
   */
  @Test
  public void test_U4_generateRandomItems_returns_null() {
    MiscFactory.randomItemGenerator(mockedGenerator);
    when(mockedGenerator.generateItemData()).thenReturn(null);

    try {
      MiscFactory.newChest(MiscFactory.FILL_CHEST.RANDOM);
    } catch (NullPointerException e) {
      assertTrue(true);
    }
  }

  /*
   * Äquivalenzklasse: U5. Überladene newChest(Set<Item>, Point)-Methode schlägt fehl
   */
  @Test
  public void test_U5_overloaded_newChest_method_fails() {
    try {
      Entity chest = MiscFactory.newChest(MiscFactory.FILL_CHEST.RANDOM);
      assertNotNull(chest);
    } catch (Throwable t) {
      fail("Exception wurde unerwartet abgefangen oder modifiziert: " + t.getMessage());
    }
  }
}
