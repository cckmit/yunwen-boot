package li.fyun.commons.security.service;

import li.fyun.commons.messaging.mail.MailService;
import li.fyun.commons.messaging.sms.SmsService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class SecurityMessageService  {

    @Resource
    private SmsService smsService;
    @Resource
    private MailService mailService;

    @Async
    public void sendSimpleMail(String mailTo, String subject, String content) {
        mailService.sendSimpleMail(mailTo, subject, content);
    }

    @Async
    public void sendSms(String mobile, String template, Map<String, String> params){
        smsService.send(mobile, template, params);
    }

}
