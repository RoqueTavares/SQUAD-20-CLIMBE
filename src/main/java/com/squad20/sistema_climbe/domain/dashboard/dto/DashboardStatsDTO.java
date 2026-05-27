package com.squad20.sistema_climbe.domain.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardStatsDTO {

    private long totalProposals;
    private long totalContracts;
    private long totalClients;
    private String totalRevenue;
    private List<ValueItem> monthlyRevenue;
    private List<ValueItem> proposalStatusDistribution;
    private List<ActivityItem> recentActivities;

    public record ValueItem(String name, long value, String color) {
    }

    public record ActivityItem(String name, String time, String status, String color) {
    }
}
