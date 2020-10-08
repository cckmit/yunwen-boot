package li.fyun.commons.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

@Slf4j
public final class CacheKeyGenerator implements KeyGenerator {

    // custom cache key
    private static final int NO_PARAM_KEY = 0;
    private static final int NULL_PARAM_KEY = 53;
    private static final String PARAM_KEY_DELIMITER = "_";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringBuilder key = new StringBuilder();
        key.append(target.getClass().getSimpleName()).append(".")
                .append(method.getName()).append('(');
        if (params.length == 0) {
            return key.append(NO_PARAM_KEY).toString();
        }
        for (Object param : params) {
            if (param == null) {
                log.warn("input null param for Spring cache, use default key={}", NULL_PARAM_KEY);
                key.append(NULL_PARAM_KEY);
            } else if (ClassUtils.isPrimitiveArray(param.getClass())) {
                int length = Array.getLength(param);
                for (int i = 0; i < length; i++) {
                    key.append(Array.get(param, i));
                    key.append(',');
                }
            } else if (ClassUtils.isPrimitiveOrWrapper(param.getClass()) || param instanceof String) {
                key.append(param);
            } else {
                log.warn("Using an object as a cache key may lead to unexpected results. " +
                        "Either use @Cacheable(key=..) or implement CacheKey. Method is " + target.getClass() + "#" + method.getName());
                try {
                    key.append(objectMapper.writeValueAsString(param));
                } catch (JsonProcessingException e) {
                    key.append(param.hashCode());
                }
            }
            key.append(PARAM_KEY_DELIMITER);
        }

        String finalKey = StringUtils.substringBeforeLast(key.toString(), PARAM_KEY_DELIMITER) + ")";;
        log.debug("using cache key={}", finalKey);
        return finalKey;
    }

}