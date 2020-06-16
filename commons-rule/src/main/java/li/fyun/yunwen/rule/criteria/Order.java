package li.fyun.yunwen.rule.criteria;

public interface Order {

    String getField();
    void setField(String field);

    boolean isAscending();
    void setAscending(boolean ascending);

}
