package com.squad20.sistema_climbe.domain.contract.service;

import com.squad20.sistema_climbe.domain.contract.entity.Contract;
import com.squad20.sistema_climbe.domain.enterprise.entity.Enterprise;
import com.squad20.sistema_climbe.domain.proposal.entity.Proposal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class PdfGeneratorServiceTest {

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Test
    void testGenerateContractPdf() throws IOException {
        // 1. Arrange: Create mock data
        Enterprise enterprise = new Enterprise();
        enterprise.setLegalName("Empresa Mock Ltda");
        enterprise.setCnpj("12.345.678/0001-99");

        Proposal proposal = new Proposal();
        proposal.setEnterprise(enterprise);

        Contract contract = new Contract();
        contract.setId(999L);
        contract.setStartDate(LocalDate.now());
        contract.setProposal(proposal);

        // 2. Act: Generate the PDF
        byte[] pdfBytes = pdfGeneratorService.generateContractPdf(contract);

        // 3. Assert: Verify the PDF was generated
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0, "O array de bytes do PDF não deveria estar vazio");

        // 4. Salvar em arquivo local para visualização real
        String filePath = "build/contrato-teste.pdf";
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(pdfBytes);
        }
        System.out.println("====== PDF DE TESTE GERADO COM SUCESSO ======");
        System.out.println("Arquivo salvo em: " + filePath);
        System.out.println("Tamanho do arquivo: " + pdfBytes.length + " bytes");
    }
}
