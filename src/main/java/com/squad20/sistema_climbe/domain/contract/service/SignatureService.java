package com.squad20.sistema_climbe.domain.contract.service;

import com.squad20.sistema_climbe.domain.contract.entity.Contract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class SignatureService {

    /**
     * MOCK implementation of an e-signature API integration (e.g., ZapSign, ClickSign).
     * In a real scenario, this would send an HTTP POST request to the provider's API
     * containing the pdfUrl and signer information, and return the document's external ID.
     */
    public String sendDocumentForSignature(Contract contract, String pdfUrl) {
        log.info("====== SIMULANDO INTEGRAÇÃO COM ZAPSIGN / CLICKSIGN ======");
        log.info("Enviando contrato ID {} para assinatura...", contract.getId());
        log.info("URL do PDF: {}", pdfUrl);
        log.info("Empresa: {}", contract.getProposal().getEnterprise().getLegalName());
        
        // Mocking an external ID returned by the API
        String mockExternalId = "ZAPSIGN_" + UUID.randomUUID().toString();
        log.info("Sucesso! ID Externo retornado: {}", mockExternalId);
        log.info("==========================================================");
        
        return mockExternalId;
    }
}
