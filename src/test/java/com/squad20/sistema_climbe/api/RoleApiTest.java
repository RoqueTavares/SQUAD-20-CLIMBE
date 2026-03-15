package com.squad20.sistema_climbe.api;

/** Testes da API /api/roles (Cargos). */
class RoleApiTest extends ApiTestBase {

    @Override
    protected String getBasePath() {
        return "/api/roles";
    }

    @Override
    protected String getMinimalPostBody() {
        return "{\"name\":\"Cargo Test\"}";
    }

    @Override
    protected String getPatchBody() {
        return "{\"name\":\"Cargo Atualizado\"}";
    }
}
