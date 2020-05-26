package li.fyun.commons.messaging.mail;

import javax.mail.MessagingException;
import java.io.File;

public interface MailService {

    void sendSimpleMail(String mailTo, String subject, String content);
    void sendMimeMail(String mailTo, String subject, String content, File... files) throws MessagingException;
    void sendInlineResourceMail(String mailTo, String subject, String content, File[] files);

}
