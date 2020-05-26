package li.fyun.commons.security;

import li.fyun.commons.core.utils.YamlPropertySourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.cache.jcache.JCacheManagerFactoryBean;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;

@Configuration
@EnableAsync
@EnableCaching
@PropertySource(
        value = {"classpath:application.yml", "classpath:security.yml"},
        ignoreResourceNotFound = true,
        factory = YamlPropertySourceFactory.class
)
@ComponentScan(basePackageClasses = YunwenSecurityAutoConfig.class)
@EntityScan(basePackageClasses = YunwenSecurityAutoConfig.class)
@EnableJpaRepositories(basePackageClasses = YunwenSecurityAutoConfig.class)
public class YunwenSecurityAutoConfig {

    @Bean
    @ConditionalOnMissingBean(name = "cacheManager")
    public CacheManager cacheManager() {
        try {
            return new JCacheCacheManager(cacheManagerFactoryBean().getObject());
        } catch (IOException e) {
            return new ConcurrentMapCacheManager();
        }
    }

    @Bean("cacheManagerFactoryBean")
    @ConditionalOnMissingBean(name = "cacheManagerFactoryBean")
    public JCacheManagerFactoryBean cacheManagerFactoryBean() throws IOException {
        JCacheManagerFactoryBean factoryBean = new JCacheManagerFactoryBean();
        factoryBean.setCacheManagerUri(new ClassPathResource("ehcache.xml").getURI());
        return factoryBean;
    }

    @Bean("emailCache")
    public Cache emailCache(@Autowired CacheManager cacheManager) {
        return cacheManager.getCache("emailCache");
    }

    @Bean("captchaCache")
    public Cache captchaCache(@Autowired CacheManager cacheManager) {
        return cacheManager.getCache("captchaCache");
    }

}
