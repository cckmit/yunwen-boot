package li.fyun.commons.security.controller;

import li.fyun.commons.security.entity.Permission;
import li.fyun.commons.security.entity.Role;
import li.fyun.commons.core.controller.RestResourceController;
import li.fyun.commons.security.repository.RoleRepository;
import li.fyun.commons.security.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/security/roles")
public class RoleController extends RestResourceController<Role, Long> {

    @Resource
    private SecurityService securityService;

    public RoleController(@Autowired RoleRepository repository) {
        super(repository);
    }

    @PostMapping(value = "/{id}/authorize")
    public Role authorize(@PathVariable Long id, Long[] permissions) {
        return securityService.authorizeRole(id, permissions);
    }

    @GetMapping(value = "/exists")
    public boolean exists(String code) {
        Role role = ((RoleRepository) repository).findByCode(code);
        return role != null;
    }

    @GetMapping(value = "/as-perm", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Permission> roles() {
        List<Role> roles = repository.findAll();
        List<Permission> permissions = new ArrayList<>();
        for (Role role : roles) {
            Permission permission = new Permission();
            permission.setId(role.getId());
            permission.setCode("ROLE_" + role.getCode());
            permission.setName(role.getName());
            permissions.add(permission);
        }
        return permissions;
    }

}
