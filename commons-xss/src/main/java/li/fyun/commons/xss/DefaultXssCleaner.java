package li.fyun.commons.xss;

import com.google.common.html.HtmlEscapers;
import org.apache.commons.lang3.StringUtils;

public class DefaultXssCleaner implements XssCleaner {

    @Override
    public String cleanXSS(String key, String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        value = JsoupUtils.clean(value);
        return isRichTextFields(key) ? value : HtmlEscapers.htmlEscaper().escape(value);
    }

}
