package contrib.components;
import contrib.utils.components.health.DamageType;
import core.Component;
import core.Entity;

import java.util.function.Consumer;

/** Fake class for test purposes */
public class FakeHealthComponent implements Component, BarDisplayable {


  private int currentHealth;
  private int maxHealth;
  private int damage = 0;
  private boolean dead = false;
  private boolean alreadyDead = false;

  private boolean onDeathCalled = false;

  private Consumer<Entity> deathCallback;


  public FakeHealthComponent(int health) {
    this.maxHealth = health;
    this.currentHealth = health;

    this.deathCallback = e -> {};
  }


  public int currentHealthpoints() {
    return currentHealth;
  }


  public void currentHealthpoints(int amount) {
    this.currentHealth =
      Math.min(maxHealth, amount);

    if(currentHealth <= 0) {
      dead = true;
    }
  }


  public int maximalHealthpoints() {
    return maxHealth;
  }


  public boolean isDead() {
    return dead;
  }


  public boolean alreadyDead() {
    return alreadyDead;
  }


  public void alreadyDead(boolean value) {
    this.alreadyDead = value;
  }


  public void triggerOnDeath(Entity entity) {
    onDeathCalled = true;
    deathCallback.accept(entity);
  }


  public boolean onDeathCalled() {
    return onDeathCalled;
  }


  public void onDeath(Consumer<Entity> callback) {
    this.deathCallback = callback;
  }


  public void receiveDamage(int amount) {
    damage += amount;
  }


  public int calculateDamageOf(DamageType type) {
    return damage;
  }


  public void clearDamage() {
    damage = 0;
  }

  @Override
  public float current() {
    return currentHealth;
  }


  @Override
  public float max() {
    return maxHealth;
  }


  @Override
  public String barStyleName() {
    return "healthbar";
  }


  @Override
  public int barPriority() {
    return 0;
  }
}
