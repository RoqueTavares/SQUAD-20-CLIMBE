package com.squad20.sistema_climbe.api;

/** Testes da API /api/spreadsheets. GET lista e GET 404; POST exige contractId. */
class SpreadsheetApiTest extends ApiTestBase {

    @Override
    protected String getBasePath() {
        return "/api/spreadsheets";
    }
}
