package com.squad20.sistema_climbe.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class GoogleCloudStorageConfig {

    @Value("${gcp.credentials.file.path}")
    private String credentialsFilePath;

    @Bean
    @ConditionalOnMissingBean(Storage.class)
    public Storage googleCloudStorage() throws IOException {
        InputStream credentialsStream = null;

        if (credentialsFilePath.startsWith("classpath:")) {
            String path = credentialsFilePath.replace("classpath:", "");
            credentialsStream = getClass().getClassLoader().getResourceAsStream(path);

            if (credentialsStream == null) {
                System.out.println("[GCP Storage] Arquivo de credenciais não encontrado no classpath — usando Application Default Credentials (Cloud Run/ADC).");
            }
        } else {
            credentialsStream = new java.io.FileInputStream(credentialsFilePath);
        }

        if (credentialsStream == null) {
            // Cloud Run: usa ADC com a service account do ambiente + permissão iam.serviceAccountTokenCreator
            GoogleCredentials adcCredentials = GoogleCredentials.getApplicationDefault()
                    .createScoped("https://www.googleapis.com/auth/cloud-platform");
            return StorageOptions.newBuilder()
                    .setCredentials(adcCredentials)
                    .build()
                    .getService();
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
                .createScoped("https://www.googleapis.com/auth/cloud-platform");

        return StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
    }
}
