package com.squad20.sistema_climbe.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Testes da API /api/proposals. GET lista e GET 404; POST exige enterpriseId e userId. */
class ProposalApiTest extends ApiTestBase {

    @Override
    protected String getBasePath() {
        return "/api/proposals";
    }

    @Override
    protected String getMinimalPostBody() {
        try {
            String enterpriseId = createEnterprise();
            UserFixture user = createUser();
            return "{\"enterpriseId\":" + enterpriseId + ",\"userId\":" + user.id() + ",\"createdAt\":\"2026-03-20T10:15:30\"}";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getPatchBody() {
        return "{\"status\":\"IN_TRIAGE\"}";
    }

    @Test
    @DisplayName("POST cria proposta com status inicial RECEIVED")
    void postCriaPropostaComStatusRecebida() throws Exception {
        String enterpriseId = createEnterprise();
        UserFixture user = createUser();

        mockMvc.perform(post(getBasePath())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"enterpriseId\":" + enterpriseId + ",\"userId\":" + user.id() + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("RECEIVED"));
    }

    @Test
    @DisplayName("PATCH permite avançar de RECEIVED para IN_TRIAGE")
    void patchAvancaRecebidaParaEmTriagem() throws Exception {
        ProposalFixture proposal = createProposal();

        mockMvc.perform(patch(getBasePath() + "/" + proposal.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"IN_TRIAGE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_TRIAGE"));
    }

    @Test
    @DisplayName("PATCH bloqueia avanço direto de RECEIVED para ELIGIBLE")
    void patchBloqueiaAvancoDiretoParaApta() throws Exception {
        ProposalFixture proposal = createProposal();

        mockMvc.perform(patch(getBasePath() + "/" + proposal.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"ELIGIBLE\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    @DisplayName("PATCH bloqueia ELIGIBLE quando empresa ainda tem dados pendentes")
    void patchBloqueiaAptaComDadosPendentes() throws Exception {
        ProposalFixture proposal = createProposal();

        mockMvc.perform(patch(getBasePath() + "/" + proposal.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"IN_TRIAGE\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(patch(getBasePath() + "/" + proposal.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"ELIGIBLE\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Dados iniciais incompletos")));
    }

    @Test
    @DisplayName("PATCH permite seguir para PENDING_ADJUSTMENTS e voltar para IN_TRIAGE")
    void patchPermitePendenciaEAjuste() throws Exception {
        ProposalFixture proposal = createProposal();

        mockMvc.perform(patch(getBasePath() + "/" + proposal.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"IN_TRIAGE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_TRIAGE"));

        mockMvc.perform(patch(getBasePath() + "/" + proposal.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"PENDING_ADJUSTMENTS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING_ADJUSTMENTS"));

        mockMvc.perform(patch(getBasePath() + "/" + proposal.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"IN_TRIAGE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_TRIAGE"));
    }

    @Test
    @DisplayName("PATCH permite avançar para ELIGIBLE quando dados mínimos da empresa estão completos")
    void patchPermiteAptaComDadosMinimosCompletos() throws Exception {
        String enterpriseId = createCompleteEnterprise();
        UserFixture user = createUser();

        String response = mockMvc.perform(post(getBasePath())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"enterpriseId\":" + enterpriseId + ",\"userId\":" + user.id() + "}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String proposalId = extractIdFromJson(response);

        mockMvc.perform(patch(getBasePath() + "/" + proposalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"IN_TRIAGE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_TRIAGE"));

        mockMvc.perform(patch(getBasePath() + "/" + proposalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"ELIGIBLE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ELIGIBLE"));
    }

    @Test
    @DisplayName("GET por empresa retorna propostas criadas")
    void getByEnterpriseReturnsCreatedProposal() throws Exception {
        ProposalFixture proposal = createProposal();

        mockMvc.perform(get(getBasePath() + "/enterprise/" + proposal.enterpriseId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].enterpriseId").value(Integer.parseInt(proposal.enterpriseId())));
    }

    @Test
    @DisplayName("GET por usuário retorna propostas criadas")
    void getByUserReturnsCreatedProposal() throws Exception {
        ProposalFixture proposal = createProposal();

        mockMvc.perform(get(getBasePath() + "/user/" + proposal.userId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(Integer.parseInt(proposal.userId())));
    }

    @Test
    @DisplayName("Soft delete não aparece em $.content da listagem geral")
    void softDeleteNaoApareceEmPageContent() throws Exception {
        ProposalFixture proposal = createProposal();

        mockMvc.perform(get(getBasePath() + "?size=200"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[?(@.id == " + proposal.id() + ")]").isNotEmpty());

        mockMvc.perform(delete(getBasePath() + "/" + proposal.id()))
            .andExpect(status().isNoContent());

        mockMvc.perform(get(getBasePath() + "/" + proposal.id()))
            .andExpect(status().isNotFound());

        mockMvc.perform(get(getBasePath() + "?size=200"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[?(@.id == " + proposal.id() + ")]").isEmpty());
    }

    @Test
    @DisplayName("Soft delete em proposta oculta na listagem por empresa")
    void softDeleteOcultaNaListagemPorEmpresa() throws Exception {
        ProposalFixture proposal = createProposal();

        mockMvc.perform(get(getBasePath() + "/enterprise/" + proposal.enterpriseId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.id == " + proposal.id() + ")]").isNotEmpty());

        mockMvc.perform(delete(getBasePath() + "/" + proposal.id()))
            .andExpect(status().isNoContent());

        mockMvc.perform(get(getBasePath() + "/enterprise/" + proposal.enterpriseId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.id == " + proposal.id() + ")]").isEmpty());
    }

    @Test
    @DisplayName("Soft delete em proposta oculta contrato associado")
    void softDeleteCascataContratoOcultaAposDelete() throws Exception {
        ContractFixture contract = createContract();

        mockMvc.perform(delete(getBasePath() + "/" + contract.proposalId()))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/contracts/" + contract.id()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Soft delete em proposta oculta relatório do contrato associado")
    void softDeleteCascataRelatorioOcultaAposDelete() throws Exception {
        ContractFixture contract = createContract();
        String reportId = createReport(contract.id());

        mockMvc.perform(delete(getBasePath() + "/" + contract.proposalId()))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/reports/" + reportId))
            .andExpect(status().isNotFound());
    }
}
