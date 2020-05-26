package li.fyun.commons.core.jpa;

import li.fyun.commons.core.utils.SnowflakeSequence;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class SnowflakeIdentifierGenerator implements IdentifierGenerator {

    public static final SnowflakeSequence SEQUENCE = new SnowflakeSequence();

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return SEQUENCE.nextId();
    }

}
