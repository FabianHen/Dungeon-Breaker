package contrib.utils.components.skill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import contrib.components.ManaComponent;
import core.Entity;
import core.utils.Tuple;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Tests for {@link Skill}. */
class SkillTest {

  @Test
  void nameReturnsConfiguredSkillName() {
    TestSkill skill = new TestSkill("Arcane Burst", 250L);

    assertEquals("Arcane Burst", skill.name(), "name() should return the configured skill name.");
  }

  @Test
  void cooldownSetterAndGetterUseConfiguredValue() {
    TestSkill skill = new TestSkill("Arcane Burst", 250L);

    skill.cooldown(900L);

    assertEquals(
        900L, skill.cooldown(), "cooldown() should return the value set by cooldown(long).");
  }

  @Test
  void resourceCostReturnsCopyOfInternalMap() {
    TestSkill skill = new TestSkill("Arcane Burst", 250L, Tuple.of(Resource.MANA, 4));
    Map<Resource, Integer> costCopy = skill.resourceCost();

    costCopy.put(Resource.STAMINA, 2);

    assertEquals(1, skill.resourceCost().size(), "resourceCost() should return a defensive copy.");
    assertFalse(
        skill.resourceCost().containsKey(Resource.STAMINA),
        "Changing the returned map must not change the skill's internal resource cost.");
  }

  @Test
  void nullResourceCostCreatesEmptyMap() {
    TestSkill skill = new TestSkill("Arcane Burst", 250L, Tuple.of(Resource.MANA, 4));

    skill.resourceCost(null);

    assertTrue(
        skill.resourceCost().isEmpty(), "resourceCost(null) should reset the cost map to empty.");
  }

  @Test
  void addResourceAddsNewEntry() {
    TestSkill skill = new TestSkill("Arcane Burst", 250L);

    skill.addResource(Resource.MANA, 6);

    assertEquals(
        6, skill.resourceCost().get(Resource.MANA), "addResource() should store the given amount.");
  }

  @Test
  void updateResourceCostUpdatesExistingEntry() {
    TestSkill skill = new TestSkill("Arcane Burst", 250L, Tuple.of(Resource.MANA, 4));

    skill.updateResourceCost(Resource.MANA, 8);

    assertEquals(
        8,
        skill.resourceCost().get(Resource.MANA),
        "updateResourceCost() should update an existing resource amount.");
  }

  @Test
  void removeResourceRemovesEntry() {
    TestSkill skill = new TestSkill("Arcane Burst", 250L, Tuple.of(Resource.MANA, 4));

    skill.removeResource(Resource.MANA);

    assertFalse(
        skill.resourceCost().containsKey(Resource.MANA),
        "removeResource() should remove the configured resource.");
  }

  @Test
  void executeConsumesResourcesAndStartsCooldown() {
    TestSkill skill = new TestSkill("Arcane Burst", 1_000L, Tuple.of(Resource.MANA, 5));
    Entity caster = entityWithMana(20f);

    boolean executed = skill.execute(caster);

    assertTrue(
        executed, "execute() should succeed when cooldown passed and resources are available.");
    assertEquals(
        15f,
        mana(caster).currentAmount(),
        "Successful execution should consume the configured resource cost.");
    assertFalse(skill.canBeUsedAgain(), "Successful execution should activate the cooldown.");
  }

  @Test
  void executeSucceedsWithoutResourceCost() {
    TestSkill skill = new TestSkill("Arcane Burst", 0L);
    Entity caster = new Entity();

    boolean executed = skill.execute(caster);

    assertTrue(executed, "execute() should succeed when no resource cost is configured.");
    assertEquals(
        1, skill.executions, "executeSkill() should be called for a successful no-cost execution.");
  }

  @Test
  void executeReturnsFalseWhileCooldownIsActive() {
    TestSkill skill = new TestSkill("Arcane Burst", 1_000L, Tuple.of(Resource.MANA, 5));
    Entity caster = entityWithMana(20f);

    skill.setLastUsedToNow();
    boolean executed = skill.execute(caster);

    assertFalse(executed, "execute() should return false while the skill is still on cooldown.");
    assertEquals(
        20f,
        mana(caster).currentAmount(),
        "Resources must not be consumed while the skill is on cooldown.");
    assertEquals(
        0, skill.executions, "executeSkill() must not run while the skill is on cooldown.");
  }

  @Test
  void executeReturnsFalseWhenResourcesAreInsufficient() {
    TestSkill skill = new TestSkill("Arcane Burst", 1_000L, Tuple.of(Resource.MANA, 5));
    Entity caster = entityWithMana(3f);

    boolean executed = skill.execute(caster);

    assertFalse(
        executed, "execute() should return false when the entity lacks required resources.");
    assertEquals(
        3f, mana(caster).currentAmount(), "Resources must stay unchanged when execution fails.");
    assertEquals(
        0, skill.executions, "executeSkill() must not run when resources are insufficient.");
  }

  @Test
  void componentInteractionUsesEntityResourcesAndCooldownCorrectly() {
    TestSkill skill = new TestSkill("Arcane Burst", 1_000L, Tuple.of(Resource.MANA, 5));
    Entity caster = entityWithMana(20f);

    boolean firstExecution = skill.execute(caster);
    boolean secondExecution = skill.execute(caster);

    assertTrue(firstExecution, "The concrete skill should execute successfully the first time.");
    assertFalse(
        secondExecution, "The same skill should not execute again while cooldown is active.");
    assertEquals(
        15f,
        mana(caster).currentAmount(),
        "Mana should be consumed exactly once across both executions.");
    assertSame(
        caster, skill.lastCaster, "executeSkill() should receive the entity that cast the skill.");
  }

  private static Entity entityWithMana(float currentMana) {
    Entity entity = new Entity();
    entity.add(new ManaComponent(20f, currentMana, 0f));
    return entity;
  }

  private static ManaComponent mana(Entity entity) {
    return entity.fetch(ManaComponent.class).orElseThrow();
  }

  private static final class TestSkill extends Skill {

    private int executions;
    private Entity lastCaster;

    @SafeVarargs
    private TestSkill(String name, long cooldown, Tuple<Resource, Integer>... resources) {
      super(name, cooldown, resources);
    }

    @Override
    protected void executeSkill(Entity caster) {
      executions++;
      lastCaster = caster;
    }
  }
}
