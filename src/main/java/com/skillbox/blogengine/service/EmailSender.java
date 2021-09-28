package com.skillbox.blogengine.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class EmailSender {
    private final static Logger LOGGER = LogManager.getLogger(EmailSender.class);

    private static final String FROM = "noreply@skillboxBlog.com";
    private JavaMailSender emailSender;

    public EmailSender(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendSimpleMessage(String to, String subject, String text) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        try {
            helper.setText(text, true); //true for html type
            helper.setTo(to);
            helper.setSubject(subject); // TODO не понимаю, почему остается email из конфига
            helper.setFrom(new InternetAddress(FROM));
            emailSender.send(message);
        } catch (MessagingException e) {
            LOGGER.error("Got error while sending email to {}", to, e);
        }
    }
}
