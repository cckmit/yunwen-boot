package li.fyun.commons.messaging.mail.impl;

import li.fyun.commons.messaging.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Slf4j
@Component
public class JavaMailService implements MailService {

    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String mailFrom;

    /**
     * 发送简单邮件
     *
     * @param mailTo
     * @param subject
     * @param content
     */
    @Override
    public void sendSimpleMail(String mailTo, String subject, String content) {
        validate(mailTo, subject, content);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailFrom);
        mailMessage.setTo(mailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(content);
        mailSender.send(mailMessage);
        log.info("邮件已发送 {}", mailTo);
    }

    private void validate(String mailTo, String subject, String content) {
        Assert.hasText(mailTo, "无效的收件邮箱");
        Assert.hasText(subject, "邮件标题不能为空");
        Assert.hasText(content, "邮件内容不能为空");
    }

    /**
     * 发送富文本邮件，可带附件
     *
     * @param mailTo
     * @param subject
     * @param content
     * @param files
     * @throws MessagingException
     */
    @Override
    public void sendMimeMail(String mailTo, String subject, String content, File... files) throws MessagingException {
        validate(mailTo, subject, content);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(mailFrom);
        helper.setTo(mailTo);
        helper.setSubject(subject);
        helper.setText(content, true);

        if (ArrayUtils.isNotEmpty(files)) {
            for (File file : files) {
                helper.addAttachment(file.getName(), file);
            }
        }

        mailSender.send(message);
        log.info("邮件已发送 {}", mailTo);
    }

    /**
     * 发送内嵌静态资源邮件
     *
     * @param mailTo
     * @param subject
     * @param content
     * @param files
     */
    @Override
    public void sendInlineResourceMail(String mailTo, String subject, String content, File[] files) {
        validate(mailTo, subject, content);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailFrom);
            helper.setTo(mailTo);
            helper.setSubject(subject);
            helper.setText(content, true);

            if (ArrayUtils.isNotEmpty(files)) {
                for (File file : files) {
                    helper.addInline(RandomStringUtils.randomNumeric(12, 13), file);
                }
            }

            mailSender.send(message);
            log.info("嵌入静态资源的邮件已经发送 {}", mailTo);
        } catch (MessagingException e) {
            log.error("发送嵌入静态资源的邮件时发生异常！", e);
        }
    }

}
