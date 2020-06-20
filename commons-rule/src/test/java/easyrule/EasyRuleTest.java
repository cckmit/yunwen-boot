package easyrule;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.mvel.MVELRule;
import org.junit.Test;

public class EasyRuleTest {

    @Test
    public void testMVel() {
        Rule ageRule = new MVELRule()
                .name("age rule")
                .description("test rule")
                .priority(1)
                .when("age > 18")
                .then("result = 'OK'");

        Facts facts = new Facts();
        facts.put("age", 19);
        facts.put("grade", "V1");

        // create rules
        Rules rules = new Rules();
        rules.register(ageRule);

        RulesEngine rulesEngine = new DefaultRulesEngine();
        rulesEngine.fire(rules, facts);

        System.out.println(facts.get("result").toString());
    }

}
