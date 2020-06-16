package li.fyun.yunwen.rule.criteria;

import li.fyun.yunwen.rule.engine.IParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.List;

public interface Criterion<T extends Expression> extends Serializable {

    String CONJUNCTION_AND = " && ";
    String CONJUNCTION_OR = " || ";

    List<T> getAnd();

    void setAnd(List<T> expressions);

    List<T> getOr();

    void setOr(List<T> expressions);

    boolean validate();

    default boolean validate(String field) {
        int clause = StringUtils.isNotBlank(field) ? 1 : 0;
        clause += CollectionUtils.isNotEmpty(this.getAnd()) ? 1 : 0;
        clause += CollectionUtils.isNotEmpty(this.getOr()) ? 1 : 0;
        if (clause > 1) {
            return false;
        }
        if (CollectionUtils.isNotEmpty(this.getAnd())) {
            for (Expression e : this.getAnd()) {
                if (!e.validate()) {
                    return false;
                }
            }
        }
        if (CollectionUtils.isNotEmpty(this.getOr())) {
            for (Expression e : this.getOr()) {
                if (!e.validate()) {
                    return false;
                }
            }
        }
        return true;
    }

    default <E extends IParam> String build(List<E> ruleParams) {
        Assert.isTrue(this.validate(), "查询条件无效");
        String expr = this.build(this.getAnd(), ruleParams, true);
        expr += this.build(this.getOr(), ruleParams, false);
        return expr;
    }

    @SuppressWarnings("unchecked")
    default <E extends IParam> String build(List<T> expressions, List<E> ruleParams, boolean and) {
        if (org.springframework.util.CollectionUtils.isEmpty(expressions)) {
            return "";
        }

        String expr = "(";
        boolean starts = true;
        for (T exp : expressions) {
            if (!starts) {
                expr += and ? CONJUNCTION_AND : CONJUNCTION_OR;
            }
            starts = false;

            if (StringUtils.isNotBlank(exp.getField())) {
                expr += exp.build(ruleParams);
            }

            final List<T> subAnd = exp.getAnd();
            expr += this.build(subAnd, ruleParams, true);

            final List<T> subOr = exp.getOr();
            expr += this.build(subOr, ruleParams, false);
        }
        expr += ")";
        return expr;
    }

}
