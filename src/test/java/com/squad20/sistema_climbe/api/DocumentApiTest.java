package com.squad20.sistema_climbe.api;

/** Testes da API /api/documents. GET lista e GET 404; POST exige enterpriseId. */
class DocumentApiTest extends ApiTestBase {

    @Override
    protected String getBasePath() {
        return "/api/documents";
    }
}
