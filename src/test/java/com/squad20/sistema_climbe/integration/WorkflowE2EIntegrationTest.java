package com.squad20.sistema_climbe.integration;

import com.squad20.sistema_climbe.domain.contract.dto.ContractCreateRequest;
import com.squad20.sistema_climbe.domain.contract.dto.ContractPatchRequest;
import com.squad20.sistema_climbe.domain.contract.service.ContractService;
import com.squad20.sistema_climbe.domain.document.dto.DocumentRequirementCreateRequest;
import com.squad20.sistema_climbe.domain.document.dto.DocumentRequirementPatchRequest;
import com.squad20.sistema_climbe.domain.document.entity.DocumentRequirement;
import com.squad20.sistema_climbe.domain.document.entity.DocumentRequirementStatus;
import com.squad20.sistema_climbe.domain.document.entity.DocumentType;
import com.squad20.sistema_climbe.domain.document.repository.DocumentRequirementRepository;
import com.squad20.sistema_climbe.domain.document.service.DocumentRequirementService;
import com.squad20.sistema_climbe.domain.enterprise.dto.AddressCreateRequest;
import com.squad20.sistema_climbe.domain.enterprise.dto.EnterpriseCreateRequest;
import com.squad20.sistema_climbe.domain.enterprise.entity.Enterprise;
import com.squad20.sistema_climbe.domain.enterprise.repository.EnterpriseRepository;
import com.squad20.sistema_climbe.domain.enterprise.service.EnterpriseService;
import com.squad20.sistema_climbe.domain.notification.dto.NotificationCreateRequest;
import com.squad20.sistema_climbe.domain.notification.service.NotificationService;
import com.squad20.sistema_climbe.domain.proposal.dto.ProposalCreateRequest;
import com.squad20.sistema_climbe.domain.proposal.dto.ProposalPatchRequest;
import com.squad20.sistema_climbe.domain.proposal.entity.Proposal;
import com.squad20.sistema_climbe.domain.proposal.entity.ProposalStatus;
import com.squad20.sistema_climbe.domain.proposal.repository.ProposalRepository;
import com.squad20.sistema_climbe.domain.proposal.service.ProposalService;
import com.squad20.sistema_climbe.domain.report.dto.ReportCreateRequest;
import com.squad20.sistema_climbe.domain.report.entity.Report;
import com.squad20.sistema_climbe.domain.report.entity.ReportStatus;
import com.squad20.sistema_climbe.domain.report.repository.ReportRepository;
import com.squad20.sistema_climbe.domain.report.service.ReportService;
import com.squad20.sistema_climbe.domain.service.entity.OfferedService;
import com.squad20.sistema_climbe.domain.service.repository.ServiceRepository;
import com.squad20.sistema_climbe.domain.user.entity.Role;
import com.squad20.sistema_climbe.domain.user.entity.User;
import com.squad20.sistema_climbe.domain.user.repository.UserRepository;
import com.squad20.sistema_climbe.exception.BadRequestException;
import com.squad20.sistema_climbe.messaging.EmailPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WorkflowE2EIntegrationTest {

    @Autowired private EnterpriseService enterpriseService;
    @Autowired private EnterpriseRepository enterpriseRepository;
    @Autowired private ProposalService proposalService;
    @Autowired private ProposalRepository proposalRepository;
    @Autowired private ContractService contractService;
    @Autowired private DocumentRequirementService documentRequirementService;
    @Autowired private DocumentRequirementRepository documentRequirementRepository;
    @Autowired private com.squad20.sistema_climbe.domain.document.repository.DocumentRepository documentRepository;
    @Autowired private ReportService reportService;
    @Autowired private ReportRepository reportRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ServiceRepository serviceRepository;

    @MockitoBean private NotificationService notificationService;
    @MockitoBean private EmailPublisher emailPublisher;

    private User authUser;
    private User seniorAnalyst;

    @BeforeEach
    void setUp() {
        authUser = new User();
        authUser.setEmail("user.test@climbe.com");
        authUser.setPasswordHash("123456");
        authUser.setFullName("User Test");
        authUser.setCpf("12345678901");
        authUser.setPhone("11999999999");
        authUser.setStatus("ATIVO");
        authUser.setRole(Role.CMO);
        authUser = userRepository.save(authUser);

        seniorAnalyst = new User();
        seniorAnalyst.setEmail("senior@climbe.com");
        seniorAnalyst.setPasswordHash("123456");
        seniorAnalyst.setFullName("Senior Analyst");
        seniorAnalyst.setCpf("10987654321");
        seniorAnalyst.setPhone("11988888888");
        seniorAnalyst.setStatus("ATIVO");
        seniorAnalyst.setRole(Role.ANALISTA_SENIOR);
        seniorAnalyst = userRepository.save(seniorAnalyst);
    }

    private Long setupEnterprise() {
        EnterpriseCreateRequest req = EnterpriseCreateRequest.builder()
                .legalName("Empresa Teste E2E")
                .tradeName("Empresa Teste")
                .cnpj("12.345.678/0001-99")
                .email("contato@empresa.com")
                .phone("11912345678")
                .representativeName("Carlos")
                .representativeCpf("111.222.333-44")
                .representativePhone("11912345678")
                .address(AddressCreateRequest.builder()
                        .street("Rua 1").number("100").neighborhood("Centro")
                        .city("São Paulo").state("SP").zipCode("01000-000")
                        .build())
                .build();
        return enterpriseService.save(req).getId();
    }

    @Test
    @DisplayName("Cenário 1: Caminho Feliz E2E (Fase 1 a Fase 3)")
    void testHappyPathE2E() {
        // [Fase 1]: Criar Empresa e Proposta
        Long enterpriseId = setupEnterprise();

        ProposalCreateRequest propReq = ProposalCreateRequest.builder()
                .enterpriseId(enterpriseId)
                .userId(authUser.getId())
                .build();
        Long proposalId = proposalService.save(propReq).getId();

        // Tramitação da Proposta
        proposalService.update(proposalId, ProposalPatchRequest.builder().status(ProposalStatus.IN_TRIAGE).build());
        proposalService.update(proposalId, ProposalPatchRequest.builder().status(ProposalStatus.ELIGIBLE).build());
        proposalService.markCommercialProposalSubmitted(proposalId);
        proposalService.update(proposalId, ProposalPatchRequest.builder().status(ProposalStatus.COMMERCIAL_PROPOSAL_APPROVED).build());

        // Criar Contrato e Assinar
        Long contractId = contractService.save(ContractCreateRequest.builder()
                .proposalId(proposalId)
                .build()).getId();

        contractService.update(contractId, ContractPatchRequest.builder().status("DIGITALLY_SIGNED").build());

        // Assert Fase 1: Proposta deve estar READY_FOR_NEXT_STAGE
        Proposal proposal = proposalRepository.findById(proposalId).orElseThrow();
        assertEquals(ProposalStatus.READY_FOR_NEXT_STAGE, proposal.getStatus());

        // [Fase 2]: Documentação
        documentRequirementService.createRequirements(proposalId, new DocumentRequirementCreateRequest());
        List<DocumentRequirement> docs = documentRequirementRepository.findByProposal_Id(proposalId);
        assertFalse(docs.isEmpty(), "Deveria ter gerado requisitos de documento");

        Enterprise enterprise = enterpriseRepository.findById(enterpriseId).orElseThrow();
        for (DocumentRequirement doc : docs) {
            com.squad20.sistema_climbe.domain.document.entity.Document d = new com.squad20.sistema_climbe.domain.document.entity.Document();
            d.setDocumentType(doc.getDocumentType().name());
            d.setEnterprise(enterprise);
            d = documentRepository.save(d);
            
            doc.setDocument(d);
            documentRequirementRepository.save(doc);

            documentRequirementService.patchRequirement(doc.getId(), DocumentRequirementPatchRequest.builder()
                    .status(DocumentRequirementStatus.APPROVED)
                    .build());
        }

        // Assert Fase 2: Analista Senior Notificado e Avanço para Fase 3 (Set Deadline)
        verify(notificationService, atLeastOnce()).save(argThat(n -> n.getType().equals("ALL_DOCUMENTS_APPROVED")));
        
        contractService.setExecutionDeadline(contractId, LocalDate.now().plusDays(30));
        contractService.assignTeam(contractId, List.of(seniorAnalyst.getId()), "LIDER");

        // [Fase 3]: Relatório
        Long reportId = reportService.save(ReportCreateRequest.builder()
                .contractId(contractId)
                .build()).getId();

        reportService.submitForReview(reportId);
        Report report = reportRepository.findById(reportId).orElseThrow();
        assertEquals(ReportStatus.IN_REVIEW, report.getStatus());

        // Revisor aprova
        reportService.reviewReport(reportId, seniorAnalyst.getId(), true);
        report = reportRepository.findById(reportId).orElseThrow();
        assertEquals(ReportStatus.APPROVED, report.getStatus());

        // Simula Conclusão da Reunião
        assertDoesNotThrow(() -> reportService.completePresentation(contractId));
    }

    @Test
    @DisplayName("Cenário 2: Falha - Rejeição Prematura (Tentar assinar contrato de proposta rejeitada)")
    void testPrematureRejection() {
        Long enterpriseId = setupEnterprise();

        Long proposalId = proposalService.save(ProposalCreateRequest.builder()
                .enterpriseId(enterpriseId)
                .userId(authUser.getId())
                .build()).getId();

        proposalService.update(proposalId, ProposalPatchRequest.builder().status(ProposalStatus.IN_TRIAGE).build());
        proposalService.update(proposalId, ProposalPatchRequest.builder().status(ProposalStatus.ELIGIBLE).build());
        proposalService.markCommercialProposalSubmitted(proposalId);
        proposalService.update(proposalId, ProposalPatchRequest.builder().status(ProposalStatus.COMMERCIAL_PROPOSAL_REJECTED).build());

        assertThrows(BadRequestException.class, () -> {
            contractService.save(ContractCreateRequest.builder()
                    .proposalId(proposalId)
                    .build());
        }, "Não deve permitir criar contrato para proposta rejeitada");
    }

    @Test
    @DisplayName("Cenário 3: Falha - Ciclo de Documentação Inválida")
    void testInvalidDocumentation() {
        Long enterpriseId = setupEnterprise();
        Long proposalId = proposalService.save(ProposalCreateRequest.builder()
                .enterpriseId(enterpriseId).userId(authUser.getId()).build()).getId();

        proposalService.update(proposalId, ProposalPatchRequest.builder().status(ProposalStatus.IN_TRIAGE).build());
        proposalService.update(proposalId, ProposalPatchRequest.builder().status(ProposalStatus.ELIGIBLE).build());
        proposalService.markCommercialProposalSubmitted(proposalId);
        proposalService.update(proposalId, ProposalPatchRequest.builder().status(ProposalStatus.COMMERCIAL_PROPOSAL_APPROVED).build());

        documentRequirementService.createRequirements(proposalId, new DocumentRequirementCreateRequest());
        List<DocumentRequirement> docs = documentRequirementRepository.findByProposal_Id(proposalId);
        
        // Simular reprovação de documento
        DocumentRequirement firstDoc = docs.get(0);
        documentRequirementService.patchRequirement(firstDoc.getId(), DocumentRequirementPatchRequest.builder()
                .status(DocumentRequirementStatus.NON_COMPLIANT)
                .rejectionReason("Documento ilegível ou incompleto")
                .build());

        // Deve disparar emailPublisher
        verify(emailPublisher, atLeastOnce()).publish(anyString(), any());
    }

    @Test
    @DisplayName("Cenário 4: Recorrência CFO/FS")
    void testCfoFsRecurrence() {
        // Criar serviço CFO
        OfferedService cfoService = new OfferedService();
        cfoService.setName("CFO");
        cfoService = serviceRepository.save(cfoService);

        Long enterpriseId = setupEnterprise();
        Enterprise enterprise = enterpriseRepository.findById(enterpriseId).orElseThrow();
        enterprise.setServices(new java.util.HashSet<>(Set.of(cfoService)));
        enterpriseRepository.save(enterprise);

        Long proposalId = proposalService.save(ProposalCreateRequest.builder()
                .enterpriseId(enterpriseId).userId(authUser.getId()).build()).getId();

        // ... avançar tudo até contrato assinado
        proposalService.update(proposalId, ProposalPatchRequest.builder().status(ProposalStatus.IN_TRIAGE).build());
        proposalService.update(proposalId, ProposalPatchRequest.builder().status(ProposalStatus.ELIGIBLE).build());
        proposalService.markCommercialProposalSubmitted(proposalId);
        proposalService.update(proposalId, ProposalPatchRequest.builder().status(ProposalStatus.COMMERCIAL_PROPOSAL_APPROVED).build());

        Long contractId = contractService.save(ContractCreateRequest.builder().proposalId(proposalId).build()).getId();
        contractService.update(contractId, ContractPatchRequest.builder().status("DIGITALLY_SIGNED").build());

        // Concluir apresentação do Relatório
        reportService.completePresentation(contractId);

        // TODO: Em uma implementação futura, "completePresentation" deve acionar "createRequirements"
        // Novamente gerando uma nova rodada para o próximo ciclo
        // verify(documentRequirementService).createRequirements(...) (Mockado internamente)
    }
}
