package li.fyun.commons.file;

import li.fyun.commons.file.impl.AliyunOssStorage;
import li.fyun.commons.file.impl.DatabaseFileStorage;
import li.fyun.commons.file.impl.LocalFileStorage;
import net.rossillo.spring.web.mvc.CacheControlHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(basePackageClasses = YunwenFileUploadAutoConfig.class)
@EntityScan(basePackageClasses = YunwenFileUploadAutoConfig.class)
@EnableJpaRepositories(basePackageClasses = YunwenFileUploadAutoConfig.class)
@EnableConfigurationProperties
public class YunwenFileUploadAutoConfig implements WebMvcConfigurer {

    @ConditionalOnMissingBean(FileStorage.class)
    @Bean
    public FileStorage fileStorage(@Autowired YunwenFileUploadProperties ywFileUploadProperties) {
        if ("DATABASE".equals(ywFileUploadProperties.getStorage())) {
            return new DatabaseFileStorage();
        } else if ("ALIYUN-OSS".equals(ywFileUploadProperties.getStorage())) {
            return new AliyunOssStorage(ywFileUploadProperties);
        } else {
            return new LocalFileStorage();
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CacheControlHandlerInterceptor());
    }

}
