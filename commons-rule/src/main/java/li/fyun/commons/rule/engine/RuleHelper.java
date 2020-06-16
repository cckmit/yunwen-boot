package li.fyun.commons.rule.engine;

import li.fyun.commons.rule.enums.RuleItemRelation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.RulesEngineParameters;
import org.jeasy.rules.mvel.MVELRule;
import org.jeasy.rules.support.CompositeRule;
import org.mvel2.MVEL;

import java.util.List;
import java.util.Map;

@Slf4j
public final class RuleHelper {

    public static Map<String, Object> fire(RulesWrapper rulesWrapper,
                                           Map<String, Object> params,
                                           RuleItemRelation ruleItemRelation) {
        if (rulesWrapper == null || rulesWrapper.getRules() == null) {
            return null;
        }

        RulesEngine rulesEngine = createRuleEngine(ruleItemRelation);
        Facts facts = createFacts(params);
        rulesEngine.fire(rulesWrapper.getRules(), facts);
        return facts.asMap();
    }

    public static Facts createFacts(Map<String, Object> values) {
        Facts facts = new Facts();
        values.forEach((k, v) -> {
            facts.put(k, v);
        });
        return facts;
    }

    protected static RulesEngine createRuleEngine(RuleItemRelation ruleItemRelation) {
        RulesEngineParameters parameters = new RulesEngineParameters();
        if (RuleItemRelation.AND.equals(ruleItemRelation)) {
            parameters.skipOnFirstAppliedRule(false);
        } else {
            parameters.skipOnFirstAppliedRule(true);
        }
        return new DefaultRulesEngine(parameters);
    }

    public static <E extends IRule> RulesWrapper getRules(List<E> ruleItems) {
        if (CollectionUtils.isEmpty(ruleItems)) {
            return null;
        }

        Rules rules = new Rules();
        ruleItems.forEach(i -> rules.register(i.asRule().getRule()));
        log.info("rules: {}", rules);
        return new RulesWrapper(rules);
    }

    public static RuleWrapper asRule(IRule aRule, int priority) {
        Rule rule = new MVELRule()
                .name(aRule.getName())
                .description(aRule.getDescription())
                .priority(priority)
                .when(aRule.getConditionExpression())
                .then(aRule.getActionExpression());

        List<IRule> children = aRule.getChildren();
        if (CollectionUtils.isNotEmpty(aRule.getChildren())) {
            CompositeRule unitRuleGroup = new PostConditionalRuleGroup(
                    RandomStringUtils.randomAlphabetic(6), aRule.getPriority(), rule);
            children.forEach(c -> {
                RuleWrapper subRule = c.asRule(c.getPriority() + aRule.getPriority());
                unitRuleGroup.addRule(subRule.getRule());
            });
            return new RuleWrapper(unitRuleGroup);
        } else {
            return new RuleWrapper(rule);
        }
    }

    public static boolean testCondition(String condition, Map<String, Object> params) {
        return (Boolean) MVEL.eval(condition, params);
    }

    public static Object testFormula(String formula, Map<String, Object> params) {
        return MVEL.eval(formula, params);
    }

}
