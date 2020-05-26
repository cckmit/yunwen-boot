package li.fyun.commons.security.controller;

import li.fyun.commons.core.controller.RestResourceController;
import li.fyun.commons.core.jpa.RepositoryUtils;
import li.fyun.commons.security.repository.UserAccountRepository;
import li.fyun.commons.security.entity.UserAccount;
import li.fyun.commons.security.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/security/user-accounts")
public class UserAccountController extends RestResourceController<UserAccount, Long> {

    @Resource
    private SecurityService securityService;

    public UserAccountController(@Autowired UserAccountRepository repository) {
        super(repository);
    }

    @PutMapping(value = "/{id}/enable")
    public void enable(@PathVariable Long id) {
        securityService.enableUserAccount(id);
    }

    @PutMapping(value = "/{id}/disable")
    public void disable(@PathVariable Long id) {
        securityService.disableUserAccount(id);
    }

    @PostMapping(value = "/{id}/authorize")
    public UserAccount authorize(@PathVariable Long id, Long[] roles) {
        return securityService.authorizeUserAccount(id, roles);
    }

    @Override
    public Map<String, List> delete(Long[] id) throws NoSuchMethodException {
        throw new NoSuchMethodException();
    }

    @GetMapping(value = "/search")
    public Iterable<UserAccount> search(String keyword, String page, String size) {
        return securityService.searchUserAccounts(keyword, RepositoryUtils.buildPageRequest(page, size));
    }

}
