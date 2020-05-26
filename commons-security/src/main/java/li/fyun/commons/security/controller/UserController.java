package li.fyun.commons.security.controller;

import li.fyun.commons.core.controller.BaseController;
import li.fyun.commons.security.entity.UserAccount;
import li.fyun.commons.security.service.SecurityService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/security/users")
public class UserController extends BaseController {

    @Resource
    private SecurityService securityService;

    @PostMapping(value = "/register")
    @PermitAll
    public void register(@RequestBody UserAccount userAccount) {
        securityService.register(userAccount);
    }

    @GetMapping(value = "/exists")
    @PermitAll
    public boolean exists(String word) {
        return securityService.userAccountExists(word);
    }

    @PostMapping(value = "/reset-token")
    @PermitAll
    public Map<String, Object> resetPasswordToken(String dest, String channel) {
        securityService.resetPasswordToken(dest, channel);
        return new HashMap<String, Object>() {{
            put("email", dest);
            put("status", "SENT");
            put("message", StringUtils.equals(SecurityService.CHANNEL_EMAIL, channel) ?
                    "已将验证码发送到电子邮箱" : "已将验证码发送到绑定手机");
        }};
    }

    @PutMapping(value = "/reset-password")
    @PermitAll
    public ResponseEntity resetPassword(Long userId, String token, String password) {
        securityService.resetPassword(userId, token, password);
        return OK;
    }

}
