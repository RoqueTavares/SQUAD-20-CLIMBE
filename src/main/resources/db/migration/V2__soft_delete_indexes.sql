-- V2: unicidade apenas para registros ativos em entidades com soft delete.
-- Mantém a reutilização de valores quando o registro antigo possui deleted_at preenchido.

CREATE UNIQUE INDEX IF NOT EXISTS idx_cargos_nome_active
    ON cargos(nome_cargo) WHERE deleted_at IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS idx_permissoes_descricao_active
    ON permissoes(descricao) WHERE deleted_at IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS idx_servicos_nome_active
    ON servicos(nome) WHERE deleted_at IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS idx_usuarios_email_active
    ON usuarios(email) WHERE deleted_at IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS idx_usuarios_cpf_active
    ON usuarios(cpf) WHERE deleted_at IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS idx_empresas_cnpj_active
    ON empresas(cnpj) WHERE deleted_at IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS idx_empresas_email_active
    ON empresas(email) WHERE deleted_at IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS idx_requisitos_proposta_tipo_active
    ON requisitos_documentais(proposta_id, tipo_documento) WHERE deleted_at IS NULL;
