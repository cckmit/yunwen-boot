package li.fyun.commons.core.criteria.plain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import li.fyun.commons.core.criteria.Expression;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class PlainExpressionImpl extends PlainCriterionImpl implements Expression<PlainExpressionImpl, PlainComparor> {

    private String field;
    @Enumerated(EnumType.STRING)
    private PlainComparor op;
    private Object value;

    // 普通条件, AND/OR 不可同时存在
    @Override
    public boolean validate() {
        String field = getField();
        PlainComparor op = getOp();
        Object value = getValue();
        if (!this.validate(field)) {
            return false;
        }

        if (StringUtils.isNotBlank(field)) {
            if (op == null) {
                return false;
            }

            if (PlainComparor.isNull.equals(op) || PlainComparor.notNull.equals(op)) {
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

}
