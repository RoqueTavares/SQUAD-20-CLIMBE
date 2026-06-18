package com.squad20.sistema_climbe.domain.report.service;

import com.squad20.sistema_climbe.domain.contract.entity.Contract;
import com.squad20.sistema_climbe.domain.contract.repository.ContractRepository;
import com.squad20.sistema_climbe.domain.proposal.entity.Proposal;
import com.squad20.sistema_climbe.domain.enterprise.entity.Enterprise;
import com.squad20.sistema_climbe.domain.report.entity.Report;
import com.squad20.sistema_climbe.domain.report.entity.ReportStatus;
import com.squad20.sistema_climbe.domain.report.mapper.ReportMapper;
import com.squad20.sistema_climbe.domain.report.repository.ReportRepository;
import com.squad20.sistema_climbe.domain.user.entity.Role;
import com.squad20.sistema_climbe.domain.user.entity.User;
import com.squad20.sistema_climbe.domain.user.repository.UserRepository;
import com.squad20.sistema_climbe.domain.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock private ReportRepository reportRepository;
    @Mock private ContractRepository contractRepository;
    @Mock private UserRepository userRepository;
    @Mock private NotificationService notificationService;
    @Mock private ReportMapper reportMapper;

    @InjectMocks private ReportService service;

    @Test
    void testSubmitForReview() {
        Report report = new Report();
        report.setId(1L);
        Contract contract = new Contract();
        contract.setId(2L);
        report.setContract(contract);

        User senior = new User();
        senior.setId(99L);

        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(reportRepository.save(any())).thenReturn(report);
        when(userRepository.findByRole(Role.ANALISTA_SENIOR)).thenReturn(List.of(senior));

        service.submitForReview(1L);

        verify(reportRepository).save(argThat(r -> r.getStatus() == ReportStatus.IN_REVIEW));
        verify(notificationService).save(any());
    }

    @Test
    void testReviewReportApproved() {
        Report report = new Report();
        report.setId(1L);
        Contract contract = new Contract();
        contract.setId(2L);
        report.setContract(contract);

        User reviewer = new User();
        reviewer.setId(5L);

        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(userRepository.findById(5L)).thenReturn(Optional.of(reviewer));
        when(reportRepository.save(any())).thenReturn(report);

        service.reviewReport(1L, 5L, true);

        verify(reportRepository).save(argThat(r -> r.getStatus() == ReportStatus.APPROVED && r.getReviewer().equals(reviewer)));
    }
}
