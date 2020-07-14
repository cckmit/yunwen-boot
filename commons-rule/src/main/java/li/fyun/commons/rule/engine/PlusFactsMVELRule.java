package li.fyun.commons.rule.engine;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.mvel.MVELRule;

import java.util.Map;

@Slf4j
@Setter
@Accessors(chain = true)
public class PlusFactsMVELRule extends MVELRule {

    private Map<String, Object> plusFacts;

    @Override
    public boolean evaluate(Facts facts) {
        if (MapUtils.isNotEmpty(plusFacts)) {
            plusFacts.forEach((k, v) -> {
                facts.put(k, v);
                log.debug("rule {}: attach fact {} value {}", name, k, v);
            });
        }
        return super.evaluate(facts);
    }

    @Override
    public void execute(Facts facts) throws Exception {
        super.execute(facts);
    }
}
