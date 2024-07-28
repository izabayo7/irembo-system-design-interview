package rw.companyz.useraccountms.services.impl;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import rw.companyz.useraccountms.services.IEmailService;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements IEmailService {

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendHtmlMessage(String to, String subject, String header, String text, String c2aLink, String c2aButton) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);

        String htmlContent = generateEmailContent(header, text, c2aLink, c2aButton);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    public String generateEmailContent(String header, String text, String c2aLink, String c2aButton) {
        Context context = new Context();
        context.setVariable("header", header);
        context.setVariable("text", text);
        context.setVariable("c2aLink", c2aLink);
        context.setVariable("c2aButton", c2aButton);

        return templateEngine.process("email-template", context);
    }

}
