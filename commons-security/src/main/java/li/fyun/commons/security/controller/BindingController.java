package li.fyun.commons.security.controller;

import li.fyun.commons.security.entity.UserAccount;
import li.fyun.commons.security.service.SecurityService;
import li.fyun.commons.core.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/security/bind")
@PreAuthorize("isAuthenticated()")
public class BindingController extends BaseController {

    @Resource
    private SecurityService securityService;

    @PostMapping(value = "/sms-captcha", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> sendSmsCaptcha(String mobile) {
        securityService.sendMobileBindingCaptcha(mobile);
        return new HashMap<String, String>() {{
            put("mobile", mobile);
            put("status", "SENT");
            put("message", "已发送手机验证码");
        }};
    }

    @PostMapping(value = "/bind-mobile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity bindMobile(String mobile, String captcha) {
        String username = UserAccount.loggedInUser();
        securityService.changeMobile(username, mobile, captcha);
        return OK;
    }

    @PostMapping(value = "/mail-captcha", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> sendMailCaptcha(String email, HttpServletRequest request) {
        securityService.sendEmailBindingCaptcha(email, request);
        return new HashMap<String, String>() {{
            put("email", email);
            put("status", "SENT");
            put("message", "已发送邮箱绑定确认信");
        }};
    }

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/bind-email", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity bindEmail(@RequestParam("uid") Long userId, String token) {
        Assert.notNull(userId, "非法参数");
        Assert.isTrue(StringUtils.isNotBlank(token), "非法参数");
        securityService.changeEmail(userId, token);
        return OK;
    }

    @PostMapping(value = "/unbind-mobile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity clearMobile() {
        String username = UserAccount.loggedInUser();
        securityService.clearMobile(username);
        return OK;
    }

    @PostMapping(value = "/unbind-email", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity clearEmail() {
        String username = UserAccount.loggedInUser();
        securityService.clearEmail(username);
        return OK;
    }

}
