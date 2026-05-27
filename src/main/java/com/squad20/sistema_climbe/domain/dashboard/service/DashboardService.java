package com.squad20.sistema_climbe.domain.dashboard.service;

import com.squad20.sistema_climbe.domain.contract.repository.ContractRepository;
import com.squad20.sistema_climbe.domain.dashboard.dto.DashboardStatsDTO;
import com.squad20.sistema_climbe.domain.enterprise.repository.EnterpriseRepository;
import com.squad20.sistema_climbe.domain.proposal.entity.Proposal;
import com.squad20.sistema_climbe.domain.proposal.entity.ProposalStatus;
import com.squad20.sistema_climbe.domain.proposal.repository.ProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProposalRepository proposalRepository;
    private final ContractRepository contractRepository;
    private final EnterpriseRepository enterpriseRepository;

    @Transactional(readOnly = true)
    public DashboardStatsDTO getStats() {
        List<Proposal> proposals = proposalRepository.findAll();
        LocalDate firstMonth = LocalDate.now().withDayOfMonth(1).minusMonths(5);
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MMM", Locale.forLanguageTag("pt-BR"));

        List<DashboardStatsDTO.ValueItem> monthlyProposals = IntStream.range(0, 6)
                .mapToObj(offset -> firstMonth.plusMonths(offset))
                .map(month -> new DashboardStatsDTO.ValueItem(
                        capitalize(month.format(monthFormat)),
                        proposals.stream()
                                .filter(proposal -> proposal.getCreatedAt() != null)
                                .filter(proposal -> proposal.getCreatedAt().toLocalDate().withDayOfMonth(1).equals(month))
                                .count(),
                        "#79C6C0"))
                .toList();

        List<DashboardStatsDTO.ValueItem> statusDistribution = Arrays.stream(ProposalStatus.values())
                .map(status -> new DashboardStatsDTO.ValueItem(
                        status.name(),
                        proposals.stream().filter(proposal -> status == proposal.getStatus()).count(),
                        statusColor(status)))
                .filter(item -> item.value() > 0)
                .toList();

        List<DashboardStatsDTO.ActivityItem> activities = proposals.stream()
                .filter(proposal -> proposal.getCreatedAt() != null)
                .sorted((left, right) -> right.getCreatedAt().compareTo(left.getCreatedAt()))
                .limit(5)
                .map(proposal -> new DashboardStatsDTO.ActivityItem(
                        "Proposta #" + proposal.getId(),
                        proposal.getCreatedAt().toLocalDate().toString(),
                        proposal.getStatus() != null ? proposal.getStatus().name() : "RECEIVED",
                        "bg-climbe-primary/10 text-climbe-primary"))
                .toList();

        return DashboardStatsDTO.builder()
                .totalProposals(proposals.size())
                .totalContracts(contractRepository.count())
                .totalClients(enterpriseRepository.count())
                .totalRevenue("R$ 0,00")
                .monthlyRevenue(monthlyProposals)
                .proposalStatusDistribution(statusDistribution)
                .recentActivities(activities)
                .build();
    }

    private String statusColor(ProposalStatus status) {
        return switch (status) {
            case COMMERCIAL_PROPOSAL_APPROVED, READY_FOR_NEXT_STAGE -> "#79C6C0";
            case COMMERCIAL_PROPOSAL_REJECTED, PENDING_ADJUSTMENTS -> "#888888";
            default -> "#111111";
        };
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        return value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1);
    }
}
