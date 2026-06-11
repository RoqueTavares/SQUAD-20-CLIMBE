package com.squad20.sistema_climbe.domain.dashboard.service;

import com.squad20.sistema_climbe.domain.contract.entity.Contract;
import com.squad20.sistema_climbe.domain.contract.repository.ContractRepository;
import com.squad20.sistema_climbe.domain.dashboard.dto.DashboardStatsDTO;
import com.squad20.sistema_climbe.domain.enterprise.repository.EnterpriseRepository;
import com.squad20.sistema_climbe.domain.proposal.entity.Proposal;
import com.squad20.sistema_climbe.domain.proposal.entity.ProposalStatus;
import com.squad20.sistema_climbe.domain.proposal.repository.ProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
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
        List<Contract> contracts = contractRepository.findAll();
        LocalDate firstMonth = LocalDate.now().withDayOfMonth(1).minusMonths(5);
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MMM", Locale.forLanguageTag("pt-BR"));

        BigDecimal totalRevenue = contracts.stream()
                .map(Contract::getTotalValue)
                .filter(value -> value != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<DashboardStatsDTO.ValueItem> monthlyRevenue = IntStream.range(0, 6)
                .mapToObj(offset -> firstMonth.plusMonths(offset))
                .map(month -> new DashboardStatsDTO.ValueItem(
                        capitalize(month.format(monthFormat)),
                        contracts.stream()
                                .filter(contract -> contract.getStartDate() != null)
                                .filter(contract -> contract.getTotalValue() != null)
                                .filter(contract -> contract.getStartDate().withDayOfMonth(1).equals(month))
                                .map(Contract::getTotalValue)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                .longValue(),
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
                .totalContracts(contracts.size())
                .totalClients(enterpriseRepository.count())
                .totalRevenue(formatCurrency(totalRevenue))
                .monthlyRevenue(monthlyRevenue)
                .proposalStatusDistribution(statusDistribution)
                .recentActivities(activities)
                .build();
    }

    private String formatCurrency(BigDecimal value) {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(value);
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
