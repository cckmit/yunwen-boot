package li.fyun.commons.core;

import li.fyun.commons.core.controller.DefaultRestControllerAdvice;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Configuration
@ComponentScan(basePackageClasses = YunwenCommonsAutoConfig.class)
public class YunwenCommonsAutoConfig {

    @Bean
    @ConditionalOnMissingBean(annotation = RestControllerAdvice.class)
    public DefaultRestControllerAdvice restControllerAdvice() {
        return new DefaultRestControllerAdvice();
    }

}
