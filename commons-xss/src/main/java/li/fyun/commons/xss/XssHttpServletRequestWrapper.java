package li.fyun.commons.xss;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private XssCleaner xssCleaner;

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        xssCleaner = WebApplicationContextUtils.
                getRequiredWebApplicationContext(request.getServletContext()).
                getBean(XssCleaner.class);
    }

    @Override
    public String getQueryString() {
        String queryString = super.getQueryString();
        if (StringUtils.isBlank(queryString)) {
            return queryString;
        }
        return xssCleaner.cleanXSS(null, queryString).replaceAll("&amp;", "&");
    }

    @Override
    public String getParameter(String key) {
        String value = super.getParameter(key);
        if (value != null) {
            value = xssCleaner.cleanXSS(key, value);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public Map<String, String[]> getParameterMap() {
        Map map = super.getParameterMap();
        if (MapUtils.isEmpty(map)) {
            return map;
        }

        Map paramMap = new HashMap(super.getParameterMap());

        Set<Map.Entry> entries = map.entrySet();
        for (Map.Entry entry : entries) {
            String key = (String) entry.getKey();
            Object values = entry.getValue();

            if (values == null) {
                paramMap.remove(key);
                continue;
            }

            String[] souceArray = (String[]) values;
            String[] cleanArray = new String[souceArray.length];
            for (int i = 0; i < souceArray.length; i++) {
                cleanArray[i] = xssCleaner.cleanXSS(key, souceArray[i]);
            }
            paramMap.put(xssCleaner.cleanXSS(null, key), cleanArray);
        }
        return paramMap;
    }

    @Override
    public String[] getParameterValues(String key) {
        String[] value = super.getParameterValues(key);
        if (value != null) {
            for (int i = 0; i < value.length; i++) {
                value[i] = xssCleaner.cleanXSS(key, value[i]);
            }
        }
        return value;
    }

    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value != null) {
            value = xssCleaner.cleanXSS(name, value);
        }
        return value;
    }

}
