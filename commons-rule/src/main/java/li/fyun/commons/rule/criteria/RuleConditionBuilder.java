package li.fyun.commons.rule.criteria;

import li.fyun.commons.core.criteria.Criterion;
import li.fyun.commons.rule.engine.IParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.List;

public class RuleConditionBuilder {

    public static final RuleConditionBuilder instance = new RuleConditionBuilder();

    private RuleConditionBuilder() {
    }

    public static RuleConditionBuilder getInstance() {
        return instance;
    }

    public <E extends IParam> String build(RuleCriterionImpl criterion, List<E> ruleParams) {
        Assert.isTrue(criterion.validate(), "查询条件无效");
        String expr = this.build(criterion.getAnd(), ruleParams, true);
        expr += this.build(criterion.getOr(), ruleParams, false);
        return expr;
    }

    @SuppressWarnings("unchecked")
    public <E extends IParam> String build(List<RuleExpressionImpl> expressions, List<E> ruleParams, boolean and) {
        if (org.springframework.util.CollectionUtils.isEmpty(expressions)) {
            return "";
        }

        String expr = "(";
        boolean starts = true;
        for (RuleExpressionImpl exp : expressions) {
            if (!starts) {
                expr += and ? Criterion.CONJUNCTION_AND : Criterion.CONJUNCTION_OR;
            }
            starts = false;

            if (StringUtils.isNotBlank(exp.getField())) {
                expr += exp.build(ruleParams);
            }

            final List<RuleExpressionImpl> subAnd = exp.getAnd();
            expr += this.build(subAnd, ruleParams, true);

            final List<RuleExpressionImpl> subOr = exp.getOr();
            expr += this.build(subOr, ruleParams, false);
        }
        expr += ")";
        return expr;
    }

}
