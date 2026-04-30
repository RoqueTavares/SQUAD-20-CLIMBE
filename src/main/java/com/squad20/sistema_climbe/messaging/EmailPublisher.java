package com.squad20.sistema_climbe.messaging;

import com.squad20.sistema_climbe.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(String routingKey, EmailMessage message) {
        if (message == null || message.getTo() == null || message.getTo().isBlank()) {
            log.warn("Mensagem de e-mail descartada antes da publicação: destinatário ausente.");
            return;
        }
        rabbitTemplate.convertAndSend(RabbitConfig.EMAIL_EXCHANGE, routingKey, message);
        log.debug("E-mail publicado em {} para {}", routingKey, message.getTo());
    }
}
