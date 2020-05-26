package li.fyun.commons.security.repository;

import li.fyun.commons.security.entity.GrantedResource;
import li.fyun.commons.core.jpa.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrantedResourceRepository extends BaseRepository<GrantedResource, Long> {

    List<GrantedResource> findByUrlLike(String url);

    @Override
    @Query("select r from GrantedResource r order by r.orderNumber")
    List<GrantedResource> findAll();

}
