package com.squad20.sistema_climbe.service;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.squad20.sistema_climbe.config.GoogleApiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleWorkspaceService {

    private final GoogleApiConfig googleApiConfig;

    @Value("${climbe.google.template-spreadsheet-id:YOUR_TEMPLATE_ID}")
    private String templateSpreadsheetId;

    public String createContractEnvironment(String adminRefreshToken, String contractTitle, List<String> analystEmails) throws GeneralSecurityException, IOException {
        Drive driveService = googleApiConfig.getDriveServiceFromRefreshToken(adminRefreshToken);

        // 1. Criar pasta raiz do contrato
        File folderMetadata = new File();
        folderMetadata.setName("Contrato - " + contractTitle);
        folderMetadata.setMimeType("application/vnd.google-apps.folder");

        File contractFolder = driveService.files().create(folderMetadata)
                .setFields("id")
                .execute();
        
        String folderId = contractFolder.getId();
        log.info("Pasta do Contrato criada no Google Drive com ID: {}", folderId);

        // 2. Copiar a planilha modelo para a nova pasta
        if (templateSpreadsheetId != null && !templateSpreadsheetId.equals("YOUR_TEMPLATE_ID")) {
            File copyMetadata = new File();
            copyMetadata.setName("Planilha Gerencial - " + contractTitle);
            copyMetadata.setParents(Collections.singletonList(folderId));
            
            // Requisito 6.c: Impedir download e cópia (apenas via API V3 do Drive)
            copyMetadata.setCopyRequiresWriterPermission(true);

            File copiedFile = driveService.files().copy(templateSpreadsheetId, copyMetadata)
                    .setFields("id, webViewLink")
                    .execute();
            
            String spreadsheetId = copiedFile.getId();
            log.info("Planilha clonada no Google Drive com ID: {}", spreadsheetId);

            // 3. Compartilhar com os analistas
            for (String email : analystEmails) {
                if (email != null && !email.isBlank()) {
                    Permission permission = new Permission()
                            .setType("user")
                            .setRole("writer")
                            .setEmailAddress(email);
                    
                    driveService.permissions().create(spreadsheetId, permission)
                            .setSendNotificationEmail(true)
                            .execute();
                }
            }

            return copiedFile.getWebViewLink();
        }

        return null;
    }
}
