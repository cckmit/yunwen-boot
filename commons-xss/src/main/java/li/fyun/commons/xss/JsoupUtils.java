package li.fyun.commons.xss;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

public class JsoupUtils {

    private static final Whitelist defaultWhitelist = Whitelist.basicWithImages()
            .addTags("h1", "h2", "h3", "h4", "h5", "h6")
            .addAttributes(":all", "class")
            .addAttributes(":all", "style")
            .addProtocols("img", "src", "data");

    /**
     * 配置过滤化参数,不对代码进行格式化
     */
    private static final Document.OutputSettings outputSettings = new Document.OutputSettings().prettyPrint(false);

    public static String clean(String content) {
        return clean(content, defaultWhitelist);
    }

    public static String clean(String content, Whitelist whitelist) {
        String baseUri = "https://baseuri";
        return Jsoup.clean(content, baseUri, whitelist, outputSettings).replaceAll(baseUri, "");
    }

}