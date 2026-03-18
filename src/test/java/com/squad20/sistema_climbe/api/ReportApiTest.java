package com.squad20.sistema_climbe.api;

/** Testes da API /api/reports. GET lista e GET 404; POST exige contractId. */
class ReportApiTest extends ApiTestBase {

    @Override
    protected String getBasePath() {
        return "/api/reports";
    }
}
