package li.fyun.commons.core.criteria.plain;

import java.util.*;

public enum PlainComparor {

    eq,
    ne,
    like,
    gt,
    lt,
    ge,
    le,
    between,
    in,
    notIn,
    isNull,
    notNull;

    public static Iterable asList() {
        List<String> result = new ArrayList<>();
        for (PlainComparor type : PlainComparor.values()) {
            result.add(type.name());
        }
        return result;
    }

}
