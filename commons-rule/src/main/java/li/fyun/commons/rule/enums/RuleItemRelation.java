package li.fyun.commons.rule.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum RuleItemRelation {

    OR("或"),
    AND("和");

    private String displayName;

    RuleItemRelation(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Iterable asList() {
        List<Map<String, String>> result = new ArrayList<>();
        for (RuleItemRelation type : RuleItemRelation.values()) {
            Map<String, String> mapType = new HashMap<>();
            mapType.put("name", type.name());
            mapType.put("displayName", type.getDisplayName());
            result.add(mapType);
        }
        return result;
    }

}
