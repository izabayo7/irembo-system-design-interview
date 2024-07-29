package rw.companyz.useraccountms.services;

import jakarta.mail.MessagingException;

public interface IEmailService {
    void sendHtmlMessage(String to, String subject, String header, String text, String c2aLink, String c2aButton);
}
