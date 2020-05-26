package li.fyun.commons.security.repository;

import li.fyun.commons.core.jpa.BaseRepository;
import li.fyun.commons.security.entity.Permission;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends BaseRepository<Permission, Long> {

    Permission findByCode(String code);

}
