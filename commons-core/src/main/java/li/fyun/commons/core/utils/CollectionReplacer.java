package li.fyun.commons.core.utils;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Iterator;
import java.util.List;

public final class CollectionReplacer {

    @SuppressWarnings("unchecked")
    public static  <T extends Replaceable> void replaceAll(List<T> oldList, List<T> newList) {
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
            T mirror = Replaceable.findMirror(newList, dataField);
            if (mirror == null) {
                dataFieldIterator.remove();
            } else {
                dataField.replace(mirror);
                Replaceable.removeItem(newList, mirror);
            }
        }
        // 加上新的
        if (CollectionUtils.isNotEmpty(newList)) {
            oldList.addAll(newList);
        }
    }

}
