# Deploy No Google Cloud Run

## Objetivo

Subir o backend do Sistema Climbe em Cloud Run com container stateless, banco externo, storage externo e segredos fora do repositório.

## Arquitetura Alvo

- Container: Cloud Run.
- Imagem: Artifact Registry.
- Banco: Cloud SQL PostgreSQL.
- Storage: Google Cloud Storage.
- Segredos: Secret Manager.
- E-mail: SMTP configurado por variáveis/segredos.
- Mensageria: RabbitMQ externo ou serviço compatível, sem rodar broker dentro do container da API.
- WebSocket: suportado pelo Cloud Run, mas scale-out exige broker externo e broadcast de destinos de usuário entre instâncias.

## Variáveis Obrigatórias

- `SPRING_PROFILES_ACTIVE=prod`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_JPA_HIBERNATE_DDL_AUTO=validate` ou `none`
- `JWT_SECRET`
- `JWT_EXPIRATION_MS`
- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`
- `GCP_CREDENTIALS_FILE_PATH`
- `GCP_BUCKET_NAME`
- `SPRING_RABBITMQ_HOST`
- `SPRING_RABBITMQ_PORT`
- `SPRING_RABBITMQ_USERNAME`
- `SPRING_RABBITMQ_PASSWORD`
- `APP_WEBSOCKET_BROKER_RELAY_ENABLED=true`
- `APP_WEBSOCKET_BROKER_RELAY_HOST`
- `APP_WEBSOCKET_BROKER_RELAY_PORT`
- `APP_WEBSOCKET_BROKER_RELAY_CLIENT_LOGIN`
- `APP_WEBSOCKET_BROKER_RELAY_CLIENT_PASSCODE`
- `APP_WEBSOCKET_BROKER_RELAY_SYSTEM_LOGIN`
- `APP_WEBSOCKET_BROKER_RELAY_SYSTEM_PASSCODE`
- `APP_WEBSOCKET_BROKER_RELAY_VIRTUAL_HOST`
- `APP_WEBSOCKET_BROKER_RELAY_USER_DESTINATION_BROADCAST`
- `APP_WEBSOCKET_BROKER_RELAY_USER_REGISTRY_BROADCAST`
- `APP_WEBSOCKET_BROKER_RELAY_SYSTEM_HEARTBEAT_SEND_INTERVAL`
- `APP_WEBSOCKET_BROKER_RELAY_SYSTEM_HEARTBEAT_RECEIVE_INTERVAL`
- `SPRING_MAIL_HOST`
- `SPRING_MAIL_PORT`
- `SPRING_MAIL_USERNAME`
- `SPRING_MAIL_PASSWORD`
- `CORS_ALLOWED_ORIGINS`
- `APP_FRONTEND_REDIRECT_URL`
- `APP_COOKIE_SECURE=true`
- `APP_COOKIE_SAME_SITE`
- `PORT=8080` ou valor injetado pelo Cloud Run
- `SERVER_FORWARD_HEADERS_STRATEGY=framework`
- `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info`
- `MANAGEMENT_HEALTH_MAIL_ENABLED=false`

## Regras De Produção

- Não usar `.env` real no repositório.
- Não embutir `gcp-credentials.json` na imagem.
- Não usar `SPRING_JPA_HIBERNATE_DDL_AUTO=update` em produção.
- Não usar `admin/admin` fora do ambiente local.
- Não escalar acima de uma instância enquanto o WebSocket usar `SimpleBroker` em memória.
- Antes de liberar scale-out, validar usuário conectado em uma instância recebendo notificação criada por outra.
- Configurar URL de callback OAuth no Google Cloud Console conforme domínio público da API.
- Configurar `CORS_ALLOWED_ORIGINS` com o domínio real do frontend; o WebSocket usa a mesma lista.
- Usar `/actuator/health` como healthcheck público do container.
- Manter `MANAGEMENT_HEALTH_MAIL_ENABLED=false` se SMTP for dependência externa não crítica de startup.

Também vale para o broker relay: se `APP_WEBSOCKET_BROKER_RELAY_ENABLED=true`, informar credenciais STOMP explícitas e diferentes das credenciais locais.

## Primeiro Deploy Controlado

1. Criar Artifact Registry.
2. Buildar e publicar imagem.
3. Provisionar secrets.
4. Criar Cloud SQL e bucket GCS.
5. Subir Cloud Run com `max-instances=1`.
6. Validar `/api/auth/me`, upload de documento, fila de e-mail e conexão WebSocket.
7. Validar `/actuator/health`, `/actuator/health/liveness` e `/actuator/health/readiness`.
8. Habilitar broker relay externo e testar o fluxo WebSocket entre duas instâncias antes de aumentar `max-instances`.
