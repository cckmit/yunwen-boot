package li.fyun.commons.jwt;

import org.springframework.security.core.Authentication;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface TokenAuthcService {

    Authentication getAuthentication(HttpServletRequest request);

    void addAuthentication(HttpServletResponse response, Authentication auth);

    JwtTokenResponse refreshToken(String refreshToken);

}
