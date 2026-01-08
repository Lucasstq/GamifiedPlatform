# Boss API Endpoints Documentation

## Visão Geral

A API de Bosses permite que usuários enfrentem desafios épicos (bosses) ao completarem 80% das missões de um nível. Derrotar um boss concede XP, badges especiais e desbloqueia o próximo nível.

## Base URL

```
/api/bosses
```

## Endpoints

### 1. Listar Todos os Bosses

Lista todos os bosses disponíveis no sistema.

**Endpoint:** `GET /api/bosses`

**Autenticação:** Requerida

**Scopes necessários:** `bosses:read` ou `admin:all`

**Resposta de Sucesso:** `200 OK`

```json
[
  {
    "id": 1,
    "levelId": 1,
    "levelName": "Iniciante",
    "name": "Syntax Sentinel",
    "title": "O Guardião da Sintaxe",
    "description": "Um construto ancestral criado para proteger os fundamentos da programação...",
    "challenge": "Crie uma aplicação console que demonstre domínio completo de variáveis...",
    "xpReward": 150,
    "badgeName": "Vencedor da Sintaxe",
    "badgeDescription": "Derrotou o Syntax Sentinel e provou domínio dos fundamentos",
    "imageUrl": "https://img.icons8.com/color/96/sentinel.png",
    "badgeIconUrl": "https://img.icons8.com/color/48/code.png",
    "unlocksNextLevel": true
  }
]
```

---

### 2. Criar um Novo Boss

Cria um novo boss para um nível específico. Apenas administradores podem criar bosses. Cada nível pode ter apenas um boss.

**Endpoint:** `POST /api/bosses`

**Autenticação:** Requerida

**Scopes necessários:** `admin:all`

**Corpo da Requisição:**

```json
{
  "levelId": 1,
  "name": "Syntax Sentinel",
  "title": "O Guardião da Sintaxe",
  "description": "Um construto ancestral criado para proteger os fundamentos da programação...",
  "challenge": "Crie uma aplicação console que demonstre domínio completo de variáveis...",
  "xpReward": 150,
  "badgeName": "Vencedor da Sintaxe",
  "badgeDescription": "Derrotou o Syntax Sentinel e provou domínio dos fundamentos",
  "imageUrl": "https://img.icons8.com/color/96/sentinel.png",
  "badgeIconUrl": "https://img.icons8.com/color/48/code.png",
  "unlocksNextLevel": true
}
```

**Validações:**
- `levelId`: Obrigatório, deve ser um nível existente
- `name`: Obrigatório
- `title`: Obrigatório
- `description`: Obrigatória
- `challenge`: Obrigatório
- `xpReward`: Obrigatório, deve ser maior que 0
- `badgeName`: Obrigatório
- `badgeDescription`: Obrigatória
- `imageUrl`: Opcional
- `badgeIconUrl`: Opcional
- `unlocksNextLevel`: Opcional (default: true)

**Resposta de Sucesso:** `200 OK`

```json
{
  "id": 1,
  "levelId": 1,
  "levelName": "Iniciante",
  "name": "Syntax Sentinel",
  "title": "O Guardião da Sintaxe",
  "description": "Um construto ancestral criado para proteger os fundamentos da programação...",
  "challenge": "Crie uma aplicação console que demonstre domínio completo de variáveis...",
  "xpReward": 150,
  "badgeName": "Vencedor da Sintaxe",
  "badgeDescription": "Derrotou o Syntax Sentinel e provou domínio dos fundamentos",
  "imageUrl": "https://img.icons8.com/color/96/sentinel.png",
  "badgeIconUrl": "https://img.icons8.com/color/48/code.png",
  "unlocksNextLevel": true
}
```

**Erros Possíveis:**
- `400 Bad Request`: Dados inválidos ou já existe um boss para o nível
- `403 Forbidden`: Usuário não é administrador
- `404 Not Found`: Nível não encontrado

---

### 3. Verificar Progresso e Desbloquear Boss (por Nível)

Verifica o progresso do usuário em um nível específico e automaticamente desbloqueia o boss se o usuário atingir 80% de progresso nas missões.

**Endpoint:** `GET /api/bosses/level/{levelId}/progress`

**Autenticação:** Requerida

**Scopes necessários:** `bosses:read` ou `admin:all`

**Parâmetros de Path:**
- `levelId` (Long): ID do nível

**Parâmetros de Query:**
- `userId` (Long): ID do usuário (extraído do token JWT)

**Resposta de Sucesso:** `200 OK`

```json
{
  "levelId": 1,
  "levelName": "Iniciante",
  "totalMissions": 10,
  "completedMissions": 8,
  "progressPercentage": 80.0,
  "bossUnlocked": true,
  "bossStatus": {
    "id": 1,
    "bossId": 1,
    "bossName": "Syntax Sentinel",
    "bossTitle": "O Guardião da Sintaxe",
    "status": "UNLOCKED",
    "unlockedAt": "2026-01-07T10:30:00"
  }
}
```

**Possíveis Status do Boss:**
- `LOCKED`: Boss ainda bloqueado (< 80% de progresso)
- `UNLOCKED`: Boss desbloqueado, pode ser iniciado
- `IN_PROGRESS`: Luta em andamento
- `AWAITING_EVALUATION`: Submissão aguardando avaliação
- `DEFEATED`: Boss derrotado
- `FAILED`: Tentativa falhou, pode tentar novamente

---

### 3. Obter Progresso de Boss Específico

Obtém informações detalhadas sobre o progresso do usuário em relação a um boss específico.

**Endpoint:** `GET /api/bosses/{bossId}/progress`

**Autenticação:** Requerida

**Scopes necessários:** `bosses:read` ou `admin:all`

**Parâmetros de Path:**
- `bossId` (Long): ID do boss

**Resposta de Sucesso:** `200 OK`

```json
{
  "levelId": 1,
  "levelName": "Iniciante",
  "totalMissions": 10,
  "completedMissions": 10,
  "progressPercentage": 100.0,
  "bossUnlocked": true,
  "bossStatus": {
    "id": 1,
    "bossId": 1,
    "bossName": "Syntax Sentinel",
    "status": "DEFEATED",
    "submissionUrl": "https://github.com/user/boss-fight-1",
    "feedback": "Excelente implementação!",
    "evaluatedByName": "mentor@example.com",
    "completedAt": "2026-01-07T15:45:00"
  }
}
```

---

### 4. Iniciar Luta Contra Boss

Inicia uma luta contra um boss desbloqueado.

**Endpoint:** `POST /api/bosses/{bossId}/start`

**Autenticação:** Requerida

**Scopes necessários:** `bosses:fight` ou `admin:all`

**Parâmetros de Path:**
- `bossId` (Long): ID do boss

**Resposta de Sucesso:** `200 OK`

```json
{
  "id": 1,
  "bossId": 1,
  "bossName": "Syntax Sentinel",
  "bossTitle": "O Guardião da Sintaxe",
  "bossDescription": "Um construto ancestral...",
  "bossChallenge": "Crie uma aplicação console...",
  "xpReward": 150,
  "badgeName": "Vencedor da Sintaxe",
  "status": "IN_PROGRESS",
  "startedAt": "2026-01-07T16:00:00"
}
```

**Erros Possíveis:**
- `400 Bad Request`: Boss ainda está bloqueado
- `400 Bad Request`: Boss já foi derrotado
- `400 Bad Request`: Luta já está em progresso

---

### 5. Submeter Solução de Luta

Submete a solução desenvolvida para avaliação de um mentor/admin.

**Endpoint:** `POST /api/bosses/{bossId}/submit`

**Autenticação:** Requerida

**Scopes necessários:** `bosses:fight` ou `admin:all`

**Parâmetros de Path:**
- `bossId` (Long): ID do boss

**Corpo da Requisição:**

```json
{
  "submissionUrl": "https://github.com/user/boss-solution",
  "submissionNotes": "Implementei todas as funcionalidades solicitadas. Adicionei testes unitários e documentação completa."
}
```

**Validações:**
- `submissionUrl`: Obrigatória, deve ser uma URL do GitHub
- `submissionNotes`: Opcional

**Resposta de Sucesso:** `200 OK`

```json
{
  "id": 1,
  "bossId": 1,
  "bossName": "Syntax Sentinel",
  "status": "AWAITING_EVALUATION",
  "submissionUrl": "https://github.com/user/boss-solution",
  "submissionNotes": "Implementei todas as funcionalidades...",
  "submittedAt": "2026-01-07T18:30:00"
}
```

**Erros Possíveis:**
- `400 Bad Request`: Só pode submeter se status for IN_PROGRESS ou FAILED
- `400 Bad Request`: URL inválida

---

### 6. Avaliar Submissão de Boss Fight

Permite que mentores e admins avaliem submissões de boss fights. Se aprovado, o usuário recebe XP e badge automaticamente.

**Endpoint:** `POST /api/bosses/submissions/{userBossId}/evaluate`

**Autenticação:** Requerida

**Scopes necessários:** `bosses:evaluate` ou `admin:all`

**Parâmetros de Path:**
- `userBossId` (Long): ID do registro UserBoss

**Corpo da Requisição:**

```json
{
  "approved": true,
  "feedback": "Excelente trabalho! A implementação está completa, bem estruturada e demonstra domínio completo dos conceitos."
}
```

**Validações:**
- `approved`: Obrigatório (boolean)
- `feedback`: Obrigatório

**Resposta de Sucesso:** `200 OK`

```json
{
  "id": 1,
  "bossId": 1,
  "bossName": "Syntax Sentinel",
  "status": "DEFEATED",
  "submissionUrl": "https://github.com/user/boss-solution",
  "feedback": "Excelente trabalho!...",
  "evaluatedByName": "mentor@example.com",
  "evaluatedAt": "2026-01-07T20:00:00",
  "completedAt": "2026-01-07T20:00:00"
}
```

**Se Reprovado:**

```json
{
  "approved": false,
  "feedback": "A implementação está boa, mas faltou implementar a validação de entrada. Por favor, revise e reenvie."
}
```

**Resposta:**

```json
{
  "id": 1,
  "bossId": 1,
  "status": "FAILED",
  "feedback": "A implementação está boa, mas faltou...",
  "evaluatedByName": "mentor@example.com",
  "evaluatedAt": "2026-01-07T20:00:00"
}
```

**Erros Possíveis:**
- `400 Bad Request`: Só pode avaliar submissões com status AWAITING_EVALUATION
- `403 Forbidden`: Usuário não tem permissão para avaliar

---

### 7. Listar Boss Fights Pendentes de Avaliação

Lista todas as submissões de boss fights aguardando avaliação. Apenas para mentores e admins.

**Endpoint:** `GET /api/bosses/pending`

**Autenticação:** Requerida

**Scopes necessários:** `bosses:evaluate` ou `admin:all`

**Parâmetros de Query:**
- `page` (int): Número da página (default: 0)
- `size` (int): Tamanho da página (default: 20)
- `sort` (string): Campo de ordenação (default: submittedAt,asc)

**Resposta de Sucesso:** `200 OK`

```json
{
  "content": [
    {
      "id": 5,
      "bossId": 2,
      "bossName": "Array Archon",
      "bossTitle": "O Senhor das Estruturas de Dados",
      "status": "AWAITING_EVALUATION",
      "submissionUrl": "https://github.com/user/array-archon-solution",
      "submissionNotes": "Sistema completo com CRUD...",
      "submittedAt": "2026-01-07T10:00:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 5,
  "totalPages": 1
}
```

---

### 8. Listar Minhas Avaliações

Lista todas as avaliações de boss fights feitas pelo mentor autenticado.

**Endpoint:** `GET /api/bosses/my-evaluations`

**Autenticação:** Requerida

**Scopes necessários:** `bosses:evaluate` ou `admin:all`

**Parâmetros de Query:**
- `page` (int): Número da página (default: 0)
- `size` (int): Tamanho da página (default: 20)

**Resposta de Sucesso:** `200 OK`

```json
{
  "content": [
    {
      "id": 3,
      "bossId": 1,
      "bossName": "Syntax Sentinel",
      "status": "DEFEATED",
      "submissionUrl": "https://github.com/user/solution",
      "feedback": "Excelente trabalho!",
      "evaluatedByName": "mentor@example.com",
      "evaluatedAt": "2026-01-07T15:00:00",
      "completedAt": "2026-01-07T15:00:00"
    }
  ],
  "totalElements": 10,
  "totalPages": 1
}
```

---

## Fluxo de Uso Completo

### Para Estudantes:

1. **Completar Missões**: Complete pelo menos 80% das missões de um nível
2. **Verificar Desbloqueio**: `GET /api/bosses/level/{levelId}/progress`
3. **Ver Boss Disponível**: `GET /api/bosses` (para ver todos)
4. **Iniciar Luta**: `POST /api/bosses/{bossId}/start`
5. **Desenvolver Solução**: Trabalhar no desafio proposto
6. **Submeter Solução**: `POST /api/bosses/{bossId}/submit`
7. **Aguardar Avaliação**: Status muda para AWAITING_EVALUATION
8. **Receber Resultado**: Boss DEFEATED (XP + Badge) ou FAILED (tentar novamente)

### Para Mentores/Admins:

1. **Ver Pendentes**: `GET /api/bosses/pending`
2. **Avaliar Submissão**: `POST /api/bosses/submissions/{userBossId}/evaluate`
3. **Ver Histórico**: `GET /api/bosses/my-evaluations`

---

## Códigos de Status HTTP

- `200 OK`: Requisição bem-sucedida
- `400 Bad Request`: Dados inválidos ou regra de negócio violada
- `401 Unauthorized`: Token de autenticação ausente ou inválido
- `403 Forbidden`: Usuário não tem permissão para a ação
- `404 Not Found`: Recurso não encontrado

---

## Notas Importantes

1. **Desbloqueio Automático**: Bosses são desbloqueados automaticamente quando o endpoint de progresso é chamado e o usuário tem 80%+ de conclusão
2. **XP Automático**: XP é concedido automaticamente quando um boss é derrotado
3. **Badges**: Sistema de badges será implementado em versão futura (atualmente apenas logado)
4. **Reenvio**: Se uma submissão for reprovada (FAILED), o usuário pode resubmeter quantas vezes precisar
5. **Próximo Nível**: Derrotar um boss libera acesso ao próximo nível (se `unlocksNextLevel` for true)

---

## Scopes Necessários

- `bosses:read`: Permite ler informações sobre bosses e progresso
- `bosses:fight`: Permite iniciar e submeter soluções de boss fights
- `bosses:evaluate`: Permite avaliar submissões de boss fights (mentores/admins)
- `admin:all`: Acesso total a todas as funcionalidades

---

## Exemplos de Uso com cURL

### Verificar Progresso e Desbloquear Boss

```bash
curl -X GET "http://localhost:8080/api/bosses/level/1/progress?userId=1" \
  -H "Authorization: Bearer {token}"
```

### Iniciar Luta

```bash
curl -X POST "http://localhost:8080/api/bosses/1/start" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json"
```

### Submeter Solução

```bash
curl -X POST "http://localhost:8080/api/bosses/1/submit" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "submissionUrl": "https://github.com/user/boss-solution",
    "submissionNotes": "Implementação completa com testes"
  }'
```

### Avaliar Submissão

```bash
curl -X POST "http://localhost:8080/api/bosses/submissions/1/evaluate" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "approved": true,
    "feedback": "Excelente trabalho!"
  }'
```

