package li.fyun.commons.messaging;

import li.fyun.commons.core.utils.SnowflakeSequence;
import li.fyun.commons.core.utils.YamlPropertySourceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(
        value = {"classpath:application.yml", "classpath:messaging.yml"},
        ignoreResourceNotFound = true,
        factory = YamlPropertySourceFactory.class
)
@ComponentScan(basePackageClasses = MessagingAutoConfig.class)
public class MessagingAutoConfig {

    @Bean
    @ConditionalOnMissingBean(SnowflakeSequence.class)
    public SnowflakeSequence snowflakeSequence() {
        return new SnowflakeSequence();
    }

}
