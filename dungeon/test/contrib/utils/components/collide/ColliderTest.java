package contrib.utils.components.collide;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import contrib.components.CollideComponent;
import core.Entity;
import core.utils.Point;
import core.utils.Rectangle;
import core.utils.Vector2;
import java.security.InvalidParameterException;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests for {@link Collider}. */
class ColliderTest {

  @Test
  void centerCoordinatesAreCalculatedCorrectly() {
    Hitbox collider = new Hitbox(8f, 4f, 1f, 2f);

    assertEquals(5f, collider.centerX(), "centerX() should return the horizontal midpoint.");
    assertEquals(4f, collider.centerY(), "centerY() should return the vertical midpoint.");
  }

  @Test
  void centerSetterPositionsColliderByItsCenter() {
    Hitbox collider = new Hitbox(8f, 4f, 1f, 2f);

    collider.center(Vector2.of(10f, 20f));

    assertEquals(
        Vector2.of(10f, 20f),
        collider.center(),
        "center(Vector2) should move the collider center to the requested point.");
    assertEquals(
        6f, collider.left(), "center(Vector2) should update the left edge based on the width.");
    assertEquals(
        18f,
        collider.bottom(),
        "center(Vector2) should update the bottom edge based on the height.");
  }

  @Test
  void sizeAndHalfSizeReturnExpectedDimensions() {
    Hitbox collider = new Hitbox(8f, 4f, 1f, 2f);

    assertEquals(Vector2.of(8f, 4f), collider.size(), "size() should return width and height.");
    assertEquals(
        Vector2.of(4f, 2f),
        collider.halfSize(),
        "halfSize() should return half-width and half-height.");
  }

  @Test
  void centerOriginSetsOffsetToColliderMiddle() {
    Hitbox collider = new Hitbox(8f, 4f, 1f, 2f);

    collider.centerOrigin();

    assertEquals(
        Vector2.of(-4f, -2f),
        collider.offset(),
        "centerOrigin() should move the offset to negative half-width and half-height.");
  }

  @Test
  void absolutePositionRespectsPositionOffsetAndScale() {
    Hitbox collider = new Hitbox(8f, 4f, 1f, 2f);
    collider.position(new Point(10f, 20f));
    collider.scale(Vector2.of(2f, 3f));

    assertEquals(
        new Point(12f, 26f),
        collider.absolutePosition(),
        "absolutePosition() should combine base position, offset, and scale.");
  }

  @Test
  void absoluteCenterIsCalculatedCorrectly() {
    Hitbox collider = new Hitbox(8f, 4f, 1f, 2f);
    collider.position(new Point(10f, 20f));
    collider.scale(Vector2.of(2f, 3f));

    assertEquals(
        new Point(20f, 32f),
        collider.absoluteCenter(),
        "absoluteCenter() should return the midpoint in absolute coordinates.");
  }

  @Test
  void cornersReturnAllLocalCornerPoints() {
    Hitbox collider = new Hitbox(8f, 4f, 1f, 2f);

    assertEquals(
        List.of(Vector2.of(1f, 2f), Vector2.of(9f, 2f), Vector2.of(1f, 6f), Vector2.of(9f, 6f)),
        collider.corners(),
        "corners() should return the four local corner points in the documented order.");
  }

  @Test
  void cornersScaledApplyColliderScale() {
    Hitbox collider = new Hitbox(8f, 4f, 1f, 2f);
    collider.scale(Vector2.of(2f, 3f));

    assertEquals(
        List.of(Vector2.of(2f, 6f), Vector2.of(18f, 6f), Vector2.of(2f, 18f), Vector2.of(18f, 18f)),
        collider.cornersScaled(),
        "cornersScaled() should scale each corner coordinate.");
  }

  @Test
  void absoluteBoundsCombineAbsoluteOffsetAndScaledSize() {
    Hitbox collider = new Hitbox(8f, 4f, 1f, 2f);
    collider.position(new Point(10f, 20f));
    collider.scale(Vector2.of(2f, 3f));

    assertEquals(
        new Rectangle(16f, 12f, 12f, 26f),
        collider.absoluteBounds(),
        "absoluteBounds() should return the absolute rectangle from scaled size and absolute offset.");
  }

  @Test
  void collideWithUnknownColliderTypeThrowsException() {
    Hitbox collider = new Hitbox(8f, 4f, 1f, 2f);

    assertThrows(
        InvalidParameterException.class,
        () -> collider.collide(new UnsupportedCollider()),
        "collide(Collider) should reject unsupported collider types.");
  }

  @Test
  void colliderUsesEntityCollideComponentForCollisionDetection() {
    Entity entity = new Entity();
    entity.add(new CollideComponent(new Rectangle(2f, 2f, 0f, 0f)));
    Hitbox collider = new Hitbox(2f, 2f, 1f, 1f);

    assertTrue(
        collider.collide(entity),
        "A collider should detect collision through the entity's CollideComponent collider.");
  }

  private static final class UnsupportedCollider extends Collider {

    @Override
    public boolean collide(Point point) {
      return false;
    }

    @Override
    public boolean collide(Point from, Point to) {
      return false;
    }

    @Override
    public boolean collide(Hitbox hitbox) {
      return false;
    }

    @Override
    public boolean collide(Hitcircle hitcircle) {
      return false;
    }

    @Override
    public float width() {
      return 0;
    }

    @Override
    public void width(float value) {}

    @Override
    public float height() {
      return 0;
    }

    @Override
    public void height(float value) {}

    @Override
    public float top() {
      return 0;
    }

    @Override
    public void top(float value) {}

    @Override
    public float bottom() {
      return 0;
    }

    @Override
    public void bottom(float value) {}

    @Override
    public float left() {
      return 0;
    }

    @Override
    public void left(float value) {}

    @Override
    public float right() {
      return 0;
    }

    @Override
    public void right(float value) {}
  }
}
