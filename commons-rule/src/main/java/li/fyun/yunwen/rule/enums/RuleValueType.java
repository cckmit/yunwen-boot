package li.fyun.yunwen.rule.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum RuleValueType {

    NUMERIC("数值型"),
    STRING("字符型");
//    DATE("日期型");

    private String displayName;

    RuleValueType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Iterable asList() {
        List<Map<String, String>> result = new ArrayList<>();
        for (RuleValueType type : RuleValueType.values()) {
            Map<String, String> mapType = new HashMap<>();
            mapType.put("name", type.name());
            mapType.put("displayName", type.getDisplayName());
            result.add(mapType);
        }
        return result;
    }

}
