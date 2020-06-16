package li.fyun.commons.rule.criteria.rule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import li.fyun.commons.rule.RuleException;
import li.fyun.commons.rule.criteria.Condition;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RuleConditionImpl extends RuleCriterionImpl implements Condition<RuleExpressionImpl, RuleOrderImpl> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private List<RuleOrderImpl> orders;

    public static RuleConditionImpl fromJson(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, RuleConditionImpl.class);
        } catch (JsonProcessingException ex) {
            throw new RuleException(ex.getMessage());
        }
    }

}
