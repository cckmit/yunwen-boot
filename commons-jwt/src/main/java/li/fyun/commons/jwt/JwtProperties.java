package li.fyun.commons.jwt;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class JwtProperties {

    @Value("${app.security.jwt.access-token-expiration-time}")
    private long accessTokenExpirationTime;
    @Value("${app.security.jwt.refresh-token-expiration-time}")
    private long refreshTokenExpirationTime;
    @Value("${app.security.jwt.token-secret-key}")
    private String tokenSecretKey;
    @Value("${app.security.jwt.token-prefix}")
    private String tokenPrefix;
    @Value("${app.security.jwt.token-header-string}")
    private String tokenHeaderString;
    @Value("${app.security.jwt.default-permit-all}")
    private boolean defaultPermitAll;
    @Value("${app.security.jwt.public-assets}")
    private String[] publicAssets;
    @Value("${app.security.jwt.allow-query-param}")
    private boolean allowQueryParam;
    @Value("${app.security.jwt.allow-query-param-urls}")
    private String[] allowQueryParamUrls;
    @Value("${app.security.jwt.open-id-types}")
    private String[] openIdTypes;
    @Value("${app.security.jwt.issuer}")
    private String issuer;
    @Value("${app.security.jwt.login-url}")
    private String loginUrl;
    @Value("${app.security.jwt.logout-url}")
    private String logoutUrl;
    @Value("${app.security.jwt.need-image-captcha-urls}")
    private String[] needImageCaptchaUrls;

}
