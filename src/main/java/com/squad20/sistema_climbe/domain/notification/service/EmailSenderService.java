package com.squad20.sistema_climbe.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender javaMailSender;

    public void dispatch(String toEmail, String subject, String body) {
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
