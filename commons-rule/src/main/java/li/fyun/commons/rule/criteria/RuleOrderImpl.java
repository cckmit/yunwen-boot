package li.fyun.commons.rule.criteria;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import li.fyun.commons.core.criteria.Order;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuleOrderImpl implements Order {

    private String field;
    private boolean ascending = true;

}
