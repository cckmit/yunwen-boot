package li.fyun.commons.rule.criteria.rule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import li.fyun.commons.rule.criteria.Criterion;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class RuleCriterionImpl implements Criterion<RuleExpressionImpl> {

    private List<RuleExpressionImpl> and;
    private List<RuleExpressionImpl> or;

}
