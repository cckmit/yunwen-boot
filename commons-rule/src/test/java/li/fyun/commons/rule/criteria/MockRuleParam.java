package li.fyun.commons.rule.criteria;

import li.fyun.commons.rule.engine.IParam;
import li.fyun.commons.rule.enums.RuleValueType;

public class MockRuleParam implements IParam {

    private String code;
    private RuleValueType valueType;

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public RuleValueType getValueType() {
        return valueType;
    }

    public void setValueType(RuleValueType valueType) {
        this.valueType = valueType;
    }
}
