package li.fyun.commons.security.controller;

import li.fyun.commons.security.entity.Permission;
import li.fyun.commons.security.repository.PermissionRepository;
import li.fyun.commons.core.controller.RestResourceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/security/permissions")
public class PermissionController extends RestResourceController<Permission, Long> {

    public PermissionController(@Autowired PermissionRepository repository) {
        super(repository);
    }

    @GetMapping(value = "/exists")
    public boolean exists(String code) {
        Permission permission = ((PermissionRepository) repository).findByCode(code);
        return permission != null;
    }

}
