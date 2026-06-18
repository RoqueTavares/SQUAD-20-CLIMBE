-- V6: Seed Initial Data
-- Hash padrão para a senha '123456': $2y$10$juuh5YjqN6O5IA5Tldwe.eqvjRA6LBQ.aNq1kIgoITdI12I6t4OX2

-- Atualiza a constraint de cargo para aceitar as novas roles antes de inserir
ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS usuarios_cargo_check;
ALTER TABLE usuarios ADD CONSTRAINT usuarios_cargo_check CHECK (cargo::text = ANY (ARRAY[
    'COMPLIANCE'::text, 'CEO'::text, 'MEMBRO_CONSELHO'::text, 'CSO'::text, 'CMO'::text, 
    'CFO'::text, 'CONTADOR'::text, 'ANALISTA_CHEFE'::text, 'ANALISTA_SENIOR'::text, 
    'ANALISTA'::text, 'ANALISTA_VI_TRAINEE'::text, 'ANALISTA_VI_JUNIOR'::text, 
    'ANALISTA_VI_PLENO'::text, 'ANALISTA_VI_SENIOR'::text, 'ANALISTA_BPO_FINANCEIRO'::text
]));

-- 1. Inserir Usuários (IDs 1, 2, 3)
INSERT INTO usuarios (id_usuario, cpf, email, nome_completo, cargo, senha_hash, situacao, created_at)
VALUES
(1, '11122233344', 'ceo@climbe.com.br', 'CEO Sistema Climbe', 'CEO', '$2y$10$juuh5YjqN6O5IA5Tldwe.eqvjRA6LBQ.aNq1kIgoITdI12I6t4OX2', 'ATIVO', NOW()),
(2, '55566677788', 'analista@climbe.com.br', 'Analista Sênior', 'ANALISTA_SENIOR', '$2y$10$juuh5YjqN6O5IA5Tldwe.eqvjRA6LBQ.aNq1kIgoITdI12I6t4OX2', 'ATIVO', NOW()),
(3, '99988877766', 'bpo@climbe.com.br', 'Analista de BPO', 'ANALISTA_BPO_FINANCEIRO', '$2y$10$juuh5YjqN6O5IA5Tldwe.eqvjRA6LBQ.aNq1kIgoITdI12I6t4OX2', 'ATIVO', NOW())
ON CONFLICT DO NOTHING;

SELECT setval('usuarios_id_usuario_seq', 3);

-- 2. Inserir Empresas (IDs 1, 2)
INSERT INTO empresas (id_empresa, razao_social, nome_fantasia, cnpj, email, representante_nome, representante_cpf, created_at)
VALUES 
(1, 'Empresa de Tecnologia Alpha LTDA', 'Tech Alpha', '12345678000195', 'contato@techalpha.com.br', 'João Representante', '11122233344', NOW()),
(2, 'Serviços Beta S.A', 'Beta Services', '98765432000198', 'financeiro@betaservices.com.br', 'Maria Representante', '55566677788', NOW())
ON CONFLICT DO NOTHING;

SELECT setval('empresas_id_empresa_seq', 2);

-- 3. Inserir Propostas (IDs 1, 2)
INSERT INTO propostas (id_proposta, empresa_id, usuario_id, analista_responsavel_id, status, created_at)
VALUES 
(1, 1, 1, 2, 'COMMERCIAL_PROPOSAL_APPROVED', NOW()),
(2, 2, 1, 3, 'IN_TRIAGE', NOW())
ON CONFLICT DO NOTHING;

SELECT setval('propostas_id_proposta_seq', 2);

-- 4. Inserir Contrato (ID 1)
INSERT INTO contratos (id_contrato, proposta_id, analista_responsavel_id, status, data_inicio, data_fim, prazo_execucao, created_at)
VALUES 
(1, 1, 2, 'ACTIVE', CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', CURRENT_DATE + INTERVAL '30 days', NOW())
ON CONFLICT DO NOTHING;

SELECT setval('contratos_id_contrato_seq', 1);

-- 5. Inserir Planilha (ID 1)
INSERT INTO planilhas (id_planilha, contrato_id, url_google_sheets, permissao_visualizacao, bloqueada, created_at)
VALUES 
(1, 1, 'https://docs.google.com/spreadsheets/d/mock_id_12345', 'TEAM_ONLY', false, NOW())
ON CONFLICT DO NOTHING;

SELECT setval('planilhas_id_planilha_seq', 1);

-- 6. Inserir Reunião (ID 1)
INSERT INTO reunioes (id_reuniao, empresa_id, titulo, pauta, data, hora, hora_fim, presencial, status, created_at)
VALUES 
(1, 1, 'Reunião de Kick-off (Tech Alpha)', 'Alinhamento inicial do projeto e apresentação do BPO', CURRENT_DATE + INTERVAL '5 days', '14:00:00', '15:00:00', false, 'SCHEDULED', NOW())
ON CONFLICT DO NOTHING;

SELECT setval('reunioes_id_reuniao_seq', 1);

-- 7. Inserir Participantes da Reunião
INSERT INTO participantes_reuniao (id_reuniao, id_usuario)
VALUES 
(1, 1),
(1, 2)
ON CONFLICT DO NOTHING;

-- 8. Inserir Equipe do Contrato
INSERT INTO contrato_equipe (id_equipe, contrato_id, usuario_id, funcao_na_equipe, created_at)
VALUES 
(1, 1, 2, 'Líder Técnico', NOW()),
(2, 1, 3, 'Analista BPO', NOW())
ON CONFLICT DO NOTHING;

SELECT setval('contrato_equipe_id_equipe_seq', 2);
