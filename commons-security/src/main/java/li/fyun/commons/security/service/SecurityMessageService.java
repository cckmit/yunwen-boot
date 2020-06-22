package li.fyun.commons.security.service;

import java.util.Map;

public interface SecurityMessageService  {

    void sendSimpleMail(String mailTo, String subject, String content);

    void sendSms(String mobile, String template, Map<String, String> params);

}
