package com.squad20.sistema_climbe.domain.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailSenderService {

    private final ObjectProvider<JavaMailSender> javaMailSenderProvider;

    public EmailSenderService(ObjectProvider<JavaMailSender> javaMailSenderProvider) {
        this.javaMailSenderProvider = javaMailSenderProvider;
    }

    public void dispatch(String toEmail, String subject, String body) {
        JavaMailSender javaMailSender = javaMailSenderProvider.getIfAvailable();
        if (javaMailSender == null) {
            throw new IllegalStateException("Nenhuma configuracao SMTP encontrada para envio de e-mail.");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        javaMailSender.send(message);
        log.info("E-mail enviado para {} com o assunto: {}", toEmail, subject);
    }

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            dispatch(toEmail, subject, body);
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail para {}: {}", toEmail, e.getMessage());
        }
    }
}
