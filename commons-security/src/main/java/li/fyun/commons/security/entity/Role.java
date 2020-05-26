package li.fyun.commons.security.entity;

import li.fyun.commons.core.jpa.AbstractAuditable;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "sys_role")
@Getter
@Setter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Role extends AbstractAuditable {

    @NotBlank
    @Column(length = 20, nullable = false, unique = true)
    private String code; // 角色编码
    @NotBlank
    @Column(length = 100, nullable = false)
    private String name;// 角色名称
    private String remark;// 备注
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_role_permission", joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id")})
    private Set<Permission> permissions;

    public String[] getIgnoreProperties() {
        return ArrayUtils.addAll(super.getIgnoreProperties(), new String[]{"permissions"});
    }

    public boolean hasPermission(Long permissionId) {
        if (CollectionUtils.isEmpty(permissions)) {
            return false;
        }
        Permission permission = new Permission();
        permission.setId(permissionId);
        return permissions.contains(permission);
    }

    public void setPermissions(Long[] permissionIds) {
        if (permissions != null) {
            permissions.clear();
        }
        for (Long permissionId : permissionIds) {
            this.addPermission(permissionId);
        }
    }

    public void addPermission(Long permissionId) {
        synchronized ("ADD_PERMISSION") {
            if (permissions == null) {
                permissions = new LinkedHashSet<>();
            }
        }
        Permission permission = new Permission();
        permission.setId(permissionId);
        permissions.add(permission);
    }

}
