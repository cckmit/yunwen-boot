package li.fyun.commons.core.utils;

import org.springframework.cache.interceptor.KeyGenerator;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

public final class CacheKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        return new WorkingKey(target.getClass(), method.getName(), params);
    }

    /**
     * Like {@link org.springframework.cache.interceptor.SimpleKey} but considers the method.
     */
    static final class WorkingKey implements Serializable {
        private static final long serialVersionUID = 6989926895431484183L;
        private final Class<?> clazz;
        private final String methodName;
        private final Object[] params;
        private final int hashCode;

        /**
         * Initialize a key.
         *
         * @param clazz      the receiver class
         * @param methodName the method name
         * @param params     the method parameters
         */
        WorkingKey(Class<?> clazz, String methodName, Object[] params) {
            this.clazz = clazz;
            this.methodName = methodName;
            this.params = params;
            int code = Arrays.deepHashCode(params);
            code = 31 * code + clazz.hashCode();
            code = 31 * code + methodName.hashCode();
            this.hashCode = code;
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof WorkingKey)) {
                return false;
            }
            WorkingKey other = (WorkingKey) obj;
            if (this.hashCode != other.hashCode) {
                return false;
            }

            return this.clazz.equals(other.clazz)
                    && this.methodName.equals(other.methodName)
                    && Arrays.deepEquals(this.params, other.params);
        }

    }

}