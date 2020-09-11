package li.fyun.commons.core.controller;

import com.google.common.collect.ImmutableSet;
import li.fyun.commons.core.utils.ResponseWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Map;
import java.util.Set;

@RestControllerAdvice
public class WrapResponseEntityRestControllerAdvice extends DefaultRestControllerAdvice implements ResponseBodyAdvice<Object> {

    private static final Set<String> WRAP_FIELDS = ImmutableSet.of("timestamp", "status", "message", "path");
    private static AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Value("${app.wrap-response-entity.no-wrap-requests}")
    private String[] noWrapRequests;

    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    public Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType,
                                  MediaType selectedContentType, Class selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        //判断url是否需要拦截
        if (this.shouldIgnore(request.getURI().toString())) {
            return body;
        }

        String uri = request.getURI().getPath();
        if (body instanceof Map && CollectionUtils.containsAll(((Map) body).keySet(), WRAP_FIELDS)) {
            return body;
        }
        return body != null && body instanceof ResponseWrapper ?
                body : ResponseWrapper.success(body, uri);
    }

    protected boolean shouldIgnore(String uri) {
        for (String noWrap : noWrapRequests) {
            if (antPathMatcher.match(noWrap, uri)) {
                return true;
            }
        }
        return false;
    }

}
