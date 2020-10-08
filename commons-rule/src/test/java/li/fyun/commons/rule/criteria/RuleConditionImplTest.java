package li.fyun.commons.rule.criteria;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import li.fyun.commons.rule.enums.RuleComparator;
import li.fyun.commons.rule.enums.RuleValueType;
import org.junit.Test;

import java.util.List;

public class RuleConditionImplTest {

    @Test
    public void testBuild() throws JsonProcessingException {
        RuleConditionImpl condition = new RuleConditionImpl();
        List<RuleExpressionImpl> andExprs = Lists.newArrayList();
        condition.setAnd(andExprs);

        RuleExpressionImpl simpleExpression = new RuleExpressionImpl();
        simpleExpression.setField("price");
        simpleExpression.setOp(RuleComparator.eq);
        simpleExpression.setValue("2");
        andExprs.add(simpleExpression);

        RuleExpressionImpl simpleExpression1 = new RuleExpressionImpl();
        simpleExpression1.setField("weight");
        simpleExpression1.setOp(RuleComparator.between);
        simpleExpression1.setValue(ImmutableList.of(100, 300));
        andExprs.add(simpleExpression1);

        RuleExpressionImpl orExpression = new RuleExpressionImpl();
        List<RuleExpressionImpl> or = Lists.newArrayList();
        orExpression.setOr(or);
        andExprs.add(orExpression);

        RuleExpressionImpl simpleExpression2 = new RuleExpressionImpl();
        simpleExpression2.setField("grade");
        simpleExpression2.setOp(RuleComparator.gt);
        simpleExpression2.setValue("V3");
        or.add(simpleExpression2);

        RuleExpressionImpl simpleExpression3 = new RuleExpressionImpl();
        simpleExpression3.setField("product");
        simpleExpression3.setOp(RuleComparator.belong);
        simpleExpression3.setValue(ImmutableList.of("101", "102"));
        or.add(simpleExpression3);

        RuleExpressionImpl simpleExpression4 = new RuleExpressionImpl();
        simpleExpression4.setField("customer");
        simpleExpression4.setOp(RuleComparator.in);
        simpleExpression4.setValue(ImmutableList.of("C1", "C2"));
        or.add(simpleExpression4);

        MockRuleParam param = new MockRuleParam();
        param.setCode("price");
        param.setValueType(RuleValueType.NUMERIC);
        MockRuleParam param1 = new MockRuleParam();
        param1.setCode("weight");
        param1.setValueType(RuleValueType.NUMERIC);
        MockRuleParam param2 = new MockRuleParam();
        param2.setCode("grade");
        param2.setValueType(RuleValueType.STRING);
        MockRuleParam param3 = new MockRuleParam();
        param3.setCode("product");
        param3.setValueType(RuleValueType.STRING);
        MockRuleParam param4 = new MockRuleParam();
        param4.setCode("customer");
        param4.setValueType(RuleValueType.STRING);

        String result = RuleConditionBuilder.getInstance()
                .build(condition, ImmutableList.of(param, param1, param2, param3, param4));
        System.out.println(result);
    }

}