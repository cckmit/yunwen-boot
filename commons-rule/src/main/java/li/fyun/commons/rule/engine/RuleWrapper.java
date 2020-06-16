package li.fyun.commons.rule.engine;

import lombok.Data;
import org.jeasy.rules.api.Rule;

@Data
public class RuleWrapper {

    private Rule rule;

    public RuleWrapper(Rule rule) {
        this.rule = rule;
    }

}
