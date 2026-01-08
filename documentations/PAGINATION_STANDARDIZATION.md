# Padronização de Paginação - API REST

## Resumo das Mudanças

Este documento descreve a padronização implementada para converter todos os endpoints que retornavam `List<T>` para retornar `Page<T>`, melhorando a performance da API e a integração com o frontend.

## Objetivo

- **Padronizar** todos os endpoints de listagem para usar paginação
- **Melhorar** a performance da API evitando carregamento de dados desnecessários
- **Facilitar** a integração com o frontend que pode implementar scroll infinito ou paginação tradicional
- **Reduzir** o consumo de memória e largura de banda

## Services Modificados

### 1. **GetAllMissionsService**
- **Antes:** `List<MissionResponse> execute()`
- **Depois:** `Page<MissionResponse> execute(Pageable pageable)`
- **Controller:** `GET /missions?page=0&size=20&sort=id`

### 2. **GetMissionByLevelService**
- **Antes:** `List<MissionResponse> execute(Long levelId)`
- **Depois:** `Page<MissionResponse> execute(Long levelId, Pageable pageable)`
- **Controller:** `GET /missions/level/{levelId}?page=0&size=20&sort=orderNumber`

### 3. **GetAllBadgesService**
- **Antes:** `List<BadgeResponse> execute()`
- **Depois:** `Page<BadgeResponse> execute(Pageable pageable)`
- **Controller:** `GET /badges?page=0&size=20&sort=name`

### 4. **GetUserBadgesService**
- **Antes:** `List<UserBadgeResponse> execute(Long userId)`
- **Depois:** `Page<UserBadgeResponse> execute(Long userId, Pageable pageable)`
- **Controller:** `GET /badges/user/{userId}?page=0&size=20&sort=unlockedAt`

### 5. **GetAllGrimoiresService**
- **Antes:** `List<GrimoireResponse> execute()`
- **Depois:** `Page<GrimoireResponse> execute(Pageable pageable)`
- **Controller:** `GET /grimoires?page=0&size=20&sort=uploadedAt`

### 6. **GetLevelByDifficultyService**
- **Antes:** `List<LevelResponse> execute(DifficultyLevel difficulty)`
- **Depois:** `Page<LevelResponse> execute(DifficultyLevel difficulty, Pageable pageable)`
- **Controller:** `GET /levels/difficulty/{difficulty}?page=0&size=20&sort=orderLevel`

### 7. **GetUnlockLevelsService**
- **Antes:** `List<LevelResponse> execute(Integer currentXp)`
- **Depois:** `Page<LevelResponse> execute(Integer currentXp, Pageable pageable)`
- **Controller:** `GET /levels/unlocked?currentXp={xp}&page=0&size=20&sort=orderLevel`

### 8. **GetLockedLevelsService**
- **Antes:** `List<LevelResponse> execute(Integer currentXp)`
- **Depois:** `Page<LevelResponse> execute(Integer currentXp, Pageable pageable)`
- **Controller:** `GET /levels/locked?currentXp={xp}&page=0&size=20&sort=orderLevel`

### 9. **GetGlobalRankingService**
- **Antes:** `List<RankingResponse> execute(int page, int size)`
- **Depois:** `Page<RankingResponse> execute(Pageable pageable)`
- **Controller:** `GET /api/ranking?page=0&size=50`

### 10. **GetRankingByLevelService**
- **Antes:** `List<RankingResponse> execute(Long levelId, int page, int size)`
- **Depois:** `Page<RankingResponse> execute(Long levelId, Pageable pageable)`
- **Controller:** `GET /api/ranking/level/{levelId}?page=0&size=50`

## Controllers Modificados

Todos os controllers foram atualizados para receber `Pageable` via `@PageableDefault`:

```java
@GetMapping
public ResponseEntity<Page<Response>> getAll(
    @PageableDefault(size = 20, sort = "campo") Pageable pageable) {
    return ResponseEntity.ok(service.execute(pageable));
}
```

## Formato de Resposta Paginada

Todos os endpoints agora retornam um objeto `Page` com a seguinte estrutura:

```json
{
  "content": [
    // Array com os itens da página atual
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "pageNumber": 0,
    "pageSize": 20,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 5,
  "totalElements": 100,
  "last": false,
  "first": true,
  "size": 20,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 20,
  "empty": false
}
```

## Exemplos de Uso

### Paginação Básica
```
GET /missions?page=0&size=20
```

### Paginação com Ordenação
```
GET /badges?page=0&size=10&sort=name,asc
```

### Múltiplos Critérios de Ordenação
```
GET /missions/level/1?page=0&size=20&sort=orderNumber,asc&sort=title,asc
```

### Filtro com Paginação
```
GET /levels/difficulty/HARD?page=0&size=10&sort=orderLevel
```

## Benefícios

1. **Performance Melhorada:** Carregamento sob demanda reduz tempo de resposta
2. **Escalabilidade:** Suporta grandes volumes de dados sem sobrecarregar memória
3. **Flexibilidade:** Frontend pode controlar tamanho da página e ordenação
4. **Consistência:** Todos os endpoints seguem o mesmo padrão
5. **Metadados Úteis:** Informações sobre total de páginas, elementos, etc.

## Compatibilidade

### Breaking Changes

Esta mudança **quebra compatibilidade** com clientes existentes. Os frontends precisam ser atualizados para:

1. Enviar parâmetros de paginação (`page`, `size`, `sort`)
2. Processar a resposta `Page` ao invés de `List` direta
3. Acessar os dados via `response.content` ao invés de `response` diretamente

### 📱 Adaptação do Frontend

**Antes:**
```javascript
const missions = await api.get('/missions');
// missions = [...]
```

**Depois:**
```javascript
const response = await api.get('/missions?page=0&size=20');
// response = { content: [...], totalPages: 5, ... }
const missions = response.content;
```

## Testes

Recomenda-se testar todos os endpoints modificados para garantir:

- Paginação funciona corretamente
- Ordenação está aplicada
- Cache continua funcionando (onde aplicável)
- Total de elementos está correto

## Referências

- [Spring Data Pagination](https://docs.spring.io/spring-data/commons/docs/current/reference/html/#repositories.query-methods)
- [REST API Pagination Best Practices](https://www.baeldung.com/rest-api-pagination-in-spring)

