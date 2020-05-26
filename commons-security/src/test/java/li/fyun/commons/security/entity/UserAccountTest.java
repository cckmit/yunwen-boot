package li.fyun.commons.security.entity;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserAccountTest {

    static Logger logger = LoggerFactory.getLogger(UserAccountTest.class);

    @Test
    public void flushPassword() {
        UserAccount userAccount = new UserAccount();
        userAccount.setPassword("admin");
        userAccount.flushPassword("Feb20200202");
        logger.info("password {}", userAccount.getPassword());
        logger.info("getPasswordSalt {}", userAccount.getPasswordSalt());
    }
}