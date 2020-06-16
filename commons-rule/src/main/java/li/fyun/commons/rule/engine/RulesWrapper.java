package li.fyun.commons.rule.engine;

import lombok.Data;
import org.jeasy.rules.api.Rules;

@Data
public class RulesWrapper {

    private Rules rules;

    public RulesWrapper(Rules rules) {
        this.rules = rules;
    }

}
