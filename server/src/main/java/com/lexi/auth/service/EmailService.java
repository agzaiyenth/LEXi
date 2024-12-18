package com.lexi.auth.service;

import com.lexi.auth.dto.MailBody;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javamailSender;

    public EmailService(JavaMailSender javamailSender) {
        this.javamailSender = javamailSender;
    }
    public void sendSimpleMail(MailBody mailbody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailbody.to());
        message.setFrom("noreply@lexi.com");
        message.setSubject(mailbody.subject());
        message.setText(mailbody.text());
        javamailSender.send(message);


    }
}
