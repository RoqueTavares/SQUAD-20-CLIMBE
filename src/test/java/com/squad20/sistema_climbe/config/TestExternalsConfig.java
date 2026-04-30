package com.squad20.sistema_climbe.config;

import com.google.cloud.storage.Storage;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

/**
 * Stubs de infra externa em perfil "test": nenhum teste fala com RabbitMQ,
 * Google OAuth ou Google Cloud Storage de verdade.
 */
@Configuration
@Profile("test")
public class TestExternalsConfig {

    @Bean
    @Primary
    public ConnectionFactory testConnectionFactory() {
        return Mockito.mock(ConnectionFactory.class);
    }

    @Bean
    @Primary
    public RabbitTemplate testRabbitTemplate() {
        return Mockito.mock(RabbitTemplate.class);
    }

    @Bean
    @Primary
    public ClientRegistrationRepository testClientRegistrationRepository() {
        return Mockito.mock(ClientRegistrationRepository.class);
    }

    @Bean
    @Primary
    public OAuth2AuthorizedClientService testAuthorizedClientService() {
        return Mockito.mock(OAuth2AuthorizedClientService.class);
    }

    @Bean
    @Primary
    public Storage testGoogleCloudStorage() {
        return Mockito.mock(Storage.class);
    }
}
