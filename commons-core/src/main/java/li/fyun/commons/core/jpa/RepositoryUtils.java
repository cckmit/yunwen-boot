package li.fyun.commons.core.jpa;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;

public final class RepositoryUtils {

    public static final int MIN_PAGE_SIZE = 5;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int APP_INIT_ENTITY_ID_LIMIT = 100;

    public static final PageRequest buildPageRequest(int page, int size) {
        int jpaPage = page > 0 ? page : 0;
        int jpaSize = size > MIN_PAGE_SIZE ? size : MIN_PAGE_SIZE;
        return PageRequest.of(jpaPage, jpaSize);
    }

    public static final PageRequest buildPageRequest(String page, String size) {
        return buildPageRequest(page, size, false);
    }

    public static final PageRequest buildPageRequest(String page, String size, boolean forcePaging) {
        if (StringUtils.isBlank(page)) {
            if (forcePaging) {
                return buildPageRequest(0, DEFAULT_PAGE_SIZE);
            } else {
                return null;
            }
        }
        int iPage = 0;
        try {
            iPage = Integer.parseInt(page);
        } catch (Exception ignore) {

        }
        int iSize = RepositoryUtils.DEFAULT_PAGE_SIZE;
        try {
            iSize = Integer.parseInt(size);
        } catch (Exception ignore) {

        }
        return buildPageRequest(iPage, iSize);
    }

}
