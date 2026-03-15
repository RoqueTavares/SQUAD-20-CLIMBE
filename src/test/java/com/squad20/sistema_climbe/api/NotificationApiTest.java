package com.squad20.sistema_climbe.api;

/** Testes da API /api/notifications. GET lista e GET 404; POST exige userId. */
class NotificationApiTest extends ApiTestBase {

    @Override
    protected String getBasePath() {
        return "/api/notifications";
    }
}
