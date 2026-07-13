package contrib.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import contrib.components.CollideComponent;
import contrib.components.InventoryComponent;
import contrib.item.Item;
import contrib.item.concreteItem.ItemHammer;
import contrib.modules.interaction.InteractionComponent;
import core.Entity;
import core.Game;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.utils.Point;
import core.utils.components.path.SimpleIPath;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class NewDestroyableObjectTest {
  private static final SimpleIPath VASE = new SimpleIPath("objects/vase");
  private static final SimpleIPath STONE = new SimpleIPath("objects/stone");

  @Mock private Item mockItem1;

  @Mock private Item mockItem2;

  private AutoCloseable mocks;

  @BeforeEach
  void setup() {
    mocks = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    Game.removeAllEntities();
    Game.removeAllSystems();
    Game.currentLevel(null);
    mocks.close();
  }

  @Test
  void newDestroyableObject_WithoutRequiredItem_HasPositionComponent() {
    // Arrange & Act
    Entity obj = MiscFactory.newDestroyableObject("vase", VASE, new Point(0, 0), null, Set.of());

    // Assert
    assertTrue(obj.isPresent(PositionComponent.class));
  }

  @Test
  void newDestroyableObject_WithoutRequiredItem_HasInventoryComponent() {
    // Arrange & Act
    Entity obj = MiscFactory.newDestroyableObject("vase", VASE, new Point(0, 0), null, Set.of());

    // Assert
    assertTrue(obj.isPresent(InventoryComponent.class));
  }

  @Test
  void newDestroyableObject_WithoutRequiredItem_HasCollideComponent() {
    // Arrange & Act
    Entity obj = MiscFactory.newDestroyableObject("vase", VASE, new Point(0, 0), null, Set.of());

    // Assert
    assertTrue(obj.isPresent(CollideComponent.class));
  }

  @Test
  void newDestroyableObject_WithoutRequiredItem_HasDrawComponent() {
    // Arrange & Act
    Entity obj = MiscFactory.newDestroyableObject("vase", VASE, new Point(0, 0), null, Set.of());

    // Assert
    assertTrue(obj.isPresent(DrawComponent.class));
  }

  @Test
  void newDestroyableObject_WithoutRequiredItem_HasInteractionComponent() {
    // Arrange & Act
    Entity obj = MiscFactory.newDestroyableObject("vase", VASE, new Point(0, 0), null, Set.of());

    // Assert
    assertTrue(obj.isPresent(InteractionComponent.class));
  }

  @Test
  void newDestroyableObject_WhenCreated_StartsInIdleState() {
    // Arrange & Act
    Entity obj = MiscFactory.newDestroyableObject("vase", VASE, new Point(0, 0), null, Set.of());

    // Assert
    assertEquals("idle", obj.fetch(DrawComponent.class).orElseThrow().currentState().name);
  }

  @Test
  void newDestroyableObject_WhenCreated_IsSolid() {
    // Arrange & Act
    Entity obj = MiscFactory.newDestroyableObject("vase", VASE, new Point(0, 0), null, Set.of());

    // Assert
    assertTrue(obj.fetch(CollideComponent.class).orElseThrow().isSolid());
  }

  @Test
  void newDestroyableObject_WithoutRequiredItem_RemovesInteractionComponentOnInteraction() {
    // Arrange
    Entity obj = MiscFactory.newDestroyableObject("vase", VASE, new Point(0, 0), null, Set.of());
    Entity interactor = interactorAt(new Point(0, 0));

    // Act
    obj.fetch(InteractionComponent.class).orElseThrow().triggerInteraction(obj, interactor);

    // Assert
    assertFalse(obj.isPresent(InteractionComponent.class));
  }

  @Test
  void newDestroyableObject_WithoutRequiredItemPresent_RemovesInteractionComponent() {
    // Arrange
    Entity stone =
        MiscFactory.newDestroyableObject(
            "stone", STONE, new Point(0, 0), ItemHammer.class, Set.of());
    Entity hero = heroWithHammer();

    // Act
    stone.fetch(InteractionComponent.class).orElseThrow().triggerInteraction(stone, hero);

    // Assert
    assertFalse(stone.isPresent(InteractionComponent.class));
  }

  @Test
  void newDestroyableObject_WithRequiredItemPresent_DisablesCollision() {
    // Arrange
    Entity stone =
        MiscFactory.newDestroyableObject(
            "stone", STONE, new Point(0, 0), ItemHammer.class, Set.of());
    Entity hero = heroWithHammer();

    // Act
    stone.fetch(InteractionComponent.class).orElseThrow().triggerInteraction(stone, hero);

    // Assert
    assertFalse(stone.fetch(CollideComponent.class).orElseThrow().isSolid());
  }

  @Test
  void newDestroyableObject_WithRequiredItemPresent_PlaysBreakingAnimation() {
    // Arrange
    Entity stone =
        MiscFactory.newDestroyableObject(
            "stone", STONE, new Point(0, 0), ItemHammer.class, Set.of());
    Entity hero = heroWithHammer();

    // Act
    stone.fetch(InteractionComponent.class).orElseThrow().triggerInteraction(stone, hero);

    // Assert
    assertEquals("breaking", stone.fetch(DrawComponent.class).orElseThrow().currentState().name);
  }

  @Test
  void newDestroyableObject_WithRequiredItemMissing_KeepsInteractionComponent() {
    // Arrange
    Entity stone =
        MiscFactory.newDestroyableObject(
            "stone", STONE, new Point(0, 0), ItemHammer.class, Set.of());
    Entity hero = heroWithEmptyInventory();

    // Act
    stone.fetch(InteractionComponent.class).orElseThrow().triggerInteraction(stone, hero);

    // Assert
    assertTrue(stone.isPresent(InteractionComponent.class));
  }

  @Test
  void newDestroyableObject_WithRequiredItemMissing_KeepsCollisionSolid() {
    // Arrange
    Entity stone =
        MiscFactory.newDestroyableObject(
            "stone", STONE, new Point(0, 0), ItemHammer.class, Set.of());
    Entity hero = heroWithEmptyInventory();

    // Act
    stone.fetch(InteractionComponent.class).orElseThrow().triggerInteraction(stone, hero);

    // Assert
    assertTrue(stone.fetch(CollideComponent.class).orElseThrow().isSolid());
  }

  @Test
  void newDestroyableObject_WithRequiredItemMissing_StaysInIdleState() {
    // Arrange
    Entity stone =
        MiscFactory.newDestroyableObject(
            "stone", STONE, new Point(0, 0), ItemHammer.class, Set.of());
    Entity hero = heroWithEmptyInventory();

    // Act
    stone.fetch(InteractionComponent.class).orElseThrow().triggerInteraction(stone, hero);

    // Assert
    assertEquals("idle", stone.fetch(DrawComponent.class).orElseThrow().currentState().name);
  }

  @Test
  void newDestroyableObject_WithEmptyInventory_RemovesInteractionComponent() {
    // Arrange
    Entity vase = MiscFactory.newDestroyableObject("vase", VASE, new Point(0, 0), null, Set.of());
    Entity interactor = interactorAt(new Point(0, 0));
    // Act
    vase.fetch(InteractionComponent.class).orElseThrow().triggerInteraction(vase, interactor);

    // Assert
    assertFalse(vase.isPresent(InteractionComponent.class));
  }

  @Test
  void newDestroyableObject_WithEmptyInventory_KeepsInventoryEmpty() {
    // Arrange
    Entity vase = MiscFactory.newDestroyableObject("vase", VASE, new Point(0, 0), null, Set.of());
    InventoryComponent inv = vase.fetch(InventoryComponent.class).orElseThrow();
    Entity interactor = interactorAt(new Point(0, 0));

    // Act
    vase.fetch(InteractionComponent.class).orElseThrow().triggerInteraction(vase, interactor);

    // Assert
    assertEquals(0, inv.count());
  }

  @Test
  void newDestroyableObject_WithMultipleItems_DropsAllContainedItems() {
    // Arrange
    Point spawn = new Point(0, 0);
    Entity vase = MiscFactory.newDestroyableObject("vase", VASE, spawn, null, twoMockItem());
    Entity interactor = interactorAt(new Point(0, 0));

    // Act
    vase.fetch(InteractionComponent.class).orElseThrow().triggerInteraction(vase, interactor);

    // Assert
    verify(mockItem1).drop(spawn);
    verify(mockItem2).drop(spawn);
  }

  @Test
  void newDestroyableObject_WithMultipleItems_RemovesAllContainedItemsFromInventory() {
    // Arrange
    Entity vase =
        MiscFactory.newDestroyableObject("vase", VASE, new Point(0, 0), null, twoMockItem());
    InventoryComponent inv = vase.fetch(InventoryComponent.class).orElseThrow();
    Entity interactor = interactorAt(new Point(0, 0));

    // Act
    vase.fetch(InteractionComponent.class).orElseThrow().triggerInteraction(vase, interactor);

    // Assert
    assertEquals(0, inv.count());
  }

  @Test
  void newDestroyableObject_OnInteraction_TransitionsToBreakingState() {
    // Arrange
    Entity vase = MiscFactory.newDestroyableObject("vase", VASE, new Point(0, 0), null, Set.of());
    Entity interactor = interactorAt(new Point(0, 0));

    // Act
    vase.fetch(InteractionComponent.class).orElseThrow().triggerInteraction(vase, interactor);

    // Assert
    assertEquals("breaking", vase.fetch(DrawComponent.class).orElseThrow().currentState().name);
  }

  @Test
  void newDestroyableObject_OnInteraction_DisablesCollision() {
    // Arrange
    Entity vase = MiscFactory.newDestroyableObject("vase", VASE, new Point(0, 0), null, Set.of());
    Entity interactor = interactorAt(new Point(0, 0));

    // Act
    vase.fetch(InteractionComponent.class).orElseThrow().triggerInteraction(vase, interactor);

    // Assert
    assertFalse(vase.fetch(CollideComponent.class).orElseThrow().isSolid());
  }

  @Test
  void newDestroyableObject_AfterDestruction_HasNoInteractionComponent() {
    // Arrange
    Entity vase = MiscFactory.newDestroyableObject("vase", VASE, new Point(0, 0), null, Set.of());
    Entity interactor = interactorAt(new Point(0, 0));

    // Act
    vase.fetch(InteractionComponent.class).orElseThrow().triggerInteraction(vase, interactor);

    // Assert
    assertTrue(vase.fetch(InteractionComponent.class).isEmpty());
  }

  // ------

  @Test
  void newDestroyableObject_WithNullName_DoesNotThrow() {
    // Act & Assert
    assertDoesNotThrow(
        () -> MiscFactory.newDestroyableObject(null, VASE, new Point(0, 0), null, Set.of()));
  }

  @Test
  void newDestroyableObject_WithNullName_CreateEntity() {
    // Arrrange & Act
    Entity obj = MiscFactory.newDestroyableObject(null, VASE, new Point(0, 0), null, Set.of());

    // Assert
    assertNotNull(obj);
  }

  @Test
  void newDestroyableObject_WithNullTexturePath_ThrowsNullPointerException() {
    // Act & Assert
    assertThrows(
        NullPointerException.class,
        () -> MiscFactory.newDestroyableObject("x", null, new Point(0, 0), null, Set.of()));
  }

  @Test
  void newDestroyableObject_WithNullSpawnPoint_PositionThrowsNullPointerException() {
    // Arrange
    Entity obj = MiscFactory.newDestroyableObject("x", VASE, null, null, Set.of());
    PositionComponent positionComponent = obj.fetch(PositionComponent.class).orElseThrow();

    // Act & Assert
    assertThrows(NullPointerException.class, positionComponent::position);
  }

  @Test
  void newDestroyableObject_WithNullItems_ThrowsNullPointerException() {
    // Act & Assert
    assertThrows(
        NullPointerException.class,
        () -> MiscFactory.newDestroyableObject("x", VASE, new Point(0, 0), null, null));
  }

  @Test
  void newDestroyableObject_WithMissingAnimationState_ThrowsIllegalArgumentException() {
    // Act & Assert
    assertThrows(
        IllegalArgumentException.class,
        () ->
            MiscFactory.newDestroyableObject(
                "x", new SimpleIPath("character/knight"), new Point(0, 0), null, Set.of()));
  }

  @Test
  void newDestroyableObject_WithInteractorWithoutInventory_KeepsInteractionComponent() {
    // Arrange
    Entity stone =
        MiscFactory.newDestroyableObject(
            "stone", STONE, new Point(0, 0), ItemHammer.class, Set.of());
    Entity interactor = interactorAt(new Point(0, 0));

    // Act
    stone.fetch(InteractionComponent.class).orElseThrow().triggerInteraction(stone, interactor);

    // Assert
    assertTrue(stone.isPresent(InteractionComponent.class));
  }

  @Test
  void newDestroyableObject_WithInteractorWithoutInventory_StaysInIdleState() {
    // Arrange
    Entity stone =
        MiscFactory.newDestroyableObject(
            "stone", STONE, new Point(0, 0), ItemHammer.class, Set.of());
    Entity interactor = interactorAt(new Point(0, 0));

    // Act
    stone.fetch(InteractionComponent.class).orElseThrow().triggerInteraction(stone, interactor);

    // Assert
    assertEquals("idle", stone.fetch(DrawComponent.class).orElseThrow().currentState().name);
  }

  @Test
  void newDestroyableObject_WithoutDrawSystem_DoesNotThrow() {
    // Arrange
    Entity vase = MiscFactory.newDestroyableObject("vase", VASE, new Point(0, 0), null, Set.of());
    InteractionComponent interactionComponent =
        vase.fetch(InteractionComponent.class).orElseThrow();
    Entity interactor = interactorAt(new Point(0, 0));

    // Act & Assert
    assertDoesNotThrow(() -> interactionComponent.triggerInteraction(vase, interactor));
  }

  // ------------------------------------
  private static Entity heroWithHammer() {
    Entity hero = interactorAt(new Point(0, 0));
    InventoryComponent inv = new InventoryComponent();
    inv.add(new ItemHammer());
    hero.add(inv);
    return hero;
  }

  private static Entity heroWithEmptyInventory() {
    Entity hero = interactorAt(new Point(0, 0));
    hero.add(new InventoryComponent(1));
    return hero;
  }

  private Set<Item> twoMockItem() {
    when(mockItem1.stackSize()).thenReturn(1);
    when(mockItem1.maxStackSize()).thenReturn(1);
    when(mockItem2.stackSize()).thenReturn(1);
    when(mockItem2.maxStackSize()).thenReturn(1);
    Set<Item> items = new HashSet<>();
    items.add(mockItem1);
    items.add(mockItem2);
    return items;
  }

  private static Entity interactorAt(Point point) {
    Entity entity = new Entity();
    entity.add(new PositionComponent(point));
    return entity;
  }
}
