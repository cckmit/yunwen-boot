package li.fyun.commons.rule.criteria.rule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import li.fyun.commons.rule.RuleException;
import li.fyun.commons.rule.criteria.Expression;
import li.fyun.commons.rule.engine.IParam;
import li.fyun.commons.rule.enums.RuleComparator;
import li.fyun.commons.rule.enums.RuleValueType;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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
        Object compareVal = getCompareValue();
        switch (op) {
            case eq:
                return field + " == " + wrapValueToProperType(compareVal, valueType);
            case ne:
                return field + " != " + wrapValueToProperType(compareVal, valueType);
            case gt:
                return field + " > " + wrapValueToProperType(compareVal, valueType);
            case lt:
                return field + " < " + wrapValueToProperType(compareVal, valueType);
            case ge:
                return field + " >= " + wrapValueToProperType(compareVal, valueType);
            case le:
                return field + " <= " + wrapValueToProperType(compareVal, valueType);
            case between:
                if (!(compareVal instanceof List) || ((List) compareVal).size() < 2) {
                    throw new RuleException("between条件应提供两个值");
                }
                List valueList = (List) compareVal;
                expr = "(";
                expr += field + " >= " + wrapValueToProperType(valueList.get(0), valueType);
                expr += CONJUNCTION_AND;
                expr += field + " <= " + wrapValueToProperType(valueList.get(1), valueType);
                expr += ")";
                return expr;
            case in:
                if (!(compareVal instanceof List) || CollectionUtils.isEmpty((List) compareVal)) {
                    throw new RuleException("in条件应提供非空集合数值");
                }
                return populate(valueType, " == ", compareVal, CONJUNCTION_OR);
            case notIn:
                if (!(compareVal instanceof List) || CollectionUtils.isEmpty((List) compareVal)) {
                    throw new RuleException("notIn条件应提供非空集合数值");
                }
                return populate(valueType, " != ", compareVal, CONJUNCTION_AND);
            case belong:
            case startsWith:
                return populate(valueType, ".startsWith", compareVal, CONJUNCTION_OR);
            case notBelong:
                return populate(valueType, ".startsWith", compareVal, CONJUNCTION_AND, "!");
            case endsWith:
                return populate(valueType, ".endsWith", compareVal, CONJUNCTION_OR);
            case includes:
                return populate(valueType, ".contains", compareVal, CONJUNCTION_OR);
            case excludes:
                return populate(valueType, ".contains", compareVal, CONJUNCTION_AND, "!");
            case isNull:
                return field + " == null";
            case notNull:
                return field + " != null";
            default:
                return expr;
        }
    }

    private Object getCompareValue() {
        Object compareVal = value;
        if (value instanceof String && StringUtils.isNotBlank((String) value)) {
            String[] split = StringUtils.split((String) value, " ,");
            if (split.length == 1) {
                compareVal = split[0];
            } else {
                compareVal = Arrays.asList(split);
            }
        }
        return compareVal;
    }

    private String populate(RuleValueType valueType, String comparor, Object compareVal,
                            String conjunction, String... prefix) {
        if (compareVal instanceof List && CollectionUtils.isNotEmpty((List) compareVal)) {
            return getCompareGroup(valueType, comparor, (List) compareVal, conjunction, prefix);
        } else {
            return getCompareItem(valueType, comparor, compareVal, prefix);
        }
    }

    private String getCompareGroup(RuleValueType valueType, String comparor, List valueList,
                                   String conjunction, String[] prefix) {
        String expr = "(";
        boolean starts = true;
        for (Object val : valueList) {
            if (!starts) {
                expr += conjunction;
            }
            starts = false;
            expr += getCompareItem(valueType, comparor, val, prefix);
        }
        expr += ")";
        return expr;
    }

    private String getCompareItem(RuleValueType valueType, String comparor, Object val, String... prefix) {
        String expr = field + comparor + "(" + wrapValueToProperType(val.toString(), valueType) + ")";
        if (ArrayUtils.isNotEmpty(prefix)) {
            expr = prefix[0] + "(" + expr + ")";
        }
        return expr;
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
