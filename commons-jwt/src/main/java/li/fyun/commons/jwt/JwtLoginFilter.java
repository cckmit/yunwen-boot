package li.fyun.commons.jwt;

import li.fyun.commons.core.utils.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {

    private static final String LOGIN_URL = "/security/login";
    private static final String REQ_PARAM_USERNAME = "username";
    private static final String REQ_PARAM_PASSWORD = "password";

    @Autowired
    private TokenAuthcService tokenAuthcService;

    public JwtLoginFilter(AuthenticationManager authManager, JwtProperties jwtProperties) {
        super(new AntPathRequestMatcher(jwtProperties.getLoginUrl()));
        setAuthenticationManager(authManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        // 返回一个验证令牌
        return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getParameter(REQ_PARAM_USERNAME),
                        req.getParameter(REQ_PARAM_PASSWORD)
                )
        );
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) {
        tokenAuthcService.addAuthentication(res, auth);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed)
            throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(failed, HttpStatus.INTERNAL_SERVER_ERROR.value(), LOGIN_URL);
        errorResponse.print(response);
    }

}
