-- V3: Add audit fields for JPA Auditing

ALTER TABLE cargos ADD COLUMN updated_at timestamp(6) without time zone;
ALTER TABLE cargos ADD COLUMN created_by bigint;
ALTER TABLE cargos ADD COLUMN updated_by bigint;

ALTER TABLE contrato_equipe ADD COLUMN updated_at timestamp(6) without time zone;
ALTER TABLE contrato_equipe ADD COLUMN created_by bigint;
ALTER TABLE contrato_equipe ADD COLUMN updated_by bigint;

ALTER TABLE contratos ADD COLUMN updated_at timestamp(6) without time zone;
ALTER TABLE contratos ADD COLUMN created_by bigint;
ALTER TABLE contratos ADD COLUMN updated_by bigint;

ALTER TABLE documentos ADD COLUMN updated_at timestamp(6) without time zone;
ALTER TABLE documentos ADD COLUMN created_by bigint;
ALTER TABLE documentos ADD COLUMN updated_by bigint;

ALTER TABLE empresas ADD COLUMN updated_at timestamp(6) without time zone;
ALTER TABLE empresas ADD COLUMN created_by bigint;
ALTER TABLE empresas ADD COLUMN updated_by bigint;

ALTER TABLE notificacoes ADD COLUMN updated_at timestamp(6) without time zone;
ALTER TABLE notificacoes ADD COLUMN created_by bigint;
ALTER TABLE notificacoes ADD COLUMN updated_by bigint;

ALTER TABLE permissoes ADD COLUMN updated_at timestamp(6) without time zone;
ALTER TABLE permissoes ADD COLUMN created_by bigint;
ALTER TABLE permissoes ADD COLUMN updated_by bigint;

ALTER TABLE planilhas ADD COLUMN updated_at timestamp(6) without time zone;
ALTER TABLE planilhas ADD COLUMN created_by bigint;
ALTER TABLE planilhas ADD COLUMN updated_by bigint;

ALTER TABLE propostas ADD COLUMN updated_at timestamp(6) without time zone;
ALTER TABLE propostas ADD COLUMN created_by bigint;
ALTER TABLE propostas ADD COLUMN updated_by bigint;

ALTER TABLE relatorios ADD COLUMN updated_at timestamp(6) without time zone;
ALTER TABLE relatorios ADD COLUMN created_by bigint;
ALTER TABLE relatorios ADD COLUMN updated_by bigint;

ALTER TABLE requisitos_documentais ADD COLUMN updated_at timestamp(6) without time zone;
ALTER TABLE requisitos_documentais ADD COLUMN created_by bigint;
ALTER TABLE requisitos_documentais ADD COLUMN updated_by bigint;

ALTER TABLE reunioes ADD COLUMN updated_at timestamp(6) without time zone;
ALTER TABLE reunioes ADD COLUMN created_by bigint;
ALTER TABLE reunioes ADD COLUMN updated_by bigint;

ALTER TABLE servicos ADD COLUMN updated_at timestamp(6) without time zone;
ALTER TABLE servicos ADD COLUMN created_by bigint;
ALTER TABLE servicos ADD COLUMN updated_by bigint;

ALTER TABLE usuarios ADD COLUMN updated_at timestamp(6) without time zone;
ALTER TABLE usuarios ADD COLUMN created_by bigint;
ALTER TABLE usuarios ADD COLUMN updated_by bigint;
