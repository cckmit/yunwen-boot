package li.fyun.commons.security.controller;

import li.fyun.commons.security.entity.UserAccount;
import li.fyun.commons.security.service.SecurityService;
import li.fyun.commons.core.controller.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping("/security/me")
@PreAuthorize("isAuthenticated()")
public class ProfileController extends BaseController {

    @Resource
    private SecurityService securityService;

    @PutMapping(value = "/change-nickname")
    public ResponseEntity changeNickname(String nickname) {
        String username = UserAccount.loggedInUser();
        securityService.changeNickname(username, nickname);
        return OK;
    }

    @GetMapping(value = "/profile")
    public UserAccount profile() {
        String username = UserAccount.loggedInUser();
        return securityService.findByUsername(username);
    }

    @PutMapping(value = "/change-password")
    public ResponseEntity changePassword(String oldPassword, String newPassword) {
        String username = UserAccount.loggedInUser();
        securityService.changePassword(username, oldPassword, newPassword);
        return OK;
    }

    @PutMapping(value = "/upload-avatar")
    public UserAccount uploadAvatar(String avatar) throws IOException {
        String username = UserAccount.loggedInUser();
        return securityService.updateAvatar(username, avatar);
    }

}
