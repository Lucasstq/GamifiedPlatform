# Badge System API Documentation

## Overview
Sistema de badges (conquistas) que são desbloqueados automaticamente quando um usuário derrota um boss. Cada nível possui um badge épico associado.

## Endpoints

### GET /api/badges
Lista todos os badges disponíveis no sistema.

**Permissão:** `@PreAuthorize("isAuthenticated()")`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "levelId": 1,
    "levelName": "Aprendiz do Código",
    "levelOrder": 1,
    "name": "Vencedor da Sintaxe",
    "title": "Mestre dos Fundamentos",
    "description": "Concedido aos valentes que derrotaram o temível Syntax Sentinel...",
    "iconUrl": "https://img.icons8.com/color/48/code.png",
    "rarity": "EPIC",
    "createdAt": "2026-01-07T10:00:00"
  }
]
```

---

### GET /api/badges/user/{userId}
Lista todos os badges conquistados por um usuário específico.

**Permissão:** `@CanReadProfile`

**Path Parameters:**
- `userId`: ID do usuário (Long)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 5,
    "username": "johndoe",
    "badge": {
      "id": 1,
      "levelId": 1,
      "levelName": "Aprendiz do Código",
      "levelOrder": 1,
      "name": "Vencedor da Sintaxe",
      "title": "Mestre dos Fundamentos",
      "description": "Concedido aos valentes que derrotaram o temível Syntax Sentinel...",
      "iconUrl": "https://img.icons8.com/color/48/code.png",
      "rarity": "EPIC",
      "createdAt": "2026-01-07T10:00:00"
    },
    "unlockedAt": "2026-01-07T15:30:00",
    "unlockedByBossId": 1,
    "unlockedByBossName": null
  }
]
```

---

### GET /api/badges/user/{userId}/progress
Obtém o progresso de badges de um usuário (quantos conquistou vs total disponível).

**Permissão:** `@CanReadProfile`

**Path Parameters:**
- `userId`: ID do usuário (Long)

**Response (200 OK):**
```json
{
  "totalBadges": 6,
  "unlockedBadges": 2,
  "remainingBadges": 4,
  "progressPercentage": 33.33
}
```

---

### GET /users/{id}/profile
Obtém o perfil público completo de um usuário incluindo badges e informações do personagem.

**Permissão:** `@CanReadUsers`

**Path Parameters:**
- `id`: ID do usuário (Long)

**Response (200 OK):**
```json
{
  "id": 5,
  "username": "johndoe",
  "avatarUrl": "https://example.com/avatar.png",
  "createdAt": "2026-01-01T10:00:00",
  "characterName": "Shadow Warrior",
  "level": 3,
  "xp": 850,
  "badges": [
    {
      "id": 1,
      "userId": 5,
      "username": "johndoe",
      "badge": {
        "id": 1,
        "levelId": 1,
        "levelName": "Aprendiz do Código",
        "levelOrder": 1,
        "name": "Vencedor da Sintaxe",
        "title": "Mestre dos Fundamentos",
        "description": "Concedido aos valentes que derrotaram o temível Syntax Sentinel...",
        "iconUrl": "https://img.icons8.com/color/48/code.png",
        "rarity": "EPIC",
        "createdAt": "2026-01-07T10:00:00"
      },
      "unlockedAt": "2026-01-07T15:30:00",
      "unlockedByBossId": 1,
      "unlockedByBossName": null
    }
  ],
  "badgeProgress": {
    "totalBadges": 6,
    "unlockedBadges": 2,
    "remainingBadges": 4,
    "progressPercentage": 33.33
  }
}
```

---

## Integração com Boss Fights

### Desbloqueio Automático
Quando um usuário derrota um boss (status `DEFEATED` em `/api/bosses/fight/{userBossId}/evaluate`), o sistema automaticamente:

1. Concede XP ao personagem
2. **Desbloqueia o badge associado ao nível do boss derrotado**
3. Registra a data de desbloqueio e qual boss concedeu o badge

### Tratamento de Erros
- Se o badge já foi desbloqueado anteriormente: `BusinessException`
- Se o usuário não existe: `ResourceNotFoundException`
- Se o badge não existe para aquele nível: `ResourceNotFoundException`
- Se houver erro ao desbloquear o badge durante avaliação do boss: o erro é logado mas NÃO falha a avaliação

---

## Validações de Permissão

### Visualização de Badges
- **Listar todos os badges**: Qualquer usuário autenticado
- **Ver badges de um usuário**: Próprio usuário, Admin ou Mentor (`@CanReadProfile`)
- **Ver progresso de badges**: Próprio usuário, Admin ou Mentor (`@CanReadProfile`)
- **Ver perfil público com badges**: Próprio usuário, Admin ou Mentor (`@CanReadUsers`)

---

## Badges Épicos Disponíveis

1. **Vencedor da Sintaxe** (Nível 1) - Derrotar Syntax Sentinel
2. **Mestre dos Arrays** (Nível 2) - Derrotar Array Archon
3. **Arquiteto de Objetos** (Nível 3) - Derrotar Object Oracle
4. **Domador de Exceções** (Nível 4) - Derrotar Exception Executioner
5. **Mestre da Assincronicidade** (Nível 5) - Derrotar Async Assassin
6. **Caçador de Performance** (Nível 6) - Derrotar Performance Phantom

---

## Estrutura do Banco de Dados

### tb_badges
```sql
CREATE TABLE tb_badges (
    id          BIGSERIAL PRIMARY KEY,
    level_id    BIGINT       NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL UNIQUE,
    title       VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    icon_url    VARCHAR(500),
    rarity      VARCHAR(50)  NOT NULL DEFAULT 'EPIC',
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP
);
```

### tb_user_badges
```sql
CREATE TABLE tb_user_badges (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT    NOT NULL,
    badge_id            BIGINT    NOT NULL,
    unlocked_at         TIMESTAMP NOT NULL DEFAULT NOW(),
    unlocked_by_boss_id BIGINT,
    CONSTRAINT uk_user_badge UNIQUE (user_id, badge_id)
);
```

---

## Migrações

- **V17__create_tb_badges.sql**: Cria tabela de badges
- **V18__create_tb_user_badges.sql**: Cria tabela de relacionamento usuário-badges
- **V19__seed_badges.sql**: Insere os 6 badges épicos (um por nível)

---

## Caching

- **GetAllBadgesService**: Usa `@Cacheable(value = "allBadges")` para cachear a lista de badges disponíveis, pois não mudam frequentemente.

---

## Códigos de Status HTTP

- `200 OK`: Operação bem-sucedida
- `403 FORBIDDEN`: Usuário sem permissão
- `404 NOT FOUND`: Badge ou usuário não encontrado
- `409 CONFLICT`: Badge já desbloqueado (BusinessException)

