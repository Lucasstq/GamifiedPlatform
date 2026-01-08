# Documentação Dashboard Admin

## Visão Geral

Dashboard administrativo com estatísticas e métricas do sistema gamificado.

## Endpoints

### GET /admin/dashboard
Retorna estatísticas gerais do sistema.

**Autenticação:** Requerida
**Scopes necessários:** `admin:all`

**Resposta de Sucesso:** `200 OK`
```json
{
  "totalUsers": 150,
  "activeUsers": 142,
  "totalMissions": 45,
  "totalBosses": 9,
  "totalLevels": 9,
  "totalBadges": 9,
  "averageCompletionRate": 65.5,
  "missionDifficultyStats": {
    "missionId": 5,
    "missionTitle": "Missao Dificil",
    "totalAttempts": 50,
    "failedAttempts": 30,
    "failureRate": 60.0
  },
  "bossDefeatedStats": {
    "bossId": 3,
    "bossName": "Boss Dificil",
    "totalAttempts": 20,
    "defeated": 5,
    "defeatRate": 25.0
  }
}
```

### GET /admin/dashboard/missions/difficulty
Análise de dificuldade das missões.

**Autenticação:** Requerida
**Scopes necessários:** `admin:all`

**Resposta de Sucesso:** `200 OK`
```json
{
  "hardestMissions": [
    {
      "missionId": 5,
      "missionTitle": "Implementacao Avancada",
      "levelName": "Nivel 5",
      "totalSubmissions": 50,
      "approvedSubmissions": 20,
      "failedSubmissions": 30,
      "failureRate": 60.0,
      "approvalRate": 40.0
    }
  ],
  "easiestMissions": [
    {
      "missionId": 1,
      "missionTitle": "Hello World",
      "levelName": "Nivel 1",
      "totalSubmissions": 100,
      "approvedSubmissions": 95,
      "failedSubmissions": 5,
      "failureRate": 5.0,
      "approvalRate": 95.0
    }
  ],
  "averageFailureRate": 25.5
}
```

### GET /admin/dashboard/bosses/stats
Estatísticas dos bosses.

**Autenticação:** Requerida
**Scopes necessários:** `admin:all`

**Resposta de Sucesso:** `200 OK`
```json
{
  "undefeatedBosses": [
    {
      "bossId": 9,
      "bossName": "Boss Final",
      "levelName": "Nivel 9",
      "totalAttempts": 15,
      "totalDefeats": 0,
      "totalFailures": 15,
      "defeatRate": 0.0,
      "failureRate": 100.0
    }
  ],
  "mostDefeatedBosses": [
    {
      "bossId": 1,
      "bossName": "Primeiro Boss",
      "levelName": "Nivel 1",
      "totalAttempts": 100,
      "totalDefeats": 90,
      "totalFailures": 10,
      "defeatRate": 90.0,
      "failureRate": 10.0
    }
  ],
  "hardestBosses": [],
  "totalBossesCount": 9,
  "totalBossAttempts": 500,
  "averageDefeatRate": 55.5
}
```

### GET /admin/dashboard/levels/completion
Taxa de conclusão por nível.

**Autenticação:** Requerida
**Scopes necessários:** `admin:all`

**Resposta de Sucesso:** `200 OK`
```json
[
  {
    "levelId": 1,
    "levelName": "Fundamentos",
    "orderLevel": 1,
    "totalUsers": 100,
    "usersCompleted": 85,
    "completionRate": 85.0,
    "totalMissions": 5,
    "averageProgress": 0.0,
    "missionStats": [
      {
        "missionId": 1,
        "missionTitle": "Hello World",
        "totalAttempts": 100,
        "completed": 95,
        "failed": 5,
        "completionRate": 95.0
      }
    ]
  }
]
```

## Use Cases

### Identificar Gargalos
Use `/admin/dashboard/missions/difficulty` para identificar missões com alta taxa de reprovação que podem precisar de revisão.

### Balanceamento
Use `/admin/dashboard/bosses/stats` para identificar bosses muito fáceis ou muito difíceis.

### Acompanhamento de Progresso
Use `/admin/dashboard/levels/completion` para ver como os usuários estão progredindo pelos níveis.

## Métricas Importantes

- **Completion Rate**: % de usuários que completaram determinado conteúdo
- **Failure Rate**: % de tentativas que falharam
- **Defeat Rate**: % de tentativas bem-sucedidas contra bosses
- **Average Progress**: Progresso médio dos usuários em um nível

