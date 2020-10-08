package li.fyun.commons.core.criteria;

public interface Order {

    String getField();
    void setField(String field);

    boolean isAscending();
    void setAscending(boolean ascending);

}
