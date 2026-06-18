package com.squad20.sistema_climbe.domain.contract.service;

import com.squad20.sistema_climbe.domain.contract.entity.Contract;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PdfGeneratorService {

    private final TemplateEngine templateEngine;

    public byte[] generateContractPdf(Contract contract) {
        Context context = new Context();
        context.setVariable("contractId", contract.getId() != null ? contract.getId().toString() : "N/A");
        context.setVariable("companyName", contract.getProposal().getEnterprise().getLegalName());
        context.setVariable("cnpj", contract.getProposal().getEnterprise().getCnpj());
        
        String startDateStr = contract.getStartDate() != null ? 
                contract.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : 
                "A definir";
        context.setVariable("startDate", startDateStr);
        
        String html = templateEngine.process("contract-template", context);
        
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(os);
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF do contrato", e);
        }
    }
}
