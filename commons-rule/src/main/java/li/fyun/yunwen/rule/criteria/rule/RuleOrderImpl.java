package li.fyun.yunwen.rule.criteria.rule;

import li.fyun.yunwen.rule.criteria.Order;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuleOrderImpl implements Order {

    private String field;
    private boolean ascending = true;

}
