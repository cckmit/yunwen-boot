package li.fyun.commons.security.service;

import li.fyun.commons.security.entity.UserAccount;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class SpringJpaAuditor implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(UserAccount.loggedInUser());
    }

}
