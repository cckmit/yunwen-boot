package li.fyun.commons.security.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import li.fyun.commons.core.jpa.AbstractAuditable;
import li.fyun.commons.core.utils.CodecUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.*;

import li.fyun.commons.core.utils.RegexValidators;

@Entity
@Table(name = "sys_user_account")
@Getter
@Setter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class UserAccount extends AbstractAuditable {

    public static final String ANONYMOUS_USER = "anonymousUser";
    public static final String AVATAR_IMAGE_REGEX = "^(data:image/)(jpeg|png)(;base64,)(.*)";

    private static final String[] EXTRA_IGNORES = new String[]{
            "password", "passwordSalt", "roles", "registerTime", "lastTryTimeForLogin",
            "errorCount", "lastLoginTime", "lastLoginIpAddress", "version", "locked",
            "resetPasswordToken", "resetPasswordRequestTime", "internetIp", "accountDisabled"};

    private static final int PASSWORD_HASH_ITERATIONS = 1024;

    @NotBlank
    @Pattern(regexp = RegexValidators.REGEX_USERNAME)
    @Column(length = 100, nullable = false, unique = true)
    private String username; // 用户名

    @JsonIgnore
    @Column(length = 128, nullable = false)
    private String password; // 密码

    @JsonIgnore
    @Column(length = 50, nullable = false)
    private String passwordSalt; // 盐值

    @Column(length = 100)
    @Length(min = 2, max = 50)
    private String nickname; // 昵称

    @Column(length = 50, unique = true)
    private String mobile; // 手机

    @Pattern(regexp = RegexValidators.REGEX_EMAIL)
    @Length(max = 100)
    @Column(length = 100, unique = true)
    private String email; // 电子邮箱

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date registerTime; // 注册时间

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastTryTimeForLogin; // 锁定时间

    @Column(nullable = false)
    private int errorCount; // 登录出错次数

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastLoginTime; // 最后登录时间

    @Column(length = 50)
    private String lastLoginIpAddress; // 最后登录IP

    @Version
    private int version;

    private boolean locked;

    @JsonIgnore
    private String resetPasswordToken;
    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    private Date resetPasswordRequestTime;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_user_account_role", joinColumns = {@JoinColumn(name = "user_account_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    Set<Role> roles;

    @Transient
    @Pattern(regexp = RegexValidators.REGEX_PASSWORD)
    private String typePassword;
    @Transient
    private String internetIp; // 外网IP
    private boolean accountDisabled;

    private String avatar = "/avatar2.jpg";

    public String getNickname() {
        if (StringUtils.isBlank(nickname)) {
            return this.username;
        } else {
            return nickname;
        }
    }

    /**
     * 刷新密码并加盐
     */
    public void flushPassword(String newPassword) {
        this.setPasswordSalt(RandomStringUtils.random(32, true, true));
        String hexPassword = CodecUtils.passwordEncode(newPassword,
                this.getUsername() + this.getPasswordSalt(), PASSWORD_HASH_ITERATIONS);
        this.setPassword(hexPassword);
    }

    /**
     * 验证密码
     */
    public boolean validatePassword(String password) {
        String hexPassword = CodecUtils.passwordEncode(password,
                this.getUsername() + this.getPasswordSalt(), PASSWORD_HASH_ITERATIONS);
        return StringUtils.equals(hexPassword, this.getPassword());
    }

    public boolean hasRole(Long roleId) {
        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }
        Role adminRole = new Role();
        adminRole.setId(roleId);
        return roles.contains(adminRole);
    }

    public boolean hasPermission(Long permissionId) {
        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }
        for (Role role : roles) {
            if (role.hasPermission(permissionId)) {
                return true;
            }
        }
        return false;
    }

    public String[] getIgnoreProperties() {
        return ArrayUtils.addAll(super.getIgnoreProperties(), EXTRA_IGNORES);
    }

    public void setTypePassword(String typePassword) {
        // 新创建账号强制检查
        if (isNew() && StringUtils.isBlank(typePassword)) {
            throw new SecurityException("无效的登录密码");
        }
        if (StringUtils.isNotBlank(typePassword)) {
            this.typePassword = typePassword;
            this.flushPassword(typePassword);
        }
    }

    public void setRoles(Long[] roleIds) {
        if (roles != null) {
            roles.clear();
        }
        for (Long roleId : roleIds) {
            this.addRole(roleId);
        }
    }

    public void addRole(Long roleId) {
        synchronized ("ADD_PERMISSION") {
            if (roles == null) {
                roles = new LinkedHashSet<>();
            }
        }
        Role role = new Role();
        role.setId(roleId);
        roles.add(role);
    }

    public Set getAuthorizedRoles() {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptySet();
        }

        Set<Map<String, ?>> authorizedRoles = Sets.newLinkedHashSet();
        for (Role role : this.roles) {
            Map<String, Object> map = Maps.newLinkedHashMap();
            map.put("id", role.getId() + "");
            map.put("code", role.getCode());
            map.put("name", role.getName());
            authorizedRoles.add(map);
        }
        return authorizedRoles;
    }

    public Set<Permission> getAuthorizedPermissions() {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptySet();
        }

        Set<Permission> permissions = Sets.newLinkedHashSet();
        for (Role role : roles) {
            permissions.addAll(role.getPermissions());
        }
        return permissions;
    }

    public static String loggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth == null || auth.getPrincipal() == null) ?
                ANONYMOUS_USER :
                (String) auth.getPrincipal();
    }

    public void validateStatus(long lockDuration) {
        if (this.isAccountDisabled()) {
            throw new DisabledException("该用户被禁用！");
        }

        Date lastTryTime = this.getLastTryTimeForLogin();
        if (this.isLocked() && lastTryTime != null && new Date().getTime() - lastTryTime.getTime() < lockDuration) {
            throw new LockedException("该用户被锁定！");
        }
    }

    public void loadPrivilegesEagerly() {
        if (CollectionUtils.isNotEmpty(this.getRoles())) {
            for (Role role : this.getRoles()) {
                Hibernate.initialize(role);
                if (CollectionUtils.isNotEmpty(role.getPermissions())) {
                    for (Permission permission : role.getPermissions()) {
                        Hibernate.initialize(permission);
                    }
                }
            }
        }
    }

}
