package contrib.utils.components.item;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import contrib.item.Item;
import contrib.item.concreteItem.ItemHammer;
import contrib.item.concreteItem.ItemKey;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Tests for {@link ItemGenerator}. */
public class ItemGeneratorTest {

  @Test
  public void generateItemDataReturnsRegisteredItem() {
    ItemGenerator generator = new ItemGenerator();

    generator.addItem(ItemKey::new, 2.5);

    assertInstanceOf(ItemKey.class, generator.generateItemData());
  }

  @Test
  public void generateItemDataPrefersHigherWeight() {
    ItemGenerator generator = new ItemGenerator();

    generator.addItem(ItemKey::new, 1000.0);
    generator.addItem(ItemHammer::new, 1.0);

    int generatedKeys = 0;
    int generatedHammers = 0;

    for (int i = 0; i < 500; i++) {
      Item generatedItem = generator.generateItemData();
      if (generatedItem instanceof ItemKey) {
        generatedKeys++;
      } else if (generatedItem instanceof ItemHammer) {
        generatedHammers++;
      } else {
        fail("Generated unexpected item type: " + generatedItem.getClass().getSimpleName());
      }
    }

    assertTrue(generatedKeys > generatedHammers);
  }

  @Test
  public void generateItemDataThrowsOnEmptyGenerator() {
    ItemGenerator generator = new ItemGenerator();

    assertThrows(IllegalStateException.class, generator::generateItemData);
  }

  @Test
  public void resetRemovesRegisteredItems() {
    ItemGenerator generator = new ItemGenerator();

    generator.addItem(ItemKey::new, 1.0);
    generator.addItem(ItemHammer::new, 1.0);

    generator.reset();

    assertThrows(IllegalStateException.class, generator::generateItemData);
  }

  @Test
  public void defaultItemGeneratorContainsItems() {
    ItemGenerator generator = ItemGenerator.defaultItemGenerator();

    assertNotNull(generator.generateItemData());
  }

  @Test
  public void defaultItemGeneratorGeneratesDifferentValidItemTypes() {
    ItemGenerator generator = ItemGenerator.defaultItemGenerator();
    Set<Class<? extends Item>> generatedTypes = new HashSet<>();

    for (int i = 0; i < 200; i++) {
      Item generatedItem = generator.generateItemData();
      assertNotNull(generatedItem);
      generatedTypes.add(generatedItem.getClass());
    }

    assertTrue(generatedTypes.size() > 1);
  }
}
