package li.fyun.commons.security.service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import li.fyun.commons.file.FileInfo;
import li.fyun.commons.security.YunwenSecurityException;
import li.fyun.commons.security.entity.Role;
import li.fyun.commons.security.entity.UserAccount;
import li.fyun.commons.security.repository.UserAccountRepository;
import li.fyun.commons.security.repository.RoleRepository;
import li.fyun.commons.core.utils.RegexValidators;
import li.fyun.commons.file.FileStorage;
import li.fyun.commons.file.impl.SimpleMultipartFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@Slf4j
@SuppressWarnings("unchecked")
public class SecurityService {

    public static final String CHANNEL_EMAIL = "email";

    @Resource
    private RoleRepository roleRepository;
    @Resource
    private UserAccountRepository userAccountRepository;
    @Resource
    private CaptchaService captchaService;
    @Resource
    private FileStorage fileStorage;
    @Resource
    private SecurityMessageService securityMessageService;

    @Value("${app.security.account.lock-duration}")
    private long lockedDuration = 86400000; // default 24 hours
    @Value("${app.security.account.login-fail-try-times}")
    private int loginFailTryTimes = 5; // default 5 times
    @Value("${app.security.account.new-account-disabled}")
    private boolean newAccountDisabled;

    // --- 角色 --- //
    @Transactional
    public Role authorizeRole(Long id, Long[] permissions) {
        Role role = roleRepository.getOne(id);
        if (role == null) {
            throw new ObjectNotFoundException(id, "Role");
        }

        role.setPermissions(permissions);
        return roleRepository.save(role);
    }

    @Transactional
    public void register(UserAccount userAccount) {
        if (userAccountRepository.findByUsername(userAccount.getUsername()) != null) {
            throw new YunwenSecurityException("用户名已被使用");
        }

        if (StringUtils.isNotBlank(userAccount.getMobile()) && userAccountRepository.findByMobile(userAccount.getMobile()) != null) {
            throw new YunwenSecurityException("手机号已被使用");
        }

        if (StringUtils.isNotBlank(userAccount.getEmail()) && userAccountRepository.findByEmail(userAccount.getEmail()) != null) {
            throw new YunwenSecurityException("email已被使用");
        }

        userAccount.setRegisterTime(new Date());
        userAccount.setAccountDisabled(newAccountDisabled);
        userAccountRepository.save(userAccount);
    }

    public boolean userAccountExists(String word) {
        Assert.isTrue(StringUtils.isNotBlank(word), "无效参数");
        return userAccountRepository.findByUsernameOrMobileOrEmail(word, word, word) != null;
    }

    public UserAccount findByUsername(String username) {
        return userAccountRepository.findByUsername(username);
    }

    public UserAccount findByEmail(String email) {
        return userAccountRepository.findByEmail(email);
    }

    public UserAccount findByMobile(String mobile) {
        return userAccountRepository.findByMobile(mobile);
    }

    @Transactional
    public Map<String, Object> resetPasswordToken(String dest, String channel) {
        boolean isEmailChannel = StringUtils.equals(CHANNEL_EMAIL, channel);
        UserAccount userAccount;
        if (isEmailChannel) {
            if (!RegexValidators.isEmail(dest)) {
                throw new YunwenSecurityException(dest + "email格式不正确");
            }
            userAccount = userAccountRepository.findByEmail(dest);
        } else {
            if (!RegexValidators.isMobile(dest)) {
                throw new YunwenSecurityException(dest + "手机号不正确");
            }
            userAccount = userAccountRepository.findByMobile(dest);
        }
        if (userAccount == null) {
            throw new YunwenSecurityException("未找到绑定的用户账号");
        }

        String token = isEmailChannel ?
                RandomStringUtils.randomAlphanumeric(8) :
                captchaService.generateSmsCaptcha(userAccount.getMobile());
        userAccount.setResetPasswordToken(token);
        userAccount.setResetPasswordRequestTime(new Date());
        userAccountRepository.save(userAccount);

        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("userId", "" + userAccount.getId());
        tokenMap.put("token", token);

        if (isEmailChannel) {
            // 发送通知邮件
            String content = "您请求重置密码，验证码是：\n\n" + token + "\n\n如果不是您的请求，请忽略此邮件。";
            log.debug("发布密码重置通知事件");
            securityMessageService.sendSimpleMail(userAccount.getEmail(), "密码重置通知", content);
        } else {
            captchaService.sendSmsCaptcha(userAccount.getMobile(), token);
        }

        return tokenMap;
    }

    @Transactional
    public void resetPassword(Long userId, String token, String password) {
        UserAccount userAccount = userAccountRepository.getOne(userId);
        if (userAccount == null) {
            throw new YunwenSecurityException("user (" + userId + ") 用户不存在");
        }
        if (!StringUtils.equals(token, userAccount.getResetPasswordToken())) {
            throw new YunwenSecurityException("重置密码令牌不正确");
        }
        // TODO 验证密码规则
        userAccount.flushPassword(password);
        userAccountRepository.save(userAccount);
    }

    @Transactional
    public void forceFlushPassword(Long userId, String password) {
        UserAccount userAccount = userAccountRepository.getOne(userId);
        if (userAccount == null) {
            throw new YunwenSecurityException("user (" + userId + ") 用户不存在");
        }
        userAccount.flushPassword(password);
        userAccountRepository.save(userAccount);

        // 发送通知邮件
        String content = "Dear " + userAccount.getUsername() + ":\n\n";
        content += "您的登录密码已被重置为：\n\n" + password + "\n\n";
        content += "请妥善保管密码。";

        log.debug("发布密码重置通知事件");
        securityMessageService.sendSimpleMail(userAccount.getEmail(), "密码重置通知", content);
    }

    @Transactional
    public void changeNickname(String username, String nickname) {
        UserAccount userAccount = userAccountRepository.findByUsername(username);
        if (userAccount == null) {
            throw new YunwenSecurityException("user (" + username + ") 用户不存在");
        }
        userAccount.setNickname(nickname);
        userAccountRepository.save(userAccount);
    }

    @Transactional
    public UserAccount login(String username, String password) throws AuthenticationException {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("非法参数");
        }

        UserAccount userAccount = userAccountRepository.findByUsername(username);
        if (userAccount == null) {
            throw new UsernameNotFoundException("该用户不存在！");
        }

        // 密码错误超过次数则暂时锁定
        if (!userAccount.validatePassword(password)) {
            userAccount.setLastTryTimeForLogin(new Date());
            int errorCount = userAccount.getErrorCount() + 1;
            userAccount.setErrorCount(errorCount);
            if (errorCount >= loginFailTryTimes) {
                userAccount.setLocked(true);
            }
            userAccountRepository.save(userAccount);
            throw new BadCredentialsException("密码不正确");
        }

        userAccount.validateStatus(lockedDuration);

        userAccount.setLocked(false);
        userAccount.setErrorCount(0);
        userAccount.setLastLoginTime(new Date());
        userAccountRepository.save(userAccount);

        userAccount.loadPrivilegesEagerly();
        return userAccount;
    }

    @Transactional
    public void enableUserAccount(Long userId) {
        userAccountRepository.enable(userId);
    }

    @Transactional
    public void disableUserAccount(Long userId) {
        userAccountRepository.disable(userId);
    }

    @Transactional
    public UserAccount authorizeUserAccount(Long id, Long[] roleIds) {
        UserAccount userAccount = userAccountRepository.getOne(id);
        if (userAccount == null) {
            throw new ObjectNotFoundException(id, "用户账号");
        }

        userAccount.setRoles(roleIds);
        return userAccountRepository.save(userAccount);
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        Assert.isTrue(StringUtils.isNotBlank(oldPassword), "旧密码不能为空");

        if (!RegexValidators.isPassword(newPassword)) {
            throw new IllegalArgumentException("密码不符合格式要求");
        }
        UserAccount userAccount = userAccountRepository.findByUsername(username);
        if (userAccount == null) {
            throw new UsernameNotFoundException("user (" + username + ") 该用户不存在");
        }

        if (!userAccount.validatePassword(oldPassword)) {
            throw new IllegalArgumentException("旧密码不正确");
        }

        userAccount.flushPassword(newPassword);
        userAccountRepository.save(userAccount);
    }

    public Iterable<UserAccount> searchUserAccounts(String keyword, Pageable pageable) {
        if (StringUtils.isNotBlank(keyword)) {
            String term = "%" + keyword + "%";
            if (pageable == null) {
                return userAccountRepository.findByKeyword(term);
            } else {
                return userAccountRepository.findByKeyword(term, pageable);
            }
        } else {
            if (pageable == null) {
                return userAccountRepository.findAll();
            } else {
                return userAccountRepository.findAll(pageable);
            }
        }
    }

    @Transactional
    public void changeMobile(String username, String mobile, String captcha) {
        if (!RegexValidators.isMobile(mobile)) {
            throw new YunwenSecurityException(mobile + " 手机号不正确");
        }
        if (!captchaService.validateSmsCaptcha(mobile, captcha)) {
            throw new YunwenSecurityException("无效的验证码");
        }

        UserAccount userAccount = userAccountRepository.findByUsername(username);
        if (userAccount == null) {
            throw new YunwenSecurityException("user (" + username + ") 用户不存在");
        }
        UserAccount otherUserAccount = userAccountRepository.findByMobile(mobile);
        if (otherUserAccount == null) {
            userAccount.setMobile(mobile);
            userAccountRepository.save(userAccount);
        } else if (!username.equals(otherUserAccount.getUsername())) {
            throw new YunwenSecurityException("手机号 (" + mobile + ") 已被使用");
        }
    }

    @Transactional
    public void changeEmail(Long userId, String key) {
        UserAccount userAccount = userAccountRepository.findOne(userId);
        if (userAccount == null) {
            throw new YunwenSecurityException("用户不存在");
        }

        String email = captchaService.getEmailByCachedKey(key, userAccount.getUsername());
        UserAccount otherUserAccount = userAccountRepository.findByEmail(email);
        if (otherUserAccount == null) {
            userAccount.setEmail(email);
            userAccountRepository.save(userAccount);
        } else if (!userId.equals(otherUserAccount.getId())) {
            throw new YunwenSecurityException("email (" + email + ") 已被使用");
        }
    }

    @Transactional
    public void clearMobile(String username) {
        UserAccount userAccount = userAccountRepository.findByUsername(username);
        if (userAccount == null) {
            throw new YunwenSecurityException("user (" + username + ") 用户不存在");
        }
        userAccount.setMobile(null);
        userAccountRepository.save(userAccount);
    }

    @Transactional
    public void clearEmail(String username) {
        UserAccount userAccount = userAccountRepository.findByUsername(username);
        if (userAccount == null) {
            throw new YunwenSecurityException("user (" + username + ") 用户不存在");
        }
        userAccount.setEmail(null);
        userAccountRepository.save(userAccount);
    }

    @Transactional
    public UserAccount updateAvatar(String username, String avatar) throws IOException {
        Assert.isTrue(RegexValidators.matches(UserAccount.AVATAR_IMAGE_REGEX, avatar), "内容不符合要求");

        UserAccount userAccount = userAccountRepository.findByUsername(username);
        if (userAccount == null) {
            throw new YunwenSecurityException("user (" + username + ") 用户不存在");
        }
        FileInfo avatarFile = fileStorage.put(new SimpleMultipartFile(userAccount.getId() + ".png",
                userAccount.getId() + ".png",
                "image/png",
                Base64Utils.decodeFromString(StringUtils.substringAfter(avatar, "base64,"))));
        userAccount.setAvatar(avatarFile.getFilePath());
        return userAccountRepository.save(userAccount);
    }

    public void sendEmailBindingCaptcha(String email, HttpServletRequest request) {
        if (!RegexValidators.isEmail(email)) {
            throw new YunwenSecurityException(email + " 邮箱格式不正确");
        }

        UserAccount userAccount = this.findByEmail(email);
        if (userAccount != null) {
            throw new YunwenSecurityException("电子邮箱已经被绑定");
        }

        captchaService.sendEmailCaptcha(email,
                StringUtils.replace(request.getRequestURL().toString(), "mail-captcha", "bind-email"));
    }

    public void sendMobileBindingCaptcha(String mobile) {
        if (!RegexValidators.isMobile(mobile)) {
            throw new YunwenSecurityException(mobile + " 手机号不正确");
        }

        UserAccount userAccount = this.findByMobile(mobile);
        if (userAccount != null) {
            throw new YunwenSecurityException("手机号已经被绑定");
        }

        captchaService.sendSmsCaptcha(mobile);
    }

}
