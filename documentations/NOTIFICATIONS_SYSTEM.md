# Documentação de Notificações - Sistema de Notificações
Recomendação: Executar via scheduled task diariamente.

```
notificationRepository.deleteOldReadNotifications(LocalDateTime.now().minusDays(30));
```java

O repository fornece método para deletar notificações antigas:

## Limpeza Automática

- `idx_notification_created_at` ON (created_at)
- `idx_notification_user_read` ON (user_id, is_read)
### Índices

```
);
    FOREIGN KEY (user_id) REFERENCES tb_user(id)
    read_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    reference_id BIGINT,
    is_read BOOLEAN DEFAULT false,
    message TEXT NOT NULL,
    title VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    user_id BIGINT NOT NULL,
    id BIGSERIAL PRIMARY KEY,
CREATE TABLE tb_notifications (
```sql

### Tabela: tb_notifications

## Banco de Dados

3. Manter fallback de polling para compatibilidade
2. Enviar notificações em tempo real para usuários conectados
1. Criar um WebSocket endpoint em `/ws/notifications`

O sistema está preparado para integração com WebSocket. Quando implementado:

## Futuro: WebSocket

3. **Badge Desbloqueado** - `UnlockBadgeService.execute()`
2. **Boss Avaliado** - `EvaluateBossFightService.approveBossFight()` ou `rejectBossFight()`
1. **Missão Avaliada** - `EvaluateMission.approveMission()` ou `rejectMission()`

As notificações são criadas automaticamente quando:

## Integração

**Resposta de Sucesso:** `204 No Content`

**Scopes necessários:** `profile:read`
**Autenticação:** Requerida

Marca todas as notificações do usuário como lidas.
### PUT /notifications/read-all

**Resposta de Sucesso:** `204 No Content`

**Scopes necessários:** `profile:read`
**Autenticação:** Requerida

Marca uma notificação específica como lida.
### PUT /notifications/{notificationId}/read

```
5
```json
**Resposta de Sucesso:** `200 OK`

**Scopes necessários:** `profile:read`
**Autenticação:** Requerida

Conta notificações não lidas do usuário.
### GET /notifications/unread/count

```
}
  "totalElements": 1
  "pageable": {},
  ],
    }
      "readAt": null
      "createdAt": "2026-01-08T10:00:00",
      "referenceId": 1,
      "isRead": false,
      "message": "Parabens! Sua submissao da missao 'Primeira Missao' foi aprovada.",
      "title": "Missao Aprovada",
      "type": "MISSION_EVALUATED",
      "id": 1,
    {
  "content": [
{
```json
**Resposta de Sucesso:** `200 OK`

- Parâmetros de paginação padrão (page, size, sort)
- `onlyUnread` (Boolean, opcional): Se true, retorna apenas não lidas. Default: false
**Query Parameters:**

**Scopes necessários:** `profile:read`
**Autenticação:** Requerida

Busca notificações do usuário autenticado com paginação.
### GET /notifications

## Endpoints

Notificação enviada quando um grimório é desbloqueado.
### GRIMOIRE_UNLOCKED

Notificação enviada quando um boss é desbloqueado.
### BOSS_UNLOCKED

Notificação enviada quando uma tentativa de boss é avaliada.
### BOSS_EVALUATED

Notificação enviada quando um badge é desbloqueado.
### BADGE_UNLOCKED

Notificação enviada quando o jogador sobe de nível.
### LEVEL_UP

Notificação enviada quando uma missão é avaliada (aprovada ou reprovada).
### MISSION_EVALUATED

## Tipos de Notificações

O sistema de notificações foi implementado para informar os usuários sobre eventos importantes no sistema. Preparado para integração futura com WebSocket para notificações em tempo real.

## Visão Geral


