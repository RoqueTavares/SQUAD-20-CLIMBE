package com.squad20.sistema_climbe.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI sistemaClimbeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema Climbe - API")
                        .description("Documentação da API REST do Sistema Climbe")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Squad20")));
    }
}
