package com.squad20.sistema_climbe.api;

/** Testes da API /api/enterprises (Empresas). */
class EnterpriseApiTest extends ApiTestBase {

    @Override
    protected String getBasePath() {
        return "/api/enterprises";
    }

    @Override
    protected String getMinimalPostBody() {
        return "{\"legalName\":\"Empresa Test Ltda\",\"cnpj\":\"12345678000199\"}";
    }

    @Override
    protected String getPatchBody() {
        return "{\"tradeName\":\"Empresa Atualizada\"}";
    }
}
