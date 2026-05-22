package com.squad20.sistema_climbe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Arrays;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${cors.allowed-origins:http://localhost:5173,http://localhost:3000}")
    private String allowedOriginsConfig;

    @Value("${app.websocket.broker-relay.enabled:false}")
    private boolean brokerRelayEnabled;

    @Value("${app.websocket.broker-relay.host:localhost}")
    private String brokerRelayHost;

    @Value("${app.websocket.broker-relay.port:61613}")
    private int brokerRelayPort;

    @Value("${app.websocket.broker-relay.client-login:}")
    private String brokerRelayClientLogin;

    @Value("${app.websocket.broker-relay.client-passcode:}")
    private String brokerRelayClientPasscode;

    @Value("${app.websocket.broker-relay.system-login:}")
    private String brokerRelaySystemLogin;

    @Value("${app.websocket.broker-relay.system-passcode:}")
    private String brokerRelaySystemPasscode;

    @Value("${app.websocket.broker-relay.virtual-host:/}")
    private String brokerRelayVirtualHost;

    @Value("${app.websocket.broker-relay.user-destination-broadcast:/topic/unresolved-user-destinations}")
    private String userDestinationBroadcast;

    @Value("${app.websocket.broker-relay.user-registry-broadcast:/topic/simp-user-registry}")
    private String userRegistryBroadcast;

    @Value("${app.websocket.broker-relay.system-heartbeat-send-interval:10000}")
    private long systemHeartbeatSendInterval;

    @Value("${app.websocket.broker-relay.system-heartbeat-receive-interval:10000}")
    private long systemHeartbeatReceiveInterval;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");

        if (brokerRelayEnabled) {
            validateBrokerRelayCredentials();

            config.enableStompBrokerRelay("/topic", "/queue")
                    .setRelayHost(brokerRelayHost)
                    .setRelayPort(brokerRelayPort)
                    .setClientLogin(brokerRelayClientLogin)
                    .setClientPasscode(brokerRelayClientPasscode)
                    .setSystemLogin(brokerRelaySystemLogin)
                    .setSystemPasscode(brokerRelaySystemPasscode)
                    .setSystemHeartbeatSendInterval(systemHeartbeatSendInterval)
                    .setSystemHeartbeatReceiveInterval(systemHeartbeatReceiveInterval)
                    .setVirtualHost(brokerRelayVirtualHost)
                    .setUserDestinationBroadcast(userDestinationBroadcast)
                    .setUserRegistryBroadcast(userRegistryBroadcast);
            return;
        }

        config.enableSimpleBroker("/topic", "/queue");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-notifications")
                .setAllowedOriginPatterns(parseAllowedOrigins())
                .withSockJS();
    }

    private String[] parseAllowedOrigins() {
        return Arrays.stream(allowedOriginsConfig.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toArray(String[]::new);
    }

    private void validateBrokerRelayCredentials() {
        if (!StringUtils.hasText(brokerRelayClientLogin)
                || !StringUtils.hasText(brokerRelayClientPasscode)
                || !StringUtils.hasText(brokerRelaySystemLogin)
                || !StringUtils.hasText(brokerRelaySystemPasscode)) {
            throw new IllegalStateException("Broker relay habilitado exige credenciais STOMP explícitas.");
        }
    }
}
