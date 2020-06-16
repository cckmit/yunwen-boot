package li.fyun.commons.rule.criteria.rule;

import li.fyun.commons.rule.RuleException;
import li.fyun.commons.rule.criteria.Expression;
import li.fyun.commons.rule.engine.IParam;
import li.fyun.commons.rule.enums.RuleComparator;
import li.fyun.commons.rule.enums.RuleValueType;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RuleExpressionImpl extends RuleCriterionImpl implements Expression<RuleExpressionImpl, RuleComparator> {

    private String field;
    @Enumerated(EnumType.STRING)
    private RuleComparator op;
    private Object value;

    // 普通条件, AND/OR 不可同时存在
    @Override
    public boolean validate() {
        if (!this.validate(field)) {
            return false;
        }

        if (StringUtils.isNotBlank(field)) {
            if (op == null) {
                return false;
            }

            if (RuleComparator.isNull.equals(op) || RuleComparator.notNull.equals(op)) {
                return true;
            }

            if (value == null) {
                return false;
            }

            if (value instanceof String && StringUtils.isBlank((String) value)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public <E extends IParam> String build(List<E> ruleParams) {
        Assert.isTrue(this.validate(), "查询条件无效");

        String expr = "";
        if (StringUtils.isBlank(field)) {
            return expr;
        }

        E param = findParam(ruleParams);
        RuleValueType valueType = param.getValueType();
        switch (op) {
            case eq:
                return field + " == " + wrapValueToProperType(value, valueType);
            case ne:
                return field + " != " + wrapValueToProperType(value, valueType);
            case gt:
                return field + " > " + wrapValueToProperType(value, valueType);
            case lt:
                return field + " < " + wrapValueToProperType(value, valueType);
            case ge:
                return field + " >= " + wrapValueToProperType(value, valueType);
            case le:
                return field + " <= " + wrapValueToProperType(value, valueType);
            case between:
                if (!(value instanceof List) || ((List) value).size() < 2) {
                    throw new RuleException("between条件应提供两个值");
                }
                List valueList = (List) value;
                expr = "(";
                expr += field + " >= " + wrapValueToProperType(valueList.get(0), valueType);
                expr += CONJUNCTION_AND;
                expr += field + " <= " + wrapValueToProperType(valueList.get(1), valueType);
                expr += ")";
                return expr;
            case in:
                if (!(value instanceof List) || CollectionUtils.isEmpty((List) value)) {
                    throw new RuleException("in条件应提供非空集合数值");
                }
                return populate(valueType, "==", CONJUNCTION_OR);
            case notIn:
                if (!(value instanceof List) || CollectionUtils.isEmpty((List) value)) {
                    throw new RuleException("notIn条件应提供非空集合数值");
                }
                return populate(valueType, "!=", CONJUNCTION_AND);
            case belong:
            case startsWith:
                return populate(valueType, ".startsWith", CONJUNCTION_OR);
            case endsWith:
                return populate(valueType, ".endsWith", CONJUNCTION_OR);
            case contains:
                return populate(valueType, ".contains", CONJUNCTION_OR);
            case isNull:
                return field + " == null";
            case notNull:
                return field + " != null";
            default:
                return expr;
        }
    }

    private String populate(RuleValueType valueType, String comparor, String conjunction) {
        List valueList;
        String expr;
        boolean starts;
        if (value instanceof List && CollectionUtils.isNotEmpty((List) value)) {
            valueList = (List) value;
            expr = "(";
            starts = true;
            for (Object val : valueList) {
                if (!starts) {
                    expr += conjunction;
                }
                starts = false;
                expr += field + comparor + "(" + wrapValueToProperType(val.toString(), valueType) + ")";
            }
            expr += ")";
            return expr;
        } else {
            return field + comparor + "(" + wrapValueToProperType(value.toString(), valueType) + ")";
        }
    }

    private Object wrapValueToProperType(Object value, RuleValueType valueType) {
        if (RuleValueType.NUMERIC.equals(valueType)) {
            return value;
        } else {
            return "'" + value + "'";
        }
    }

    public <E extends IParam> E findParam(List<E> ruleParams) {
        if (CollectionUtils.isNotEmpty(ruleParams)) {
            for (E param : ruleParams) {
                if (StringUtils.equals(field, param.getCode())) {
                    return param;
                }
            }
        }
        throw new RuleException("查询条件无效");
    }

}
