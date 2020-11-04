package li.fyun.commons.rule.criteria;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import li.fyun.commons.rule.RuleException;
import li.fyun.commons.core.criteria.Condition;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuleConditionImpl extends RuleCriterionImpl implements Condition<RuleExpressionImpl, RuleOrderImpl> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private List<RuleOrderImpl> orders;
    private List<String> groups;

    public static RuleConditionImpl fromJson(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, RuleConditionImpl.class);
        } catch (JsonProcessingException ex) {
            throw new RuleException(ex.getMessage());
        }
    }

}
