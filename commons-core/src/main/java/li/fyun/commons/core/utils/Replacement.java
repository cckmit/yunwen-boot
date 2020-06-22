package li.fyun.commons.core.utils;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unchecked")
public interface Replacement<T extends Replacement> {

    boolean isMirror(T search);

    void replace(T mirror);

    default T findMirror(List<T> mirrors, T search) {
        if (CollectionUtils.isNotEmpty(mirrors)) {
            for (T dest : mirrors) {
                if (dest.isMirror(search)) {
                    return dest;
                }
            }
        }
        return null;
    }

    default void removeMirror(List<T> mirrors, T search) {
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
    default void replaceAll(List<T> oldList, List<T> newList) {
        if (oldList == null) {
            oldList = Lists.newArrayList();
        }

        if (CollectionUtils.isEmpty(newList)) {
            oldList.clear();
            return;
        }

        Iterator<T> dataFieldIterator = oldList.iterator();
        while (dataFieldIterator.hasNext()) {
            T dataField = dataFieldIterator.next();
            T mirror = this.findMirror(newList, dataField);
            if (mirror == null) {
                dataFieldIterator.remove();
            } else {
                dataField.replace(mirror);
                this.removeMirror(newList, mirror);
            }
        }
        // 加上新的
        if (CollectionUtils.isNotEmpty(newList)) {
            oldList.addAll(newList);
        }
    }

}
