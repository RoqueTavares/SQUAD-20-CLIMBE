package com.squad20.sistema_climbe.api;

import com.squad20.sistema_climbe.SistemaClimbeApplication;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Base para testes por API: GET lista, GET 404, e (se houver payload mínimo) POST, PATCH, DELETE.
 */
@SpringBootTest(classes = SistemaClimbeApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
abstract class ApiTestBase {

    /** Lista única de paths das APIs — usar no smoke e para garantir que nenhum recurso fique de fora. */
    public static final String[] API_BASE_PATHS = {
        "/api/roles", "/api/users", "/api/enterprises", "/api/permissions", "/api/services",
        "/api/notifications", "/api/meetings", "/api/proposals", "/api/contracts",
        "/api/spreadsheets", "/api/reports", "/api/documents"
    };

    @Autowired
    protected MockMvc mockMvc;

    protected abstract String getBasePath();

    /**
     * Payload mínimo para POST. Se null, só rodam testes de GET lista e GET por id inexistente.
     */
    protected String getMinimalPostBody() {
        return null;
    }

    @Test
    @DisplayName("GET lista retorna 200")
    void getListaRetorna200() throws Exception {
        mockMvc.perform(get(getBasePath())).andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET por ID inexistente retorna 404")
    void getPorIdInexistenteRetorna404() throws Exception {
        mockMvc.perform(get(getBasePath() + "/999999")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST retorna 201")
    void postRetorna201() throws Exception {
        Assumptions.assumeTrue(getMinimalPostBody() != null, "Recurso exige FK, sem payload mínimo");
        mockMvc.perform(post(getBasePath())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getMinimalPostBody()))
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("PATCH retorna 200")
    void patchRetorna200() throws Exception {
        Assumptions.assumeTrue(getMinimalPostBody() != null, "Recurso exige FK, sem payload mínimo");
        String createRes = mockMvc.perform(post(getBasePath())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getMinimalPostBody()))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        String id = extractIdFromJson(createRes);
        String patchBody = getPatchBody();
        mockMvc.perform(patch(getBasePath() + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchBody))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE retorna 204")
    void deleteRetorna204() throws Exception {
        Assumptions.assumeTrue(getMinimalPostBody() != null, "Recurso exige FK, sem payload mínimo");
        String createRes = mockMvc.perform(post(getBasePath())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getMinimalPostBody()))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        String id = extractIdFromJson(createRes);
        mockMvc.perform(delete(getBasePath() + "/" + id)).andExpect(status().isNoContent());
    }

    protected String getPatchBody() {
        return getMinimalPostBody();
    }

    protected static String extractIdFromJson(String json) {
        if (json == null) return null;
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("\"id\"\\s*:\\s*(\\d+)").matcher(json);
        return m.find() ? m.group(1) : null;
    }
}
