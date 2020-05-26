package li.fyun.commons.core.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.QueryHint;
import java.io.Serializable;

@NoRepositoryBean
public interface BaseRepository<E extends Serializable, ID extends Serializable>
        extends JpaRepository<E, ID>, JpaSpecificationExecutor<E> {

    @Override
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    E getOne(ID id);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    default E findOne(ID id) {
        return this.findById(id).orElse(null);
    }

}
