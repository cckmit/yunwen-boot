package li.fyun.commons.core.criteria;

public interface Expression<T extends Expression, OP> extends Criterion<T> {

    String getField();

    void setField(String field);

    OP getOp();

    void setOp(OP op);

    Object getValue();

    void setValue(Object value);

}
