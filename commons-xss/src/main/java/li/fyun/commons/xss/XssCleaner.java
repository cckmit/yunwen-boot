package li.fyun.commons.xss;

public interface XssCleaner {

    String cleanXSS(String key, String value);

    default boolean isRichTextFields(String key) {
        return false;
    }

}
