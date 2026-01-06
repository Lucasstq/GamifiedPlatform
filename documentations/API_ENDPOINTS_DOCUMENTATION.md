# Documentação de Endpoints da API - GamifiedPlatform

## Índice
1. [Autenticação](#autenticação)
2. [Usuários](#usuários)
3. [Níveis - Consultas](#níveis---consultas)
4. [Níveis - Gamificação](#níveis---gamificação)
5. [Níveis - Admin](#níveis---admin)
6. [Missões](#missões)
7. [Missões de Usuário](#missões-de-usuário)
8. [Enumerações](#enumerações)

---

## Autenticação

### POST /auth/login
Realiza o login do usuário e retorna um token JWT.

**Permissão:** Público

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Validações:**
- `username`: Não pode ser vazio
- `password`: Não pode ser vazio

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": "3600000"
}
```

---

### POST /auth/register
Registra um novo usuário no sistema.

**Permissão:** Público

**Request Body:**
```json
{
  "username": "string",
  "email": "user@example.com",
  "password": "string",
  "avatarUrl": "string (opcional)"
}
```

**Validações:**
- `username`: Não pode ser nulo
- `email`: Não pode ser nulo e deve ser um email válido
- `password`: Não pode ser nulo e deve ter no mínimo 8 caracteres
- `avatarUrl`: Opcional

**Response (201 CREATED):**
```json
{
  "id": 1,
  "username": "string",
  "email": "user@example.com",
  "avatarUrl": "string",
  "createdAt": "2026-01-05T10:30:00",
  "updatedAt": "2026-01-05T10:30:00"
}
```

---

### GET /auth/verify-email?token={token}
Verifica o email do usuário através do token enviado por email.

**Permissão:** Público

**Query Parameters:**
- `token`: Token de verificação (string)

**Response (200 OK):**
```json
"Email verified successfully! You can now login to your account."
```

---

### POST /auth/resend-verification
Reenvia o email de verificação para o usuário.

**Permissão:** Público

**Request Body:**
```json
{
  "email": "user@example.com"
}
```

**Validações:**
- `email`: Não pode ser vazio e deve ser um email válido

**Response (200 OK):**
```json
"Verification email sent successfully!"
```

---

## Usuários

### PUT /users/{id}
Atualiza os dados de um usuário.

**Permissão:** `@CanWriteProfile`

**Path Parameters:**
- `id`: ID do usuário (Long)

**Request Body:**
```json
{
  "username": "string",
  "email": "user@example.com",
  "avatarUrl": "string (opcional)"
}
```

**Validações:**
- `username`: Não pode ser vazio
- `email`: Não pode ser vazio e deve ser um email válido

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "string",
  "email": "user@example.com",
  "avatarUrl": "string",
  "createdAt": "2026-01-05T10:30:00",
  "updatedAt": "2026-01-05T10:35:00"
}
```

---

### GET /users/{id}
Busca um usuário por ID.

**Permissão:** `@CanReadUsers`

**Path Parameters:**
- `id`: ID do usuário (Long)

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "string",
  "email": "user@example.com",
  "avatarUrl": "string",
  "createdAt": "2026-01-05T10:30:00",
  "updatedAt": "2026-01-05T10:30:00"
}
```

---

### GET /users/search?username={username}
Busca um usuário por username.

**Permissão:** `@CanReadUsers`

**Query Parameters:**
- `username`: Nome do usuário (string)

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "string",
  "email": "user@example.com",
  "avatarUrl": "string",
  "createdAt": "2026-01-05T10:30:00",
  "updatedAt": "2026-01-05T10:30:00"
}
```

---

### PATCH /users/{id}/change-password
Altera a senha do usuário.

**Permissão:** `@CanWriteProfile`

**Path Parameters:**
- `id`: ID do usuário (Long)

**Request Body:**
```json
{
  "currentPassword": "string",
  "newPassword": "string",
  "confirmNewPassword": "string"
}
```

**Validações:**
- `currentPassword`: Não pode ser nulo
- `newPassword`: Não pode ser nulo e deve ter no mínimo 8 caracteres
- `confirmNewPassword`: Não pode ser nulo

**Response (204 NO CONTENT):**
```
Sem corpo de resposta
```

---

### DELETE /users/{id}
Deleta um usuário do sistema.

**Permissão:** `@CanDeleteProfile`

**Path Parameters:**
- `id`: ID do usuário (Long)

**Request Body:**
```json
{
  "password": "string"
}
```

**Validações:**
- `password`: Obrigatório para confirmar a exclusão

**Response (204 NO CONTENT):**
```
Sem corpo de resposta
```

---

## Níveis - Consultas

### GET /levels
Lista todos os níveis do sistema.

**Permissão:** `@CanReadLevels`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "orderLevel": 1,
    "name": "Iniciante",
    "title": "Jornada Começa",
    "description": "Primeiro nível da jornada",
    "xpRequired": 0,
    "iconUrl": "https://example.com/icon.png",
    "difficultyLevel": "EASY",
    "createdAt": "2026-01-05T10:30:00",
    "updatedAt": "2026-01-05T10:30:00"
  }
]
```

---

### GET /levels/{id}
Busca um nível específico por ID.

**Permissão:** `@CanReadLevels`

**Path Parameters:**
- `id`: ID do nível (Long)

**Response (200 OK):**
```json
{
  "id": 1,
  "orderLevel": 1,
  "name": "Iniciante",
  "title": "Jornada Começa",
  "description": "Primeiro nível da jornada",
  "xpRequired": 0,
  "iconUrl": "https://example.com/icon.png",
  "difficultyLevel": "EASY",
  "createdAt": "2026-01-05T10:30:00",
  "updatedAt": "2026-01-05T10:30:00"
}
```

---

### GET /levels/order/{orderLevel}
Busca um nível por número de ordem.

**Permissão:** `@CanReadLevels`

**Path Parameters:**
- `orderLevel`: Número de ordem do nível (Integer)

**Response (200 OK):**
```json
{
  "id": 1,
  "orderLevel": 1,
  "name": "Iniciante",
  "title": "Jornada Começa",
  "description": "Primeiro nível da jornada",
  "xpRequired": 0,
  "iconUrl": "https://example.com/icon.png",
  "difficultyLevel": "EASY",
  "createdAt": "2026-01-05T10:30:00",
  "updatedAt": "2026-01-05T10:30:00"
}
```

---

### GET /levels/difficulty/{difficulty}
Busca níveis por dificuldade.

**Permissão:** `@CanReadLevels`

**Path Parameters:**
- `difficulty`: Nível de dificuldade (EASY, MEDIUM, HARD, EXPERT)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "orderLevel": 1,
    "name": "Iniciante",
    "title": "Jornada Começa",
    "description": "Primeiro nível da jornada",
    "xpRequired": 0,
    "iconUrl": "https://example.com/icon.png",
    "difficultyLevel": "EASY",
    "createdAt": "2026-01-05T10:30:00",
    "updatedAt": "2026-01-05T10:30:00"
  }
]
```

---

### GET /levels/user/{userId}
Busca o nível atual de um usuário baseado em seu XP.

**Permissão:** `@CanReadLevels`

**Path Parameters:**
- `userId`: ID do usuário (Long)

**Response (200 OK):**
```json
{
  "id": 1,
  "orderLevel": 1,
  "name": "Iniciante",
  "title": "Jornada Começa",
  "description": "Primeiro nível da jornada",
  "xpRequired": 0,
  "iconUrl": "https://example.com/icon.png",
  "difficultyLevel": "EASY",
  "createdAt": "2026-01-05T10:30:00",
  "updatedAt": "2026-01-05T10:30:00"
}
```

---

### GET /levels/next/{currentOrderLevel}
Busca o próximo nível na progressão.

**Permissão:** `@CanReadLevels`

**Path Parameters:**
- `currentOrderLevel`: Ordem do nível atual (Integer)

**Response (200 OK):**
```json
{
  "id": 2,
  "orderLevel": 2,
  "name": "Aprendiz",
  "title": "Evolução Contínua",
  "description": "Segundo nível da jornada",
  "xpRequired": 100,
  "iconUrl": "https://example.com/icon.png",
  "difficultyLevel": "EASY",
  "createdAt": "2026-01-05T10:30:00",
  "updatedAt": "2026-01-05T10:30:00"
}
```

---

### GET /levels/unlocked?currentXp={xp}
Lista todos os níveis desbloqueados para um determinado XP.

**Permissão:** `@CanReadLevels`

**Query Parameters:**
- `currentXp`: XP atual do jogador (Integer)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "orderLevel": 1,
    "name": "Iniciante",
    "title": "Jornada Começa",
    "description": "Primeiro nível da jornada",
    "xpRequired": 0,
    "iconUrl": "https://example.com/icon.png",
    "difficultyLevel": "EASY",
    "createdAt": "2026-01-05T10:30:00",
    "updatedAt": "2026-01-05T10:30:00"
  }
]
```

---

### GET /levels/locked?currentXp={xp}
Lista todos os níveis ainda bloqueados para um determinado XP.

**Permissão:** `@CanReadLevels`

**Query Parameters:**
- `currentXp`: XP atual do jogador (Integer)

**Response (200 OK):**
```json
[
  {
    "id": 5,
    "orderLevel": 5,
    "name": "Expert",
    "title": "Maestria Completa",
    "description": "Nível avançado da jornada",
    "xpRequired": 500,
    "iconUrl": "https://example.com/icon.png",
    "difficultyLevel": "EXPERT",
    "createdAt": "2026-01-05T10:30:00",
    "updatedAt": "2026-01-05T10:30:00"
  }
]
```

---

### GET /levels/stats
Retorna estatísticas gerais do sistema de níveis.

**Permissão:** `@CanReadLevels`

**Response (200 OK):**
```json
{
  "totalLevels": 10,
  "minXpRequired": 0,
  "maxXpRequired": 5000,
  "easiestLevel": {
    "id": 1,
    "orderLevel": 1,
    "name": "Iniciante",
    "title": "Jornada Começa",
    "description": "Primeiro nível da jornada",
    "xpRequired": 0,
    "iconUrl": "https://example.com/icon.png",
    "difficultyLevel": "EASY",
    "createdAt": "2026-01-05T10:30:00",
    "updatedAt": "2026-01-05T10:30:00"
  },
  "hardestLevel": {
    "id": 10,
    "orderLevel": 10,
    "name": "Mestre",
    "title": "Domínio Total",
    "description": "Último nível da jornada",
    "xpRequired": 5000,
    "iconUrl": "https://example.com/icon.png",
    "difficultyLevel": "EXPERT",
    "createdAt": "2026-01-05T10:30:00",
    "updatedAt": "2026-01-05T10:30:00"
  }
}
```

---

## Níveis - Gamificação

### GET /levels/calculate-by-xp?xp={xp}
Calcula qual nível corresponde ao XP informado.

**Permissão:** `@CanReadLevels`

**Query Parameters:**
- `xp`: Quantidade de XP (Integer)

**Response (200 OK):**
```json
{
  "id": 2,
  "orderLevel": 2,
  "name": "Aprendiz",
  "title": "Evolução Contínua",
  "description": "Segundo nível da jornada",
  "xpRequired": 100,
  "iconUrl": "https://example.com/icon.png",
  "difficultyLevel": "EASY",
  "createdAt": "2026-01-05T10:30:00",
  "updatedAt": "2026-01-05T10:30:00"
}
```

---

### GET /levels/xp-to-next?currentXp={xp}&currentOrderLevel={order}
Calcula o XP necessário para alcançar o próximo nível.

**Permissão:** `@CanReadLevels`

**Query Parameters:**
- `currentXp`: XP atual (Integer)
- `currentOrderLevel`: Ordem do nível atual (Integer)

**Response (200 OK):**
```json
150
```

---

### GET /levels/can-unlock?currentXp={xp}&levelId={id}
Verifica se o jogador pode desbloquear um nível específico.

**Permissão:** `@CanReadLevels`

**Query Parameters:**
- `currentXp`: XP atual (Integer)
- `levelId`: ID do nível a verificar (Long)

**Response (200 OK):**
```json
true
```

---

### GET /levels/progress?currentXp={xp}&currentOrderLevel={order}
Calcula o progresso percentual no nível atual.

**Permissão:** `@CanReadLevels`

**Query Parameters:**
- `currentXp`: XP atual (Integer)
- `currentOrderLevel`: Ordem do nível atual (Integer)

**Response (200 OK):**
```json
75.5
```

---

## Níveis - Admin

### POST /levels/admin
Cria um novo nível no sistema (apenas ADMIN).

**Permissão:** `@IsAdmin`

**Request Body:**
```json
{
  "orderLevel": 1,
  "name": "Iniciante",
  "title": "Jornada Começa",
  "description": "Primeiro nível da jornada",
  "xpRequired": 0,
  "iconUrl": "https://example.com/icon.png",
  "difficultyLevel": "EASY"
}
```

**Validações:**
- `orderLevel`: Obrigatório, mínimo 1
- `name`: Obrigatório, máximo 100 caracteres
- `title`: Obrigatório, máximo 200 caracteres
- `description`: Obrigatório, máximo 1000 caracteres
- `xpRequired`: Obrigatório, mínimo 0
- `difficultyLevel`: Obrigatório (EASY, MEDIUM, HARD, EXPERT)

**Response (201 CREATED):**
```json
{
  "id": 1,
  "orderLevel": 1,
  "name": "Iniciante",
  "title": "Jornada Começa",
  "description": "Primeiro nível da jornada",
  "xpRequired": 0,
  "iconUrl": "https://example.com/icon.png",
  "difficultyLevel": "EASY",
  "createdAt": "2026-01-05T10:30:00",
  "updatedAt": "2026-01-05T10:30:00"
}
```

---

### PUT /levels/admin/{id}
Atualiza um nível existente (apenas ADMIN).

**Permissão:** `@IsAdmin`

**Path Parameters:**
- `id`: ID do nível (Long)

**Request Body:**
```json
{
  "orderLevel": 1,
  "name": "Iniciante Atualizado",
  "title": "Nova Jornada",
  "description": "Descrição atualizada",
  "xpRequired": 0,
  "iconUrl": "https://example.com/icon.png",
  "difficultyLevel": "EASY"
}
```

**Validações:** Mesmas do POST

**Response (200 OK):**
```json
{
  "id": 1,
  "orderLevel": 1,
  "name": "Iniciante Atualizado",
  "title": "Nova Jornada",
  "description": "Descrição atualizada",
  "xpRequired": 0,
  "iconUrl": "https://example.com/icon.png",
  "difficultyLevel": "EASY",
  "createdAt": "2026-01-05T10:30:00",
  "updatedAt": "2026-01-05T10:35:00"
}
```

---

### DELETE /levels/admin/{id}
Deleta um nível do sistema (apenas ADMIN).

**Permissão:** `@IsAdmin`

**Path Parameters:**
- `id`: ID do nível (Long)

**Response (204 NO CONTENT):**
```
Sem corpo de resposta
```

---

## Missões

### GET /missions
Lista todas as missões do sistema.

**Permissão:** `@CanReadQuests`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "levelId": 1,
    "levelName": "Iniciante",
    "title": "Primeira Missão",
    "description": "Complete sua primeira tarefa",
    "xpReward": 50,
    "orderNumber": 1,
    "createdAt": "2026-01-05T10:30:00",
    "updatedAt": "2026-01-05T10:30:00"
  }
]
```

---

### GET /missions/level/{levelId}
Lista todas as missões de um nível específico.

**Permissão:** `@CanReadQuests`

**Path Parameters:**
- `levelId`: ID do nível (Long)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "levelId": 1,
    "levelName": "Iniciante",
    "title": "Primeira Missão",
    "description": "Complete sua primeira tarefa",
    "xpReward": 50,
    "orderNumber": 1,
    "createdAt": "2026-01-05T10:30:00",
    "updatedAt": "2026-01-05T10:30:00"
  }
]
```

---

### GET /missions/{missionId}
Busca uma missão específica por ID.

**Permissão:** `@CanReadQuests`

**Path Parameters:**
- `missionId`: ID da missão (Long)

**Response (200 OK):**
```json
{
  "id": 1,
  "levelId": 1,
  "levelName": "Iniciante",
  "title": "Primeira Missão",
  "description": "Complete sua primeira tarefa",
  "xpReward": 50,
  "orderNumber": 1,
  "createdAt": "2026-01-05T10:30:00",
  "updatedAt": "2026-01-05T10:30:00"
}
```

---

### POST /missions
Cria uma nova missão (apenas ADMIN).

**Permissão:** `@IsAdmin`

**Request Body:**
```json
{
  "levelId": 1,
  "title": "Nova Missão",
  "description": "Descrição da missão",
  "xpReward": 100,
  "orderNumber": 1
}
```

**Validações:**
- `levelId`: Obrigatório
- `title`: Obrigatório
- `description`: Obrigatório
- `xpReward`: Obrigatório, mínimo 1
- `orderNumber`: Obrigatório, mínimo 1

**Response (201 CREATED):**
```json
{
  "id": 1,
  "levelId": 1,
  "levelName": "Iniciante",
  "title": "Nova Missão",
  "description": "Descrição da missão",
  "xpReward": 100,
  "orderNumber": 1,
  "createdAt": "2026-01-05T10:30:00",
  "updatedAt": "2026-01-05T10:30:00"
}
```

---

### PUT /missions/{missionId}
Atualiza uma missão existente (apenas ADMIN).

**Permissão:** `@IsAdmin`

**Path Parameters:**
- `missionId`: ID da missão (Long)

**Request Body:**
```json
{
  "title": "Missão Atualizada",
  "description": "Nova descrição",
  "xpReward": 150,
  "orderNumber": 1
}
```

**Validações:**
- `title`: Obrigatório
- `description`: Obrigatório
- `xpReward`: Obrigatório, mínimo 1
- `orderNumber`: Obrigatório, mínimo 1

**Response (200 OK):**
```json
{
  "id": 1,
  "levelId": 1,
  "levelName": "Iniciante",
  "title": "Missão Atualizada",
  "description": "Nova descrição",
  "xpReward": 150,
  "orderNumber": 1,
  "createdAt": "2026-01-05T10:30:00",
  "updatedAt": "2026-01-05T10:35:00"
}
```

---

### DELETE /missions/{missionId}
Deleta uma missão (apenas ADMIN).

**Permissão:** `@IsAdmin`

**Path Parameters:**
- `missionId`: ID da missão (Long)

**Response (204 NO CONTENT):**
```
Sem corpo de resposta
```

---

## Missões de Usuário

### GET /api/user-missions/my-missions/level/{levelId}
Lista as missões de um usuário em um nível específico.

**Permissão:** `@CanReadQuests`

**Path Parameters:**
- `levelId`: ID do nível (Long)

**Query Parameters:**
- `userId`: ID do usuário (Long)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "missionId": 1,
    "missionTitle": "Primeira Missão",
    "missionDescription": "Complete sua primeira tarefa",
    "xpReward": 50,
    "orderNumber": 1,
    "status": "COMPLETED",
    "submissionUrl": "https://github.com/user/repo",
    "submissionNotes": "Projeto concluído com sucesso",
    "feedback": "Excelente trabalho!",
    "evaluatedByName": "Mentor Silva",
    "startedAt": "2026-01-05T10:00:00",
    "submittedAt": "2026-01-05T12:00:00",
    "evaluatedAt": "2026-01-05T14:00:00",
    "completedAt": "2026-01-05T14:00:00"
  }
]
```

---

### POST /api/user-missions/{missionId}/start
Inicia uma missão para o usuário.

**Permissão:** `@CanInitiateQuests`

**Path Parameters:**
- `missionId`: ID da missão (Long)

**Query Parameters:**
- `userId`: ID do usuário (Long)

**Response (200 OK):**
```json
{
  "id": 1,
  "missionId": 1,
  "missionTitle": "Primeira Missão",
  "missionDescription": "Complete sua primeira tarefa",
  "xpReward": 50,
  "orderNumber": 1,
  "status": "IN_PROGRESS",
  "submissionUrl": null,
  "submissionNotes": null,
  "feedback": null,
  "evaluatedByName": null,
  "startedAt": "2026-01-05T10:00:00",
  "submittedAt": null,
  "evaluatedAt": null,
  "completedAt": null
}
```

---

### POST /api/user-missions/{missionId}/submit
Submete uma missão para avaliação.

**Permissão:** `@CanCompleteQuests`

**Path Parameters:**
- `missionId`: ID da missão (Long)

**Query Parameters:**
- `userId`: ID do usuário (Long)

**Request Body:**
```json
{
  "submissionUrl": "https://github.com/user/repo",
  "submissionNotes": "Observações sobre a submissão"
}
```

**Validações:**
- `submissionUrl`: Obrigatório, deve ser uma URL do GitHub
- `submissionNotes`: Opcional

**Response (200 OK):**
```json
{
  "id": 1,
  "missionId": 1,
  "missionTitle": "Primeira Missão",
  "missionDescription": "Complete sua primeira tarefa",
  "xpReward": 50,
  "orderNumber": 1,
  "status": "AWAITING_EVALUATION",
  "submissionUrl": "https://github.com/user/repo",
  "submissionNotes": "Observações sobre a submissão",
  "feedback": null,
  "evaluatedByName": null,
  "startedAt": "2026-01-05T10:00:00",
  "submittedAt": "2026-01-05T12:00:00",
  "evaluatedAt": null,
  "completedAt": null
}
```

---

### GET /api/user-missions/my-progress/level/{levelId}
Obtém o progresso do usuário em um nível específico.

**Permissão:** `@CanReadProfile`

**Path Parameters:**
- `levelId`: ID do nível (Long)

**Query Parameters:**
- `userId`: ID do usuário (Long)

**Response (200 OK):**
```json
{
  "levelId": 1,
  "levelName": "Iniciante",
  "totalMissions": 10,
  "completedMissions": 7,
  "progressPercentage": 70.0,
  "canUnlockBoss": false
}
```

---

### GET /api/user-missions/pending
Lista todas as missões pendentes de avaliação (paginado).

**Permissão:** `@CanReadPedingQuest`

**Query Parameters:**
- `page`: Número da página (default: 0)
- `size`: Tamanho da página (default: 20)
- `sort`: Ordenação (ex: "submittedAt,desc")

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "missionId": 1,
      "missionTitle": "Primeira Missão",
      "missionDescription": "Complete sua primeira tarefa",
      "xpReward": 50,
      "orderNumber": 1,
      "status": "AWAITING_EVALUATION",
      "submissionUrl": "https://github.com/user/repo",
      "submissionNotes": "Observações",
      "feedback": null,
      "evaluatedByName": null,
      "startedAt": "2026-01-05T10:00:00",
      "submittedAt": "2026-01-05T12:00:00",
      "evaluatedAt": null,
      "completedAt": null
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true
}
```

---

### POST /api/user-missions/{userMissionId}/evaluate
Avalia uma missão submetida (mentor/admin).

**Permissão:** `@CanQuestsEvaluate`

**Path Parameters:**
- `userMissionId`: ID da missão do usuário (Long)

**Query Parameters:**
- `userId`: ID do avaliador (Long)

**Request Body:**
```json
{
  "approved": true,
  "feedback": "Excelente trabalho! Todos os requisitos foram atendidos."
}
```

**Validações:**
- `approved`: Obrigatório (boolean)
- `feedback`: Obrigatório

**Response (200 OK):**
```json
{
  "id": 1,
  "missionId": 1,
  "missionTitle": "Primeira Missão",
  "missionDescription": "Complete sua primeira tarefa",
  "xpReward": 50,
  "orderNumber": 1,
  "status": "COMPLETED",
  "submissionUrl": "https://github.com/user/repo",
  "submissionNotes": "Observações",
  "feedback": "Excelente trabalho! Todos os requisitos foram atendidos.",
  "evaluatedByName": "Mentor Silva",
  "startedAt": "2026-01-05T10:00:00",
  "submittedAt": "2026-01-05T12:00:00",
  "evaluatedAt": "2026-01-05T14:00:00",
  "completedAt": "2026-01-05T14:00:00"
}
```

---

### GET /api/user-missions/my-evaluations
Lista todas as avaliações feitas pelo mentor/avaliador (paginado).

**Permissão:** `hasAnyAuthority('SCOPE_mentor', 'SCOPE_admin')`

**Query Parameters:**
- `userId`: ID do avaliador (Long)
- `page`: Número da página (default: 0)
- `size`: Tamanho da página (default: 20)
- `sort`: Ordenação

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "missionId": 1,
      "missionTitle": "Primeira Missão",
      "missionDescription": "Complete sua primeira tarefa",
      "xpReward": 50,
      "orderNumber": 1,
      "status": "COMPLETED",
      "submissionUrl": "https://github.com/user/repo",
      "submissionNotes": "Observações",
      "feedback": "Excelente trabalho!",
      "evaluatedByName": "Mentor Silva",
      "startedAt": "2026-01-05T10:00:00",
      "submittedAt": "2026-01-05T12:00:00",
      "evaluatedAt": "2026-01-05T14:00:00",
      "completedAt": "2026-01-05T14:00:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 5,
  "totalPages": 1,
  "last": true,
  "first": true
}
```

---

## Enumerações

### DifficutyLevel
Níveis de dificuldade disponíveis:
- `EASY` - Fácil
- `MEDIUM` - Médio
- `HARD` - Difícil
- `EXPERT` - Expert

### MissionStatus
Status possíveis para missões de usuário:
- `AVAILABLE` - Missão pode ser iniciada
- `IN_PROGRESS` - Aluno iniciou mas não submeteu
- `AWAITING_EVALUATION` - Aluno submeteu, aguardando mentor
- `COMPLETED` - Aprovada pelo mentor
- `FAILED` - Reprovada pelo mentor, pode reenviar

---

## Notas Importantes

### Autenticação
A maioria dos endpoints requer autenticação via JWT. O token deve ser incluído no header:
```
Authorization: Bearer {token}
```

### Paginação
Endpoints que retornam listas paginadas suportam os seguintes parâmetros:
- `page`: Número da página (começa em 0)
- `size`: Quantidade de itens por página
- `sort`: Campo e direção de ordenação (ex: "createdAt,desc")

### Códigos de Status HTTP
- `200 OK` - Requisição bem-sucedida
- `201 CREATED` - Recurso criado com sucesso
- `204 NO CONTENT` - Requisição bem-sucedida sem corpo de resposta
- `400 BAD REQUEST` - Dados inválidos na requisição
- `401 UNAUTHORIZED` - Autenticação necessária ou inválida
- `403 FORBIDDEN` - Sem permissão para acessar o recurso
- `404 NOT FOUND` - Recurso não encontrado
- `500 INTERNAL SERVER ERROR` - Erro interno do servidor

### Validações
Todos os campos marcados como obrigatórios devem ser fornecidos. Caso contrário, a API retornará um erro 400 com detalhes sobre os campos faltantes ou inválidos.

