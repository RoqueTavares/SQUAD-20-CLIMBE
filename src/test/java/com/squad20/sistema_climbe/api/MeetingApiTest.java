package com.squad20.sistema_climbe.api;

/** Testes da API /api/meetings. GET lista e GET 404; POST exige enterpriseId e título. */
class MeetingApiTest extends ApiTestBase {

    @Override
    protected String getBasePath() {
        return "/api/meetings";
    }
}
