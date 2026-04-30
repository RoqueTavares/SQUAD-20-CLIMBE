package com.squad20.sistema_climbe.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes da API /api/documents.
 * O POST consome multipart/form-data (parte "data" com o JSON e parte "file"
 * opcional), por isso os testes herdados de POST/PATCH/DELETE são reescritos
 * usando o builder multipart.
 */
class DocumentApiTest extends ApiTestBase {

    @Override
    protected String getBasePath() {
        return "/api/documents";
    }

    @Override
    protected String getMinimalPostBody() throws RuntimeException {
        try {
            String enterpriseId = createEnterprise();
            UserFixture user = createUser();
            return "{\"enterpriseId\":" + enterpriseId + ",\"documentType\":\"CONTRATO\",\"url\":\"https://teste.com/doc.pdf\",\"validated\":true,\"analystId\":" + user.id() + "}";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getPatchBody() {
        return "{\"documentType\":\"DOC_ATUALIZADO\",\"validated\":false}";
    }

    @Override
    @Test
    @DisplayName("POST retorna 201")
    void postRetorna201() throws Exception {
        MockMultipartFile data = new MockMultipartFile(
                "data", "data.json", MediaType.APPLICATION_JSON_VALUE, getMinimalPostBody().getBytes());
        mockMvc.perform(multipart(getBasePath()).file(data))
                .andExpect(status().isCreated());
    }

    @Override
    @Test
    @DisplayName("PATCH retorna 200")
    void patchRetorna200() throws Exception {
        String id = createDocumentMultipart(getMinimalPostBody());
        mockMvc.perform(patch(getBasePath() + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getPatchBody()))
                .andExpect(status().isOk());
    }

    @Override
    @Test
    @DisplayName("DELETE retorna 204")
    void deleteRetorna204() throws Exception {
        String id = createDocumentMultipart(getMinimalPostBody());
        mockMvc.perform(delete(getBasePath() + "/" + id)).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH sem analystId preserva analista atual")
    void patchSemAnalystIdPreservaAnalistaAtual() throws Exception {
        String enterpriseId = createEnterprise();
        UserFixture analyst = createUser();

        String documentId = createDocumentMultipart(
                "{\"enterpriseId\":" + enterpriseId + ",\"documentType\":\"RG\",\"analystId\":" + analyst.id() + "}");

        mockMvc.perform(patch(getBasePath() + "/" + documentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"documentType\":\"CPF\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentType").value("CPF"))
                .andExpect(jsonPath("$.analystId").value(Integer.parseInt(analyst.id())));
    }

    @Test
    @DisplayName("PATCH só com url (sem analystId) preserva analista e persiste no GET")
    void patchSomenteUrlSemAnalystIdPreservaAnalista() throws Exception {
        String enterpriseId = createEnterprise();
        UserFixture analyst = createUser();

        String documentId = createDocumentMultipart(
                "{\"enterpriseId\":" + enterpriseId + ",\"documentType\":\"RG\",\"url\":\"https://teste.com/a.pdf\",\"analystId\":"
                        + analyst.id() + "}");

        mockMvc.perform(patch(getBasePath() + "/" + documentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"url\":\"https://teste.com/b.pdf\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://teste.com/b.pdf"))
                .andExpect(jsonPath("$.analystId").value(Integer.parseInt(analyst.id())));

        mockMvc.perform(get(getBasePath() + "/" + documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.analystId").value(Integer.parseInt(analyst.id())));
    }

    @Test
    @DisplayName("GET por empresa retorna documentos criados")
    void getByEnterpriseReturnsCreatedDocuments() throws Exception {
        String enterpriseId = createEnterprise();
        createDocumentMultipart("{\"enterpriseId\":" + enterpriseId + ",\"documentType\":\"RG\"}");

        mockMvc.perform(get(getBasePath() + "/enterprise/" + enterpriseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].enterpriseId").value(Integer.parseInt(enterpriseId)));
    }

    @Test
    @DisplayName("GET por analista retorna documentos criados")
    void getByAnalystReturnsCreatedDocuments() throws Exception {
        String enterpriseId = createEnterprise();
        UserFixture user = createUser();
        createDocumentMultipart(
                "{\"enterpriseId\":" + enterpriseId + ",\"documentType\":\"CPF\",\"analystId\":" + user.id() + "}");

        mockMvc.perform(get(getBasePath() + "/analyst/" + user.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].analystId").value(Integer.parseInt(user.id())));
    }

    @Test
    @DisplayName("Soft delete não aparece em $.content da listagem geral")
    void softDeleteNaoApareceEmPageContent() throws Exception {
        String enterpriseId = createEnterprise();
        String documentId = createDocument(enterpriseId);

        mockMvc.perform(get(getBasePath() + "?size=200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.id == " + documentId + ")]").isNotEmpty());

        mockMvc.perform(delete(getBasePath() + "/" + documentId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(getBasePath() + "/" + documentId))
                .andExpect(status().isNotFound());

        mockMvc.perform(get(getBasePath() + "?size=200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.id == " + documentId + ")]").isEmpty());
    }
}
