package li.fyun.yunwen.rule.engine;

import java.util.List;

public interface IRule {

    RuleWrapper asRule();

    default RuleWrapper asRule(int priority) {
       return RuleHelper.asRule(this, priority);
    }

    String getName();

    String getDescription();

    int getPriority();

    String getConditionExpression();

    String getActionExpression();

    <E extends IRule> List<E> getChildren();

}
