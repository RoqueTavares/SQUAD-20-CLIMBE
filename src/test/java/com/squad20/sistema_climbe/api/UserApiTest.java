package com.squad20.sistema_climbe.api;

/** Testes da API /api/users (Usuários). GET lista e GET 404; POST/PATCH/DELETE exigem roleId. */
class UserApiTest extends ApiTestBase {

    @Override
    protected String getBasePath() {
        return "/api/users";
    }
}
