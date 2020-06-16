package li.fyun.yunwen.rule.engine;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.support.CompositeRule;

public class PostConditionalRuleGroup extends CompositeRule {

    private Rule conditionalRule;

    public PostConditionalRuleGroup(String name, Rule conditionalRule) {
        super(name);
        this.conditionalRule = conditionalRule;
    }

    public PostConditionalRuleGroup(String name, int priority, Rule conditionalRule) {
        super(name);
        this.priority = priority;
        this.conditionalRule = conditionalRule;
    }

    @Override
    public boolean evaluate(Facts facts) {
        return conditionalRule.evaluate(facts);
    }

    @Override
    public void execute(Facts facts) throws Exception {
        for (Rule rule : rules) {
            if (rule.evaluate(facts)) {
                rule.execute(facts);
            }
        }
        conditionalRule.execute(facts);
    }

}
