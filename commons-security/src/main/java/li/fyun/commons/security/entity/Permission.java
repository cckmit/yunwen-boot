package li.fyun.commons.security.entity;

import li.fyun.commons.core.jpa.AbstractAuditable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "sys_permission")
@Getter
@Setter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Permission extends AbstractAuditable {

    @NotBlank
    @Column(length = 20, nullable = false, unique = true)
    private String code;
    @NotBlank
    @Column(length = 100, nullable = false, unique = true)
    private String name;

}
