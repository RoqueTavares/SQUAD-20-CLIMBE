package com.squad20.sistema_climbe.api;

/** Testes da API /api/contracts. GET lista e GET 404; POST exige proposalId. */
class ContractApiTest extends ApiTestBase {

    @Override
    protected String getBasePath() {
        return "/api/contracts";
    }
}
