package li.fyun.commons.security.service;

import li.fyun.commons.security.YunwenSecurityException;
import li.fyun.commons.security.entity.UserAccount;
import li.fyun.commons.security.repository.UserAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class CaptchaService {

    @Value("${app.sms-template.captcha}")
    private String captchaTemplate;

    @Autowired(required = false)
    private SecurityMessageService securityMessageService;
    @Resource
    private UserAccountRepository userAccountRepository;
    @Resource
    @Qualifier("captchaCache")
    private Cache captchaCache;
    @Resource
    @Qualifier("emailCache")
    private Cache emailCache;

    private String mailSubject = "账号绑定邮箱确认";
    private String mailContent = "您已申请绑定邮箱，请点击一下链接确认: \r\n";

    public String sendSmsCaptcha(String mobile) {
        String captcha = generateSmsCaptcha(mobile);
        this.sendSmsCaptcha(mobile, captcha);
        return captcha;
    }

    public boolean validateSmsCaptcha(String mobile, String captcha) {
        String holdCaptcha = captchaCache.get(mobile, String.class);
        return StringUtils.equals(holdCaptcha, captcha);
    }

    public String generateSmsCaptcha(String mobile) {
        synchronized (mobile) {
            String captcha = captchaCache.get(mobile, String.class);
            if (StringUtils.isBlank(captcha)) {
                captcha = RandomStringUtils.randomNumeric(6);
                captchaCache.put(mobile, captcha);
            }
            log.info("generated captcha {} for mobile {}", captcha, mobile);
            return captcha;
        }
    }

    public void sendSmsCaptcha(String mobile, String captcha) {
        Map<String, String> params = new HashMap<>();
        params.put("code", captcha);
        if (securityMessageService != null) {
            securityMessageService.sendSms(mobile, captchaTemplate, params);
        }
    }

    public void sendEmailCaptcha(String email, String link) {
        String username = UserAccount.loggedInUser();
        String captcha = generateEmailKey(email, username);
        UserAccount userAccount = userAccountRepository.findByUsername(username);
        String content = mailContent + link + "?uid=" + userAccount.getId() + "&token=" + captcha;
        if (securityMessageService != null) {
            securityMessageService.sendSimpleMail(email, mailSubject, content);
        }
    }

    private String generateEmailKey(String email, String username) {
        synchronized (email) {
            String key = emailCache.get(email, String.class);
            if (StringUtils.isBlank(key)) {
                key = RandomStringUtils.randomAlphanumeric(32);
                emailCache.put(key, email + ":" + username);
            }
            log.info("generated captcha {} for email {}", key, email);
            return key;
        }
    }

    public String getEmailByCachedKey(String key, String username) {
        String heldEmail = emailCache.get(key, String.class);
        if (StringUtils.equals(StringUtils.substringAfter(heldEmail, ":"), username)) {
            return StringUtils.substringBefore(heldEmail, ":");
        } else {
            throw new YunwenSecurityException("无效的绑定请求");
        }
    }

    public static final String REQ_PARAM_CAPTCHA_KEY = "captchaKey";
    public static final String REQ_PARAM_CAPTCHA_VALUE = "captchaValue";
    public static final String REQ_PARAM_CAPTCHA_IMAGE = "captchaImage";

    @SuppressWarnings("unchecked")
    public LinkedHashMap generateImageCaptcha() {
        TransparentPngCaptcha specCaptcha = new TransparentPngCaptcha(119, 40, 4);
        specCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        String captchaKey = UUID.randomUUID().toString();
        captchaCache.put(captchaKey, specCaptcha.text().toLowerCase());
        return new LinkedHashMap() {{
            put(REQ_PARAM_CAPTCHA_KEY, captchaKey);
            put(REQ_PARAM_CAPTCHA_IMAGE, specCaptcha.toBase64());
        }};
    }

    public void validateImageCaptcha(String captchaKey, String captchaValue) {
        Assert.isTrue(StringUtils.isNotBlank(captchaKey) && StringUtils.isNotBlank(captchaValue), "验证码不正确");
        Cache.ValueWrapper valueWrapper = captchaCache.get(captchaKey);
        if (valueWrapper == null) {
            throw new AuthenticationServiceException("验证码不正确");
        }

        if (!StringUtils.equals((String) valueWrapper.get(), captchaValue.trim().toLowerCase())) {
            throw new AuthenticationServiceException("验证码不正确");
        }
    }

}
