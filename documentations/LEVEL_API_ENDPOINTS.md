# Level API - Endpoints Documentation

## 📋 Sumário de Endpoints

Total de **18 endpoints** para gerenciamento de níveis e gamificação.

---

## 🔐 Segurança

### Anotações Customizadas
- **@CanReadLevels** - Permite leitura de níveis (requer `SCOPE_admin:all` ou `SCOPE_levels:read`)
- **@CanManageLevels** - Permite gerenciar níveis (requer `SCOPE_admin:all` - apenas ADMIN)

---

## 📍 Endpoints CRUD

### 1. Criar Nível
```http
POST /levels
Authorization: Bearer {token}
Content-Type: application/json

Request Body:
{
  "orderLevel": 1,
  "name": "Iniciante das Sombras",
  "title": "O Despertar do Código",
  "description": "Sua jornada épica começa aqui...",
  "xpRequired": 0,
  "iconUrl": "/icons/shadow-initiate.png",
  "difficultyLevel": "EASY"
}

Response: 201 Created
{
  "id": 1,
  "orderLevel": 1,
  "name": "Iniciante das Sombras",
  "title": "O Despertar do Código",
  "description": "Sua jornada épica começa aqui...",
  "xpRequired": 0,
  "iconUrl": "/icons/shadow-initiate.png",
  "difficultyLevel": "EASY",
  "createdAt": "2026-01-05T11:00:00",
  "updatedAt": null
}
```

**Segurança**: `@CanManageLevels` (apenas ADMIN)

---

### 2. Listar Todos os Níveis
```http
GET /levels
Authorization: Bearer {token}

Response: 200 OK
[
  {
    "id": 1,
    "orderLevel": 1,
    "name": "Iniciante das Sombras",
    ...
  },
  {
    "id": 2,
    "orderLevel": 2,
    "name": "Aprendiz das Trevas",
    ...
  }
]
```

**Segurança**: `@CanReadLevels`

---

### 3. Buscar Nível por ID
```http
GET /levels/{id}
Authorization: Bearer {token}

Exemplo: GET /levels/1

Response: 200 OK
{
  "id": 1,
  "orderLevel": 1,
  "name": "Iniciante das Sombras",
  ...
}
```

**Segurança**: `@CanReadLevels`

---

### 4. Buscar Nível por Número de Ordem
```http
GET /levels/order/{orderLevel}
Authorization: Bearer {token}

Exemplo: GET /levels/order/1

Response: 200 OK
{
  "id": 1,
  "orderLevel": 1,
  "name": "Iniciante das Sombras",
  ...
}
```

**Segurança**: `@CanReadLevels`

---

### 5. Buscar Níveis por Dificuldade
```http
GET /levels/difficulty/{difficulty}
Authorization: Bearer {token}

Valores possíveis: EASY, MEDIUM, HARD, EXPERT

Exemplo: GET /levels/difficulty/EASY

Response: 200 OK
[
  {
    "id": 1,
    "difficultyLevel": "EASY",
    ...
  },
  {
    "id": 2,
    "difficultyLevel": "EASY",
    ...
  }
]
```

**Segurança**: `@CanReadLevels`

---

### 6. Atualizar Nível
```http
PUT /levels/{id}
Authorization: Bearer {token}
Content-Type: application/json

Exemplo: PUT /levels/1

Request Body:
{
  "orderLevel": 1,
  "name": "Iniciante das Sombras ATUALIZADO",
  "title": "O Despertar do Código",
  "description": "Descrição atualizada...",
  "xpRequired": 0,
  "iconUrl": "/icons/new-icon.png",
  "difficultyLevel": "EASY"
}

Response: 200 OK
{
  "id": 1,
  "name": "Iniciante das Sombras ATUALIZADO",
  ...
}
```

**Segurança**: `@CanManageLevels` (apenas ADMIN)

---

### 7. Deletar Nível
```http
DELETE /levels/{id}
Authorization: Bearer {token}

Exemplo: DELETE /levels/1

Response: 204 No Content
```

**Segurança**: `@CanManageLevels` (apenas ADMIN)

---

## 🎮 Endpoints de Gamificação

### 8. Buscar Nível do Usuário
```http
GET /levels/user/{userId}
Authorization: Bearer {token}

Exemplo: GET /levels/user/1

Response: 200 OK
{
  "id": 3,
  "orderLevel": 3,
  "name": "Guerreiro Sombrio",
  "xpRequired": 1500,
  ...
}
```

**Segurança**: Apenas o próprio usuário ou ADMIN

**Descrição**: Busca o nível atual do personagem do usuário baseado em seu XP acumulado.

---

### 9. Calcular Nível por XP
```http
GET /levels/calculate-by-xp?xp={xp}
Authorization: Bearer {token}

Exemplo: GET /levels/calculate-by-xp?xp=2500

Response: 200 OK
{
  "id": 3,
  "orderLevel": 3,
  "name": "Guerreiro Sombrio",
  "xpRequired": 1500,
  ...
}
```

**Segurança**: `@CanReadLevels`

**Descrição**: Calcula qual nível da tabela corresponde ao XP informado.

---

### 10. Buscar Próximo Nível
```http
GET /levels/next/{currentOrderLevel}
Authorization: Bearer {token}

Exemplo: GET /levels/next/3

Response: 200 OK
{
  "id": 4,
  "orderLevel": 4,
  "name": "Mestre das Trevas",
  "xpRequired": 3000,
  ...
}
```

**Segurança**: `@CanReadLevels`

**Descrição**: Retorna o próximo nível na progressão.

---

### 11. Calcular XP para Próximo Nível
```http
GET /levels/xp-to-next?currentXp={xp}&currentOrderLevel={order}
Authorization: Bearer {token}

Exemplo: GET /levels/xp-to-next?currentXp=2500&currentOrderLevel=3

Response: 200 OK
500
```

**Segurança**: `@CanReadLevels`

**Descrição**: Calcula quantos XP faltam para alcançar o próximo nível.

---

### 12. Verificar se Pode Desbloquear Nível
```http
GET /levels/can-unlock?currentXp={xp}&levelId={id}
Authorization: Bearer {token}

Exemplo: GET /levels/can-unlock?currentXp=2500&levelId=4

Response: 200 OK
false
```

**Segurança**: `@CanReadLevels`

**Descrição**: Verifica se o jogador tem XP suficiente para desbloquear um nível específico.

---

### 13. Listar Níveis Desbloqueados
```http
GET /levels/unlocked?currentXp={xp}
Authorization: Bearer {token}

Exemplo: GET /levels/unlocked?currentXp=2500

Response: 200 OK
[
  {
    "id": 1,
    "orderLevel": 1,
    "xpRequired": 0,
    ...
  },
  {
    "id": 2,
    "orderLevel": 2,
    "xpRequired": 500,
    ...
  },
  {
    "id": 3,
    "orderLevel": 3,
    "xpRequired": 1500,
    ...
  }
]
```

**Segurança**: `@CanReadLevels`

**Descrição**: Retorna todos os níveis que o jogador já desbloqueou com o XP atual.

---

### 14. Listar Níveis Bloqueados
```http
GET /levels/locked?currentXp={xp}
Authorization: Bearer {token}

Exemplo: GET /levels/locked?currentXp=2500

Response: 200 OK
[
  {
    "id": 4,
    "orderLevel": 4,
    "xpRequired": 3000,
    ...
  },
  {
    "id": 5,
    "orderLevel": 5,
    "xpRequired": 5000,
    ...
  }
]
```

**Segurança**: `@CanReadLevels`

**Descrição**: Retorna os níveis que ainda não foram desbloqueados.

---

### 15. Calcular Progresso no Nível
```http
GET /levels/progress?currentXp={xp}&currentOrderLevel={order}
Authorization: Bearer {token}

Exemplo: GET /levels/progress?currentXp=2500&currentOrderLevel=3

Response: 200 OK
66.67
```

**Segurança**: `@CanReadLevels`

**Descrição**: Calcula o progresso percentual (0-100%) dentro do nível atual.

---

### 16. Estatísticas do Sistema
```http
GET /levels/stats
Authorization: Bearer {token}

Response: 200 OK
{
  "totalLevels": 10,
  "minXpRequired": 0,
  "maxXpRequired": 50000,
  "easiestLevel": {
    "id": 1,
    "orderLevel": 1,
    "difficultyLevel": "EASY",
    ...
  },
  "hardestLevel": {
    "id": 10,
    "orderLevel": 10,
    "difficultyLevel": "EXPERT",
    ...
  }
}
```

**Segurança**: `@CanReadLevels`

**Descrição**: Retorna estatísticas gerais sobre o sistema de níveis.

---

## 🛠️ Validações

### LevelRequest
```json
{
  "orderLevel": "Obrigatório, mínimo 1",
  "name": "Obrigatório, máximo 100 caracteres",
  "title": "Obrigatório, máximo 200 caracteres",
  "description": "Obrigatório, máximo 1000 caracteres",
  "xpRequired": "Obrigatório, mínimo 0",
  "iconUrl": "Opcional",
  "difficultyLevel": "Obrigatório (EASY, MEDIUM, HARD, EXPERT)"
}
```

---

## ❌ Códigos de Erro

| Código | Descrição |
|--------|-----------|
| 200 | OK - Sucesso |
| 201 | Created - Recurso criado |
| 204 | No Content - Operação concluída sem retorno |
| 400 | Bad Request - Validação falhou |
| 401 | Unauthorized - Token inválido/ausente |
| 403 | Forbidden - Sem permissão |
| 404 | Not Found - Recurso não encontrado |
| 500 | Internal Server Error - Erro no servidor |

---

## 📝 Exemplos de Uso Completo

### Cenário: Jogador Progredindo

```http
# 1. Jogador verifica seu nível atual
GET /levels/user/1
Response: Level 3 (2500 XP)

# 2. Verifica próximo nível
GET /levels/next/3
Response: Level 4 (requer 3000 XP)

# 3. Calcula XP faltante
GET /levels/xp-to-next?currentXp=2500&currentOrderLevel=3
Response: 500 XP

# 4. Verifica progresso
GET /levels/progress?currentXp=2500&currentOrderLevel=3
Response: 66.67%

# 5. Lista níveis desbloqueados
GET /levels/unlocked?currentXp=2500
Response: [Level 1, Level 2, Level 3]

# 6. Verifica se pode desbloquear próximo
GET /levels/can-unlock?currentXp=2500&levelId=4
Response: false
```

---

## 🔑 Autenticação

Todos os endpoints requerem autenticação via JWT Bearer Token:

```http
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 📦 Arquivos Criados

1. **LevelController.java** - 18 endpoints REST
2. **CanReadLevels.java** - Anotação de segurança para leitura
3. **CanManageLevels.java** - Anotação de segurança para gerenciamento
4. **Correções em serviços existentes**

---

## ✅ Status da Implementação

- ✅ CRUD completo (7 endpoints)
- ✅ Gamificação avançada (9 endpoints)
- ✅ Segurança configurada
- ✅ Validações implementadas
- ✅ Compilação bem-sucedida
- ✅ Pronto para uso!

---

**Desenvolvido para**: Plataforma Educacional Gamificada Dark Fantasy  
**Data**: 05/01/2026  
**Status**: ✅ **COMPLETO E FUNCIONAL**

