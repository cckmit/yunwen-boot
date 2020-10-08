package li.fyun.commons.core.utils;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unchecked")
public interface Replacement<T extends Replacement> {

    boolean isMirror(T search);

    void replace(T mirror);

    static <T extends Replacement> T findMirror(List<T> mirrors, T search) {
        if (CollectionUtils.isNotEmpty(mirrors)) {
            for (T dest : mirrors) {
                if (dest.isMirror(search)) {
                    return dest;
                }
            }
        }
        return null;
    }

    static <T extends Replacement> void removeMirror(List<T> mirrors, T search) {
        if (CollectionUtils.isNotEmpty(mirrors)) {
            Iterator<T> iterator = mirrors.iterator();
            while (iterator.hasNext()) {
                T dest = iterator.next();
                if (dest.isMirror(search)) {
                    iterator.remove();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    static <T extends Replacement> List<T> replaceAll(List<T> origin, List<T> replaces) {
        if (origin == null) {
            origin = Lists.newArrayList();
        }

        if (CollectionUtils.isEmpty(replaces)) {
            origin.clear();
            return origin;
        }

        Iterator<T> dataFieldIterator = origin.iterator();
        while (dataFieldIterator.hasNext()) {
            T dataField = dataFieldIterator.next();
            T mirror = findMirror(replaces, dataField);
            if (mirror == null) {
                dataFieldIterator.remove();
            } else {
                dataField.replace(mirror);
                removeMirror(replaces, mirror);
            }
        }
        // 加上新的
        if (CollectionUtils.isNotEmpty(replaces)) {
            origin.addAll(replaces);
        }
        return origin;
    }

}
