package li.fyun.commons.jwt;

import li.fyun.commons.core.utils.YamlPropertySourceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

@Configuration
@PropertySource(
        value = {"classpath:application.yml", "classpath:jwt.yml"},
        ignoreResourceNotFound = true,
        factory = YamlPropertySourceFactory.class
)
@ComponentScan(basePackageClasses = YunwenJwtAutoConfig.class)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ConditionalOnMissingBean(WebSecurityConfigurer.class)
public class YunwenJwtAutoConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private JwtProperties jwtProperties;

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(jwtProperties.getPublicAssets());
    }

    /**
     * 设置 HTTP 验证规则
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // 关闭csrf验证
        httpSecurity = httpSecurity
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .csrf().disable()
                .cors().and();

        if (jwtProperties.isDefaultPermitAll()) {
            httpSecurity.authorizeRequests().anyRequest().permitAll();
        } else {
            httpSecurity.authorizeRequests().anyRequest().authenticated(); // 所有请求需要身份认证
        }

        httpSecurity
                .addFilterBefore(imageCaptchaFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(resourceSecurityInterceptor(), FilterSecurityInterceptor.class)
                .logout().logoutUrl(jwtProperties.getLogoutUrl()).logoutSuccessHandler(new LogoutHandler())
                .and().exceptionHandling().authenticationEntryPoint(unauthorizedEntryPoint())
                .and().formLogin().disable();
        httpSecurity.headers().frameOptions().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(usernamePasswordAuthenticationProvider());
    }

    @Bean
    public UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider() {
        return new UsernamePasswordAuthenticationProvider();
    }

    @Bean
    public JwtLoginFilter jwtLoginFilter() throws Exception {
        return new JwtLoginFilter(authenticationManager(), jwtProperties);
    }

    @Bean
    public JwtAuthcFilter jwtAuthenticationFilter() {
        return new JwtAuthcFilter();
    }

    @Bean
    public TokenAuthcService tokenAuthcService() {
        return new JwtTokenAuthcService();
    }

    @Bean
    public Http401UnauthorizedEntryPoint unauthorizedEntryPoint() {
        return new Http401UnauthorizedEntryPoint();
    }

    @Bean
    public ResourceSecurityInterceptor resourceSecurityInterceptor() {
        return new ResourceSecurityInterceptor();
    }

    @Bean
    public ImageCaptchaFilter imageCaptchaFilter() {
        return new ImageCaptchaFilter();
    }

}
