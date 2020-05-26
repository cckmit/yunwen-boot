package li.fyun.commons.jwt;

import li.fyun.commons.security.entity.GrantedResource;
import li.fyun.commons.security.entity.Permission;
import li.fyun.commons.security.entity.Role;
import li.fyun.commons.security.entity.UserAccount;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Set;

@Data
public class GrantedAuthorityImpl implements GrantedAuthority {

    private String authority;

    public GrantedAuthorityImpl(String authority) {
        this.authority = authority;
    }

    public static ArrayList<GrantedAuthority> getGrantedAuthorities(UserAccount userAccount) {
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        Set<Role> roles = userAccount.getRoles();
        if (CollectionUtils.isNotEmpty(roles)) {
            // 遍历添加角色
            for (Role role : roles) {
                authorities.add(new GrantedAuthorityImpl("ROLE_" + role.getCode()));
                Set<Permission> perms = role.getPermissions();
                if (CollectionUtils.isNotEmpty(perms)) {
                    // 遍历添加权限
                    for (Permission perm : perms) {
                        authorities.add(new GrantedAuthorityImpl(perm.getCode()));
                    }
                }
            }
        }
        return authorities;
    }

    public static boolean validatePermission(Authentication authentication, String requiredPerm) {
        if (GrantedResource.PERM_PERMIT_ALL.equals(requiredPerm)) {
            return true;
        } else if (GrantedResource.PERM_IS_AUTHENTICATED.equals(requiredPerm)) {
            if (authentication == null || StringUtils.equals(UserAccount.ANONYMOUS_USER, (String) authentication.getPrincipal())) {
                new AccessDeniedException("Access Denied");
            } else {
                return true;
            }
        } else {
            for (GrantedAuthority ga : authentication.getAuthorities()) {
                if (StringUtils.equals(requiredPerm, ga.getAuthority())) {
                    return true;
                }
            }
        }
        return false;
    }

}
