package li.fyun.commons.security.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import li.fyun.commons.core.jpa.AbstractAuditable;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.http.HttpMethod;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Entity
@Table(name = "sys_granted_resource")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class GrantedResource extends AbstractAuditable {

    public static final String PERM_PERMIT_ALL = "NONE";
    public static final String PERM_IS_AUTHENTICATED = "AUTH";
    public static final String[] ALLOW_METHODS = {"GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"};

    @NotBlank
    @Column(nullable = false)
    private String url;
    @Column(length = 20)
    private String method;
    @NotBlank
    @Column(length = 20, nullable = false)
    private String perm;
    @Column(length = 6)
    private int orderNumber;

    @JsonIgnore
    public HttpMethod getHttpMethod() {
        HttpMethod method = null;
        if (StringUtils.isNotBlank(this.getMethod())) {
            try {
                method = HttpMethod.valueOf(this.getMethod());
            } catch (Exception ex) {
                // EX
            }
        }
        return method;
    }

}
