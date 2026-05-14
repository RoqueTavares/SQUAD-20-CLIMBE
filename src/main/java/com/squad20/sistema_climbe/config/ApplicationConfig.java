package com.squad20.sistema_climbe.config;

import com.squad20.sistema_climbe.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository repository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Profile("dev")
    public org.springframework.boot.CommandLineRunner initDataSeeder(UserRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (repository.findByEmail("admin@climbe.com.br").isEmpty()) {
                com.squad20.sistema_climbe.domain.user.entity.User admin = com.squad20.sistema_climbe.domain.user.entity.User.builder()
                        .fullName("Administrador CEO")
                        .email("admin@climbe.com.br")
                        .cpf("00000000000")
                        .passwordHash(passwordEncoder.encode("admin123"))
                        .status("ATIVO")
                        .role(com.squad20.sistema_climbe.domain.user.entity.Role.CEO)
                        .build();
                repository.save(admin);
                System.out.println("====== SEED: Usuário CEO criado com sucesso (admin@climbe.com.br / admin123) ======");
            }
        };
    }
}
