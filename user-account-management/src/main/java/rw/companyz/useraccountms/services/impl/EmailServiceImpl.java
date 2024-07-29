package rw.companyz.useraccountms.services.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import rw.companyz.useraccountms.services.IEmailService;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements IEmailService {

    @Value("${spring.mail.username}")
    private String from;

    @Value("${frontend.url}")
    private String frontendURL;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendHtmlMessage(String to, String subject, String header, String text, String c2aLink, String c2aButton) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);

            if (c2aLink == null) {
                c2aLink = "#";
            } else {
                c2aLink = frontendURL + c2aLink;
            }

            String htmlContent = generateEmailContent(header, text, c2aLink, c2aButton);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to);

            // TODO: improve this to keep track of the failed emails and retry sending them later
        }
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
