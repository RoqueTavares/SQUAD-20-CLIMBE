package com.squad20.sistema_climbe.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DocumentRequirementApiTest extends ApiTestBase {

    @Override
    protected String getBasePath() {
        return "/api/documents/requirements";
    }

    @Override
    @Test
    @DisplayName("GET lista em requirements por proposta retorna 200")
    void getListaRetorna200() throws Exception {
        ProposalFixture proposal = createProposal();
        mockMvc.perform(get("/api/proposals/" + proposal.id() + "/documents/requirements"))
                .andExpect(status().isOk());
    }

    @Override
    @Test
    @DisplayName("GET por proposta inexistente retorna 404")
    void getPorIdInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/api/proposals/999999/documents/requirements"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST cria checklist com status inicial PENDING e deadline")
    void postCreatesChecklistWithPendingAndDeadline() throws Exception {
        ProposalFixture proposal = createProposal();

        mockMvc.perform(post("/api/proposals/" + proposal.id() + "/documents/requirements")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"documentTypes\":[\"CNPJ\",\"DRE\"],\"deadline\":\"2026-12-20\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].proposalId").value(Integer.parseInt(proposal.id())))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].deadline").value("2026-12-20"));
    }

    @Test
    @DisplayName("GET lista requirements por proposta")
    void getListsRequirementsByProposal() throws Exception {
        ProposalFixture proposal = createProposal();

        mockMvc.perform(post("/api/proposals/" + proposal.id() + "/documents/requirements")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"documentTypes\":[\"CNPJ\"]}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/proposals/" + proposal.id() + "/documents/requirements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].documentType").value("CNPJ"));
    }

    @Test
    @DisplayName("POST com proposta inexistente retorna 404")
    void postWithMissingProposalReturns404() throws Exception {
        mockMvc.perform(post("/api/proposals/999999/documents/requirements")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"documentTypes\":[\"CNPJ\"]}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET com proposta inexistente retorna 404")
    void getWithMissingProposalReturns404() throws Exception {
        mockMvc.perform(get("/api/proposals/999999/documents/requirements"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST duplicado na mesma proposta e tipo retorna 409")
    void postDuplicateTypeInSameProposalReturns409() throws Exception {
        ProposalFixture proposal = createProposal();

        mockMvc.perform(post("/api/proposals/" + proposal.id() + "/documents/requirements")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"documentTypes\":[\"CNPJ\"]}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/proposals/" + proposal.id() + "/documents/requirements")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"documentTypes\":[\"CNPJ\"]}"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PATCH para APPROVED sem documento retorna 400")
    void patchApprovedClearsReasonAndSetsValidatedAt() throws Exception {
        ProposalFixture proposal = createProposal();

        MvcResult created = mockMvc.perform(post("/api/proposals/" + proposal.id() + "/documents/requirements")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"documentTypes\":[\"CNPJ\"]}"))
                .andExpect(status().isCreated())
                .andReturn();

        String requirementId = extractIdFromJson(created.getResponse().getContentAsString());
        UserFixture validator = createUser();

        mockMvc.perform(patch("/api/documents/requirements/" + requirementId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"APPROVED\",\"rejectionReason\":\"motivo temporario\",\"validatedById\":" + validator.id() + "}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH NON_COMPLIANT sem rejectionReason retorna 400")
    void patchNonCompliantWithoutReasonReturns400() throws Exception {
        ProposalFixture proposal = createProposal();

        MvcResult created = mockMvc.perform(post("/api/proposals/" + proposal.id() + "/documents/requirements")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"documentTypes\":[\"CNPJ\"]}"))
                .andExpect(status().isCreated())
                .andReturn();

        String requirementId = extractIdFromJson(created.getResponse().getContentAsString());

        mockMvc.perform(patch("/api/documents/requirements/" + requirementId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"NON_COMPLIANT\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST com documentTypes nulo cria checklist com todos os tipos do enum")
    void postWithNullTypesCreatesAllEnumTypes() throws Exception {
        ProposalFixture proposal = createProposal();

        mockMvc.perform(post("/api/proposals/" + proposal.id() + "/documents/requirements")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"deadline\":\"2026-12-20\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    @DisplayName("POST com documentTypes vazio cria checklist com todos os tipos do enum")
    void postWithEmptyTypesCreatesAllEnumTypes() throws Exception {
        ProposalFixture proposal = createProposal();

        mockMvc.perform(post("/api/proposals/" + proposal.id() + "/documents/requirements")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"documentTypes\":[]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    @DisplayName("POST com documentTypes repetidos no payload retorna 400")
    void postWithDuplicateTypesInPayloadReturns400() throws Exception {
        ProposalFixture proposal = createProposal();

        mockMvc.perform(post("/api/proposals/" + proposal.id() + "/documents/requirements")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"documentTypes\":[\"CNPJ\",\"CNPJ\"]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST com documentTypes contendo null retorna 400")
    void postWithNullTypeInPayloadReturns400() throws Exception {
        ProposalFixture proposal = createProposal();

        mockMvc.perform(post("/api/proposals/" + proposal.id() + "/documents/requirements")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"documentTypes\":[\"CNPJ\",null]}"))
                .andExpect(status().isBadRequest());
    }
}
