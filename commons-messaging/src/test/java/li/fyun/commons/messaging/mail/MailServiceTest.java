package li.fyun.commons.messaging.mail;

import li.fyun.commons.messaging.SmsTestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmsTestApplication.class)
public class MailServiceTest {

    @Autowired
    MailService mailService;

    @Test
    public void testSend(){
        mailService.sendSimpleMail("fyunli@qq.com", "测试邮件", "我觉得生活还是美好的");
    }

}