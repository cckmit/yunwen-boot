package li.fyun.commons.security.service;

import li.fyun.commons.security.entity.GrantedResource;
import li.fyun.commons.security.repository.GrantedResourceRepository;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class ResourceSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    @Resource
    private GrantedResourceRepository grantedResourceRepository;

    private Map<String, Collection<ConfigAttribute>> grantedResourceMap = Maps.newLinkedHashMap();
    private boolean loaded;
    private Lock lock = new ReentrantLock();

    /**
     * 判定用户请求的url是否在权限表中
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        if (!loaded) {
            this.loadGrantedResources();
        }
        FilterInvocation fi = (FilterInvocation) o;
        String requestUrl = (fi).getRequestUrl();
        String method = fi.getRequest().getMethod();
        log.debug("requestUrl {} {}", method, requestUrl);
        PathMatcher pathMatcher = new AntPathMatcher();
        Iterator<String> iterator = grantedResourceMap.keySet().iterator();
        while (iterator.hasNext()) {
            String definedUrl = iterator.next();
            if (StringUtils.startsWithAny(definedUrl, GrantedResource.ALLOW_METHODS)) {
                requestUrl = method + requestUrl;
            }
            if (StringUtils.isNotBlank(definedUrl) && pathMatcher.match(definedUrl, requestUrl)) {
                return grantedResourceMap.get(definedUrl);
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        if (!loaded) {
            this.loadGrantedResources();
        }
        Collection<ConfigAttribute> configAttributes = Lists.newArrayList();
        grantedResourceMap.forEach((key, value) -> {
            configAttributes.addAll(value);
        });
        return configAttributes;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    /**
     * 加载URL授权表中所有操作请求权限
     */
    public void loadGrantedResources() {
        lock.lock();
        try {
            grantedResourceMap.clear();
            Collection<ConfigAttribute> configAttributes;
            List<GrantedResource> grantedResources = grantedResourceRepository.findAll();
            for (GrantedResource resource : grantedResources) {
                if (StringUtils.isBlank(resource.getUrl())) {
                    continue;
                }

                configAttributes = Lists.newArrayList();
                ConfigAttribute cfg = new SecurityConfig(resource.getPerm());
                configAttributes.add(cfg);
                HttpMethod method = resource.getHttpMethod();
                grantedResourceMap.put((method == null ? "" : method.name()) + resource.getUrl(), configAttributes);
            }
            loaded = true;
        } finally {
            lock.unlock();
        }
    }

}