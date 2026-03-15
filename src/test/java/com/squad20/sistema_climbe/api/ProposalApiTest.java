package com.squad20.sistema_climbe.api;

/** Testes da API /api/proposals. GET lista e GET 404; POST exige enterpriseId e userId. */
class ProposalApiTest extends ApiTestBase {

    @Override
    protected String getBasePath() {
        return "/api/proposals";
    }
}
