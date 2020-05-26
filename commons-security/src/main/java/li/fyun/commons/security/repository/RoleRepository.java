package li.fyun.commons.security.repository;

import li.fyun.commons.security.entity.Role;
import li.fyun.commons.core.jpa.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends BaseRepository<Role, Long> {

    Role findByCode(String code);

}
