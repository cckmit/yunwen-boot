package li.fyun.commons.core.utils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Iterator;
import java.util.List;

public interface Replaceable<E extends Replaceable> {

    boolean isMirror(E search);

    void replace(E mirror);

    static <T extends Replaceable> T findMirror(List<T> destLiST, T search) {
        if (CollectionUtils.isNotEmpty(destLiST)) {
            for (T dest : destLiST) {
                if (dest.isMirror(search)) {
                    return dest;
                }
            }
        }
        return null;
    }

    static <T extends Replaceable> void removeItem(List<T> destList, T search) {
        if (CollectionUtils.isNotEmpty(destList)) {
            Iterator<T> iterator = destList.iterator();
            while (iterator.hasNext()) {
                T dest = iterator.next();
                if (dest.isMirror(search)) {
                    iterator.remove();
                }
            }
        }
    }

}
