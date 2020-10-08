package li.fyun.commons.core.criteria.plain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import li.fyun.commons.core.criteria.Condition;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlainConditionImpl extends PlainCriterionImpl implements Condition<PlainExpressionImpl, PlainOrderImpl> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private List<PlainOrderImpl> orders;

}
