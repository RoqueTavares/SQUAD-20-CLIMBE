package com.squad20.sistema_climbe.messaging;

import com.rabbitmq.client.Channel;
import com.squad20.sistema_climbe.config.RabbitConfig;
import com.squad20.sistema_climbe.domain.notification.service.EmailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private static final int MAX_ATTEMPTS = 3;
    private static final long BACKOFF_INITIAL_MS = 1_000L;
    private static final long BACKOFF_MULTIPLIER = 2L;

    private final EmailSenderService emailSenderService;

    @RabbitListener(queues = RabbitConfig.EMAIL_QUEUE)
    public void consume(EmailMessage payload, Message rawMessage, Channel channel) throws IOException {
        long deliveryTag = rawMessage.getMessageProperties().getDeliveryTag();
        String routingKey = rawMessage.getMessageProperties().getReceivedRoutingKey();

        try {
            sendWithRetry(payload, routingKey);
            channel.basicAck(deliveryTag, false);
        } catch (Exception ex) {
            log.error("Falha definitiva ao processar e-mail (routingKey={}, to={}). Encaminhando para DLQ.",
                    routingKey, payload.getTo(), ex);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    private void sendWithRetry(EmailMessage payload, String routingKey) throws InterruptedException {
        long backoff = BACKOFF_INITIAL_MS;
        Exception lastError = null;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                emailSenderService.dispatch(payload.getTo(), payload.getSubject(), payload.getBody());
                if (attempt > 1) {
                    log.info("E-mail entregue após {} tentativa(s) (routingKey={}).", attempt, routingKey);
                }
                return;
            } catch (Exception ex) {
                lastError = ex;
                log.warn("Tentativa {}/{} de envio falhou (routingKey={}): {}",
                        attempt, MAX_ATTEMPTS, routingKey, ex.getMessage());
                if (attempt < MAX_ATTEMPTS) {
                    Thread.sleep(backoff);
                    backoff *= BACKOFF_MULTIPLIER;
                }
            }
        }

        throw new EmailDeliveryException(
                "Esgotadas " + MAX_ATTEMPTS + " tentativas de envio de e-mail.", lastError);
    }

    static class EmailDeliveryException extends RuntimeException {
        EmailDeliveryException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
