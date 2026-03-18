package com.squad20.sistema_climbe.api;

/** Testes da API /api/enterprises (Empresas). */
class EnterpriseApiTest extends ApiTestBase {

    @Override
    protected String getBasePath() {
        return "/api/enterprises";
    }

    @Override
    protected String getMinimalPostBody() {
        long n = Math.abs(System.nanoTime());
        String filial = String.format("%04d", n % 10000);
        String dv = String.format("%02d", (n / 10000) % 100);
        return "{\"legalName\":\"Empresa Test Ltda\",\"cnpj\":\"12.345.678/" + filial + "-" + dv + "\",\"email\":\"teste" + n + "@empresa.com\"}";
    }

    @Override
    protected String getPatchBody() {
        return "{\"tradeName\":\"Empresa Atualizada " + System.nanoTime() + "\"}";
    }
}
