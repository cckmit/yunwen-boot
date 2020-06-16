package li.fyun.commons.rule.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum RuleComparator {

    eq("等于"),
    ne("不等于"),
    gt("大于"),
    lt("小于"),
    ge("大于等于"),
    le("小于等于"),
    between("在...两者之间"),
    in("在...中"),
    notIn("不在...中"),
    belong("属于"),
    contains("包含"),
    startsWith("以...开头"),
    endsWith("以...结尾"),
    isNull("为空"),
    notNull("不为空");

    private String displayName;

    RuleComparator(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Iterable asList() {
        List<Map<String, String>> result = new ArrayList<>();
        for (RuleComparator type : RuleComparator.values()) {
            Map<String, String> mapType = new HashMap<>();
            mapType.put("name", type.name());
            mapType.put("displayName", type.getDisplayName());
            result.add(mapType);
        }
        return result;
    }

}
