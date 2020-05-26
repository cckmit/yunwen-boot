package li.fyun.commons.jwt;

import li.fyun.commons.security.service.SecurityService;
import li.fyun.commons.security.entity.UserAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import javax.annotation.Resource;
import java.util.ArrayList;

@Slf4j
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    @Resource
    private SecurityService securityService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if(authentication.getCredentials() == null){
            throw new IllegalArgumentException("请提供正确的登录信息");
        }

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        UserAccount userAccount = securityService.login(username, password);
        ArrayList<GrantedAuthority> authorities = GrantedAuthorityImpl.getGrantedAuthorities(userAccount);
        log.info("用户 {} 登录成功", username);
        // 生成令牌
        return new UsernamePasswordAuthenticationToken(username, userAccount.getId(), authorities);
    }

    // 是否可以提供输入类型的认证服务
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
