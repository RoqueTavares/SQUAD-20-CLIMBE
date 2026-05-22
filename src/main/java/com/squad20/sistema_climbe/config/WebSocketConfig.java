package com.squad20.sistema_climbe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
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

    @Value("${app.websocket.broker-relay.client-login:guest}")
    private String brokerRelayClientLogin;

    @Value("${app.websocket.broker-relay.client-passcode:guest}")
    private String brokerRelayClientPasscode;

    @Value("${app.websocket.broker-relay.system-login:guest}")
    private String brokerRelaySystemLogin;

    @Value("${app.websocket.broker-relay.system-passcode:guest}")
    private String brokerRelaySystemPasscode;

    @Value("${app.websocket.broker-relay.virtual-host:/}")
    private String brokerRelayVirtualHost;

    @Value("${app.websocket.broker-relay.user-destination-broadcast:/topic/unresolved-user-destinations}")
    private String userDestinationBroadcast;

    @Value("${app.websocket.broker-relay.user-registry-broadcast:/topic/simp-user-registry}")
    private String userRegistryBroadcast;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");

        if (brokerRelayEnabled) {
            config.enableStompBrokerRelay("/topic", "/queue")
                    .setRelayHost(brokerRelayHost)
                    .setRelayPort(brokerRelayPort)
                    .setClientLogin(brokerRelayClientLogin)
                    .setClientPasscode(brokerRelayClientPasscode)
                    .setSystemLogin(brokerRelaySystemLogin)
                    .setSystemPasscode(brokerRelaySystemPasscode)
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
}
