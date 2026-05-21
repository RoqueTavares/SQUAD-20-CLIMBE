package com.squad20.sistema_climbe.domain.document.service;

import com.squad20.sistema_climbe.domain.document.dto.DocumentRequirementPatchRequest;
import com.squad20.sistema_climbe.domain.document.entity.DocumentRequirement;
import com.squad20.sistema_climbe.domain.document.entity.DocumentRequirementStatus;
import com.squad20.sistema_climbe.domain.document.entity.DocumentType;
import com.squad20.sistema_climbe.domain.document.mapper.DocumentRequirementMapper;
import com.squad20.sistema_climbe.domain.document.repository.DocumentRequirementRepository;
import com.squad20.sistema_climbe.domain.enterprise.entity.Enterprise;
import com.squad20.sistema_climbe.domain.proposal.entity.Proposal;
import com.squad20.sistema_climbe.domain.proposal.repository.ProposalRepository;
import com.squad20.sistema_climbe.domain.user.entity.Role;
import com.squad20.sistema_climbe.domain.user.entity.User;
import com.squad20.sistema_climbe.domain.user.repository.UserRepository;
import com.squad20.sistema_climbe.domain.notification.service.NotificationService;
import com.squad20.sistema_climbe.messaging.EmailPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentRequirementServiceTest {

    @Mock private DocumentRequirementRepository documentRequirementRepository;
    @Mock private ProposalRepository proposalRepository;
    @Mock private UserRepository userRepository;
    @Mock private DocumentRequirementMapper documentRequirementMapper;
    @Mock private EmailPublisher emailPublisher;
    @Mock private NotificationService notificationService;

    @InjectMocks private DocumentRequirementService service;

    private DocumentRequirement documentRequirement;
    private Proposal proposal;

    @BeforeEach
    void setUp() {
        Enterprise enterprise = new Enterprise();
        enterprise.setId(1L);
        enterprise.setEmail("test@test.com");

        proposal = new Proposal();
        proposal.setId(10L);
        proposal.setEnterprise(enterprise);

        documentRequirement = new DocumentRequirement();
        documentRequirement.setId(100L);
        documentRequirement.setProposal(proposal);
        documentRequirement.setStatus(DocumentRequirementStatus.PENDING);
        documentRequirement.setDocumentType(DocumentType.CNPJ);
    }

    @Test
    void testPatchRequirement_Approved_And_Notifies_Seniors() {
        // Arrange
        DocumentRequirementPatchRequest request = new DocumentRequirementPatchRequest();
        request.setStatus(DocumentRequirementStatus.APPROVED);

        when(documentRequirementRepository.findById(100L)).thenReturn(Optional.of(documentRequirement));
        when(documentRequirementRepository.save(any(DocumentRequirement.class))).thenAnswer(i -> i.getArgument(0));

        // Simulando que todos os documentos estao APPROVED
        when(documentRequirementRepository.findByProposal_Id(10L)).thenReturn(List.of(documentRequirement));

        User senior = new User();
        senior.setId(99L);
        when(userRepository.findByRole(Role.ANALISTA_SENIOR)).thenReturn(List.of(senior));

        // Act
        service.patchRequirement(100L, request);

        // Assert
        verify(documentRequirementRepository).save(any(DocumentRequirement.class));
        verify(emailPublisher).publish(any(), any());
        verify(notificationService).save(any()); // Notifica Analista Sênior
    }
}
