-- V5: Database Validation Triggers

-- 1. Trigger de Datas de Contrato
CREATE OR REPLACE FUNCTION trg_check_contrato_datas()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.data_fim IS NOT NULL AND NEW.data_inicio IS NOT NULL THEN
        IF NEW.data_fim < NEW.data_inicio THEN
            RAISE EXCEPTION 'A data de fim do contrato (%) não pode ser anterior à data de início (%).', NEW.data_fim, NEW.data_inicio;
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_contratos_datas
BEFORE INSERT OR UPDATE ON contratos
FOR EACH ROW
EXECUTE FUNCTION trg_check_contrato_datas();

-- 2. Trigger de Horários de Reunião
CREATE OR REPLACE FUNCTION trg_check_reuniao_horarios()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.hora_fim IS NOT NULL AND NEW.hora IS NOT NULL THEN
        IF NEW.hora_fim <= NEW.hora THEN
            RAISE EXCEPTION 'O horário de término da reunião (%) deve ser posterior ao horário de início (%).', NEW.hora_fim, NEW.hora;
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_reunioes_horarios
BEFORE INSERT OR UPDATE ON reunioes
FOR EACH ROW
EXECUTE FUNCTION trg_check_reuniao_horarios();

-- 3. Trigger de Blindagem de Auditoria
-- Impede que created_at e created_by sejam alterados num UPDATE
CREATE OR REPLACE FUNCTION trg_protect_audit_fields()
RETURNS TRIGGER AS $$
BEGIN
    -- Se é um UPDATE, forçamos os valores originais nas colunas de auditoria de criação
    NEW.created_at = OLD.created_at;
    NEW.created_by = OLD.created_by;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Aplicar em algumas tabelas principais
CREATE TRIGGER trg_audit_usuarios BEFORE UPDATE ON usuarios FOR EACH ROW EXECUTE FUNCTION trg_protect_audit_fields();
CREATE TRIGGER trg_audit_empresas BEFORE UPDATE ON empresas FOR EACH ROW EXECUTE FUNCTION trg_protect_audit_fields();
CREATE TRIGGER trg_audit_contratos BEFORE UPDATE ON contratos FOR EACH ROW EXECUTE FUNCTION trg_protect_audit_fields();
CREATE TRIGGER trg_audit_propostas BEFORE UPDATE ON propostas FOR EACH ROW EXECUTE FUNCTION trg_protect_audit_fields();

-- 4. Trigger de Validação de CPF/CNPJ e Email
CREATE OR REPLACE FUNCTION trg_validate_documents_and_email()
RETURNS TRIGGER AS $$
DECLARE
    cleaned_doc VARCHAR;
BEGIN
    -- Validação na tabela Usuarios
    IF TG_TABLE_NAME = 'usuarios' THEN
        -- Validar CPF (se não for string vazia/nula)
        IF NEW.cpf IS NOT NULL AND NEW.cpf <> '' THEN
            cleaned_doc := regexp_replace(NEW.cpf, '\D', '', 'g'); -- Remove tudo que não é dígito
            IF length(cleaned_doc) != 11 THEN
                RAISE EXCEPTION 'CPF inválido: deve conter exatamente 11 dígitos numéricos.';
            END IF;
        END IF;
        
        -- Validar Email
        IF NEW.email IS NOT NULL AND NEW.email NOT LIKE '%@%.%' THEN
            RAISE EXCEPTION 'E-mail do usuário inválido: %', NEW.email;
        END IF;
    END IF;

    -- Validação na tabela Empresas
    IF TG_TABLE_NAME = 'empresas' THEN
        -- Validar CNPJ
        IF NEW.cnpj IS NOT NULL AND NEW.cnpj <> '' THEN
            cleaned_doc := regexp_replace(NEW.cnpj, '\D', '', 'g');
            IF length(cleaned_doc) != 14 THEN
                RAISE EXCEPTION 'CNPJ inválido: deve conter exatamente 14 dígitos numéricos.';
            END IF;
        END IF;
        
        -- Validar Email
        IF NEW.email IS NOT NULL AND NEW.email NOT LIKE '%@%.%' THEN
            RAISE EXCEPTION 'E-mail da empresa inválido: %', NEW.email;
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validate_usuarios
BEFORE INSERT OR UPDATE ON usuarios
FOR EACH ROW
EXECUTE FUNCTION trg_validate_documents_and_email();

CREATE TRIGGER trg_validate_empresas
BEFORE INSERT OR UPDATE ON empresas
FOR EACH ROW
EXECUTE FUNCTION trg_validate_documents_and_email();
