package li.fyun.commons.core;

import li.fyun.commons.core.controller.RestControllerAdvice;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Configuration
@ComponentScan(basePackageClasses = YunwenCommonsAutoConfig.class)
@ConditionalOnClass(RestControllerAdvice.class)
@ConditionalOnMissingBean(annotation = ControllerAdvice.class)
public class YunwenCommonsAutoConfig {

    @Bean
    public RestControllerAdvice restControllerAdvice() {
        return new RestControllerAdvice();
    }

}
