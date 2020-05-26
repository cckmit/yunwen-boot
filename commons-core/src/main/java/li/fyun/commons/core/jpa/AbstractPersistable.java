package li.fyun.commons.core.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@Getter
@Setter
@MappedSuperclass
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "new"}, ignoreUnknown = true)
public abstract class AbstractPersistable implements Serializable {

    private static final long serialVersionUID = -5749819652366401893L;

    protected static final String[] DEFAULT_IGNORE_PROPERTIES = {
            "hibernateLazyInitializer", "handler", "new", "id"
    };

    @Id
    @GeneratedValue(generator = "twitter_snow_flake")
    @GenericGenerator(name = "twitter_snow_flake",
            strategy = "li.fyun.commons.core.jpa.SnowflakeIdentifierGenerator")
    @JsonSerialize(using = ToStringSerializer.class)
    protected Long id;

    public boolean isNew() {
        if (this.getId() == null || this.getId().longValue() < 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof AbstractPersistable)) return false;
        if(!this.getClass().isInstance(o)){
            return false;
        }

        AbstractPersistable that = (AbstractPersistable) o;
        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @JsonIgnore
    public String[] getIgnoreProperties() {
        return DEFAULT_IGNORE_PROPERTIES;
    }

}
