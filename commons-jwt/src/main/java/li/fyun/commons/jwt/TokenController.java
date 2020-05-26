package li.fyun.commons.jwt;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public
class TokenController {

    @Resource
    private TokenAuthcService tokenAuthcService;

    @GetMapping(value = "/auth/refresh-token", produces = MediaType.APPLICATION_JSON_VALUE)
    public JwtTokenResponse refresh(@RequestParam("refresh_token") String refreshToken) {
        return tokenAuthcService.refreshToken(refreshToken);
    }

}
