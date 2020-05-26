package li.fyun.commons.jwt;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.Payload;
import li.fyun.commons.security.entity.UserAccount;
import li.fyun.commons.security.service.SecurityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Slf4j
public class JwtTokenAuthcService implements TokenAuthcService {

    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private JwtClaimsHandler jwtClaimsHandler;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private SecurityService securityService;

    @SuppressWarnings("unchecked")
    public void addAuthentication(HttpServletResponse response, Authentication auth) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) auth;
            // 生成JWT
            Long now = System.currentTimeMillis();
            String accessToken = jwtClaimsHandler.compactJwt(
                    authenticationToken.getName(),
                    new Date(now + jwtProperties.getAccessTokenExpirationTime()),
                    authenticationToken.getAuthorities());
            String refreshToken = jwtClaimsHandler.compactJwt(
                    authenticationToken.getName(),
                    new Date(now + jwtProperties.getRefreshTokenExpirationTime()),
                    null
            );

            JwtTokenResponse tokenResponse = new JwtTokenResponse(
                    StringUtils.lowerCase(StringUtils.substringBefore(jwtProperties.getTokenPrefix(), ".")),
                    accessToken,
                    jwtProperties.getAccessTokenExpirationTime() / 1000,
                    refreshToken);

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getOutputStream().println(objectMapper.writeValueAsString(tokenResponse));
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }

    public Authentication getAuthentication(HttpServletRequest request) {
        return getAuthenticationFromToken(getTokenFromRequest(request));
    }

    @SuppressWarnings("unchecked")
    public Authentication getAuthenticationFromToken(String token) {
        Payload payload = jwtClaimsHandler.getPayload(token);
        if (payload == null) {
            return null;
        }

        String username = payload.getSubject();
        if (username == null) {
            return null;
        }

        // 得到 权限（角色）
        List<GrantedAuthorityImpl> authorities = null;
        Claim claimAuthorities = payload.getClaim("authorities");
        if (claimAuthorities != null) {
            authorities = claimAuthorities.asList(GrantedAuthorityImpl.class);
        }

        return new UsernamePasswordAuthenticationToken(username, token, authorities);
    }

    protected String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(jwtProperties.getTokenHeaderString());
        log.debug("url {}, login token: {}", request.getRequestURL(), token);

        if (StringUtils.isBlank(token) && jwtProperties.isAllowQueryParam()) {
            String uri = StringUtils.substringAfter(request.getRequestURI(), request.getContextPath());
            if (ArrayUtils.contains(jwtProperties.getAllowQueryParamUrls(), uri)) {
                token = request.getParameter(jwtProperties.getTokenHeaderString());
            }
        }
        return token;
    }

    @SuppressWarnings("unchecked")
    public JwtTokenResponse refreshToken(String refreshToken) {
        Payload payload = jwtClaimsHandler.getPayload(refreshToken);
        if (payload == null) {
            throw new BadCredentialsException("无效的令牌");
        }

        String username = payload.getSubject();
        if (username == null) {
            throw new BadCredentialsException("无效的令牌");
        }

        // 生成JWT
        UserAccount userAccount = securityService.findByUsername(username);
        if (userAccount == null) {
            throw new BadCredentialsException("无效的令牌");
        }

        Long now = System.currentTimeMillis();
        String accessToken = jwtClaimsHandler.compactJwt(
                username,
                new Date(now + jwtProperties.getAccessTokenExpirationTime()),
                GrantedAuthorityImpl.getGrantedAuthorities(userAccount));
        String newRefreshToken = jwtClaimsHandler.compactJwt(
                username,
                new Date(now + jwtProperties.getRefreshTokenExpirationTime()),
                null
        );

        JwtTokenResponse tokenResponse = new JwtTokenResponse(
                StringUtils.lowerCase(StringUtils.substringBefore(jwtProperties.getTokenPrefix(), ".")),
                accessToken,
                jwtProperties.getAccessTokenExpirationTime() / 1000,
                newRefreshToken);
        return tokenResponse;
    }

}
