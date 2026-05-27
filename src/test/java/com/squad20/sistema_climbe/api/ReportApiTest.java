package com.squad20.sistema_climbe.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Testes da API /api/reports. GET lista e GET 404; POST exige contractId. */
class ReportApiTest extends ApiTestBase {

    @Override
    protected String getBasePath() {
        return "/api/reports";
    }

    @Override
    protected String getMinimalPostBody() {
        try {
            ContractFixture contract = createContract();
            return "{\"contractId\":" + contract.id() + ",\"pdfUrl\":\"https://teste.com/report.pdf\",\"sentAt\":\"2026-03-20T10:15:30\"}";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getPatchBody() {
        return "{\"pdfUrl\":\"https://teste.com/report-atualizado.pdf\"}";
    }

    @Test
    @DisplayName("GET por contrato retorna relatórios criados")
    void getByContractReturnsCreatedReport() throws Exception {
        ContractFixture contract = createContract();
        createResource(getBasePath(), "{\"contractId\":" + contract.id() + ",\"pdfUrl\":\"https://teste.com/report.pdf\"}");

        mockMvc.perform(get(getBasePath() + "/contract/" + contract.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].contractId").value(Integer.parseInt(contract.id())));
    }

    @Test
    @DisplayName("Soft delete não aparece em $.content da listagem geral")
    void softDeleteNaoApareceEmPageContent() throws Exception {
        ContractFixture contract = createContract();
        String reportId = createReport(contract.id());

        mockMvc.perform(get(getBasePath() + "?size=200"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[?(@.id == " + reportId + ")]").isNotEmpty());

        mockMvc.perform(delete(getBasePath() + "/" + reportId))
            .andExpect(status().isNoContent());

        mockMvc.perform(get(getBasePath() + "/" + reportId))
            .andExpect(status().isNotFound());

        mockMvc.perform(get(getBasePath() + "?size=200"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[?(@.id == " + reportId + ")]").isEmpty());
    }

    @Test
    @DisplayName("POST upload recebe PDF e vincula ao relatorio")
    void postUploadRecebePdf() throws Exception {
        ContractFixture contract = createContract();
        MockMultipartFile data = new MockMultipartFile(
                "data", "data.json", MediaType.APPLICATION_JSON_VALUE,
                ("{\"contractId\":" + contract.id() + "}").getBytes());
        MockMultipartFile file = new MockMultipartFile(
                "file", "resultado.pdf", MediaType.APPLICATION_PDF_VALUE, "%PDF-1.4".getBytes());

        mockMvc.perform(multipart(getBasePath() + "/upload").file(data).file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contractId").value(Integer.parseInt(contract.id())))
                .andExpect(jsonPath("$.pdfUrl", startsWith("relatorios_contrato_" + contract.id() + "/")));
    }

    @Test
    @DisplayName("POST upload recusa arquivo que nao seja PDF")
    void postUploadRecusaArquivoNaoPdf() throws Exception {
        ContractFixture contract = createContract();
        MockMultipartFile data = new MockMultipartFile(
                "data", "data.json", MediaType.APPLICATION_JSON_VALUE,
                ("{\"contractId\":" + contract.id() + "}").getBytes());
        MockMultipartFile file = new MockMultipartFile(
                "file", "resultado.txt", MediaType.TEXT_PLAIN_VALUE, "texto".getBytes());

        mockMvc.perform(multipart(getBasePath() + "/upload").file(data).file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET view retorna URL externa previamente cadastrada")
    void getViewRetornaUrlCadastrada() throws Exception {
        ContractFixture contract = createContract();
        String reportId = createReport(contract.id());

        mockMvc.perform(get(getBasePath() + "/" + reportId + "/view"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", startsWith("https://teste.com/report-")));
    }
}
