package li.fyun.commons.rule.engine;

import java.util.List;
import java.util.Map;

public interface IRule {

    RuleWrapper asRule();

    default RuleWrapper asRule(int priority) {
        return RuleHelper.asRule(this, priority);
    }

    Long getId();

    String getCode();

    String getName();

    String getDescription();

    int getPriority();

    String getConditionExpression();

    String getActionExpression();

    <E extends IRule> List<E> getChildren();

    Map<String, Object> getAttachFacts();

}
