package easyrule;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.mvel.MVELRule;
import org.junit.Test;
import org.mvel2.MVEL;

import java.util.HashMap;
import java.util.Map;

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

    public static void main(String[] args) {
        Map<String, Object> params = new HashMap<>();
        params.put("x", 2);
        MVEL.eval("fee1 = (if (x == 3) 13*x; else if (x == 2) 12*x; else 10*x;)", params);
        System.out.println(params);
    }

}
