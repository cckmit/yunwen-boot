package li.fyun.yunwen.rule.criteria.rule;

import li.fyun.yunwen.rule.criteria.Criterion;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class RuleCriterionImpl implements Criterion<RuleExpressionImpl> {

    private List<RuleExpressionImpl> and;
    private List<RuleExpressionImpl> or;

}
