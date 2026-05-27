package com.squad20.sistema_climbe.api;

import com.squad20.sistema_climbe.SistemaClimbeApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SistemaClimbeApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
class DashboardApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getStatsReturnsDashboardStructure() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProposals").isNumber())
                .andExpect(jsonPath("$.totalContracts").isNumber())
                .andExpect(jsonPath("$.totalClients").isNumber())
                .andExpect(jsonPath("$.monthlyRevenue").isArray())
                .andExpect(jsonPath("$.proposalStatusDistribution").isArray())
                .andExpect(jsonPath("$.recentActivities").isArray());
    }
}
