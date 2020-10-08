package li.fyun.commons.core.criteria;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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

}
