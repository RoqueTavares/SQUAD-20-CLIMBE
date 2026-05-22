# Escala Horizontal

## Estado Atual

O backend é majoritariamente stateless:

- autenticação por JWT;
- refresh token persistido em banco;
- documentos em Google Cloud Storage;
- e-mails publicados em RabbitMQ.

O ponto sensível para escala horizontal é o WebSocket.

## Risco Principal

Quando o sistema usa `enableSimpleBroker("/topic", "/queue")`, o broker vive em memória dentro de cada instância Spring. Com duas ou mais instâncias, uma notificação publicada na instância A não alcança automaticamente usuários conectados na instância B.

Além disso, o fluxo de notificações usa destinos de usuário com `/user/queue/notifications`. Para esse caso, não basta conectar duas APIs no mesmo STOMP broker: o Spring também precisa compartilhar a resolução de destinos de usuário entre instâncias.

## Direção Técnica

Migrar o WebSocket para broker externo:

- RabbitMQ com plugin STOMP habilitado;
- Spring `enableStompBrokerRelay`;
- credenciais STOMP por variável de ambiente;
- broadcast de user destination e user registry via broker externo;
- manter `/ws-notifications` como endpoint público de conexão.

## Configuração Aplicada

O sistema agora aceita alternar entre os dois modos por variável de ambiente:

- `APP_WEBSOCKET_BROKER_RELAY_ENABLED=false`: usa `SimpleBroker` local em memória.
- `APP_WEBSOCKET_BROKER_RELAY_ENABLED=true`: usa `StompBrokerRelay` para RabbitMQ/STOMP.

Quando o relay está ligado, o Spring também publica `userDestinationBroadcast` e `userRegistryBroadcast` no broker externo. Isso permite resolver destinos `/user/queue/...` quando o usuário está conectado em uma instância e o evento nasce em outra.

Variáveis do relay:

- `APP_WEBSOCKET_BROKER_RELAY_HOST`
- `APP_WEBSOCKET_BROKER_RELAY_PORT`
- `APP_WEBSOCKET_BROKER_RELAY_CLIENT_LOGIN`
- `APP_WEBSOCKET_BROKER_RELAY_CLIENT_PASSCODE`
- `APP_WEBSOCKET_BROKER_RELAY_SYSTEM_LOGIN`
- `APP_WEBSOCKET_BROKER_RELAY_SYSTEM_PASSCODE`
- `APP_WEBSOCKET_BROKER_RELAY_VIRTUAL_HOST`
- `APP_WEBSOCKET_BROKER_RELAY_USER_DESTINATION_BROADCAST`
- `APP_WEBSOCKET_BROKER_RELAY_USER_REGISTRY_BROADCAST`

## Estratégia De Rollout

1. Manter Cloud Run com `max-instances=1` no primeiro deploy.
2. Habilitar STOMP no RabbitMQ.
3. Ligar `APP_WEBSOCKET_BROKER_RELAY_ENABLED=true`.
4. Testar duas instâncias locais da API conectadas ao mesmo RabbitMQ/STOMP.
5. Testar o fluxo funcional: cliente WebSocket conectado na instância A recebe notificação criada por request atendido na instância B.
6. Só então liberar scale-out no Cloud Run.

## Validação Mínima

- Usuário conectado na instância A recebe notificação criada por request atendido na instância B.
- Reconexão do cliente funciona após restart de uma instância.
- Fila de e-mail continua independente do broker STOMP de WebSocket.
- Testes automatizados continuam verdes sem exigir broker real no perfil `test`.
