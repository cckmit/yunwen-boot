package li.fyun.commons.security.controller;

import li.fyun.commons.security.service.CaptchaService;
import net.rossillo.spring.web.mvc.CacheControl;
import net.rossillo.spring.web.mvc.CachePolicy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
public class CaptchaController {

    @Resource
    private CaptchaService captchaService;

    @GetMapping(value = "/security/captcha")
    @CacheControl(policy = CachePolicy.NO_CACHE)
    @SuppressWarnings("unchecked")
    public Map<String, ?> captcha() {
        return captchaService.generateImageCaptcha();
    }

}
