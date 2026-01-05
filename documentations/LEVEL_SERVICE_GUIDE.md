# Level Service - Guia de Implementação

## 📚 Visão Geral

O **LevelService** foi criado para gerenciar o sistema de níveis da plataforma educacional gamificada Dark Fantasy. Este serviço controla a progressão dos jogadores através de níveis crescentes de dificuldade, desde fundamentos de programação até arquitetura avançada.

## 🗂️ Arquivos Criados

### 1. **LevelService.java**
- **Localização**: `src/main/java/dev/gamified/GamifiedPlatform/services/levels/LevelService.java`
- **Responsabilidade**: Lógica de negócio para gerenciamento de níveis
- **Linhas de código**: 279

### 2. **LevelRepository.java** (Atualizado)
- **Localização**: `src/main/java/dev/gamified/GamifiedPlatform/repository/LevelRepository.java`
- **Melhorias**: 
  - Corrigido generics (era `JpaRepository<Long, Levels>`, agora é `JpaRepository<Levels, Long>`)
  - Adicionados métodos de consulta customizados

### 3. **LevelRequest.java**
- **Localização**: `src/main/java/dev/gamified/GamifiedPlatform/dtos/request/LevelRequest.java`
- **Tipo**: Record DTO com validações
- **Campos validados**: orderLevel, name, title, description, xpRequired, iconUrl, difficultyLevel

### 4. **LevelResponse.java**
- **Localização**: `src/main/java/dev/gamified/GamifiedPlatform/dtos/response/LevelResponse.java`
- **Tipo**: Record DTO com @Builder
- **Campos**: id, orderLevel, name, title, description, xpRequired, iconUrl, difficultyLevel, createdAt, updatedAt

### 5. **LevelMapper.java**
- **Localização**: `src/main/java/dev/gamified/GamifiedPlatform/mapper/LevelMapper.java`
- **Métodos**: 
  - `toEntity(LevelRequest)` - Converte request para entidade
  - `toResponse(Levels)` - Converte entidade para response
  - `updateEntityFromRequest(Levels, LevelRequest)` - Atualiza entidade existente

## 🎯 Funcionalidades Principais

### CRUD Básico

#### 1. **Criar Nível**
```java
public LevelResponse createLevel(LevelRequest request)
```
- Valida se o orderLevel já existe
- Cria um novo nível no sistema
- Retorna o nível criado

#### 2. **Buscar Nível por ID**
```java
public LevelResponse getLevelById(Long id)
```
- Busca um nível específico
- Lança exceção se não encontrado

#### 3. **Buscar Nível por Ordem**
```java
public LevelResponse getLevelByOrder(Integer orderLevel)
```
- Busca pelo número de ordem (1, 2, 3, etc.)
- Útil para progressão sequencial

#### 4. **Listar Todos os Níveis**
```java
public List<LevelResponse> getAllLevels()
```
- Retorna todos os níveis ordenados por orderLevel
- Lista completa da jornada de aprendizado

#### 5. **Buscar por Dificuldade**
```java
public List<LevelResponse> getLevelsByDifficulty(DifficutyLevel difficulty)
```
- Filtra níveis por dificuldade (EASY, MEDIUM, HARD, EXPERT)

#### 6. **Atualizar Nível**
```java
public LevelResponse updateLevel(Long id, LevelRequest request)
```
- Atualiza dados de um nível existente
- Valida se novo orderLevel não conflita com outro

#### 7. **Deletar Nível**
```java
public void deleteLevel(Long id)
```
- Remove um nível do sistema
- ⚠️ Usar com cuidado em produção

### Funcionalidades de Gamificação

#### 8. **Calcular Nível por XP**
```java
public LevelResponse calculateLevelByXp(Integer currentXp)
```
- Determina qual nível o jogador alcançou baseado no XP atual
- Retorna o nível mais alto que pode ser desbloqueado

#### 9. **Obter Próximo Nível**
```java
public LevelResponse getNextLevel(Integer currentOrderLevel)
```
- Retorna o próximo nível na progressão
- Lança exceção se já está no nível máximo

#### 10. **Calcular XP para Próximo Nível**
```java
public Integer calculateXpToNextLevel(Integer currentXp, Integer currentOrderLevel)
```
- Calcula quantos XP faltam para subir de nível
- Retorna 0 se já está no nível máximo

#### 11. **Verificar se Pode Desbloquear**
```java
public boolean canUnlockLevel(Integer currentXp, Long levelId)
```
- Verifica se o jogador tem XP suficiente para um nível específico

#### 12. **Listar Níveis Desbloqueados**
```java
public List<LevelResponse> getUnlockedLevels(Integer currentXp)
```
- Retorna todos os níveis que o jogador já pode acessar

#### 13. **Listar Níveis Bloqueados**
```java
public List<LevelResponse> getLockedLevels(Integer currentXp)
```
- Retorna níveis que ainda precisam ser desbloqueados

#### 14. **Calcular Progresso no Nível**
```java
public Double calculateLevelProgress(Integer currentXp, Integer currentOrderLevel)
```
- Calcula progresso percentual (0-100%) dentro do nível atual
- Útil para barras de progresso na UI

#### 15. **Estatísticas do Sistema**
```java
public LevelSystemStats getSystemStats()
```
- Retorna estatísticas gerais sobre todos os níveis
- Inclui: total de níveis, XP mínimo/máximo, níveis mais fácil/difícil

## 📊 Consultas Personalizadas do Repository

```java
Optional<Levels> findByOrderLevel(Integer orderLevel)
List<Levels> findByDifficultyLevel(DifficutyLevel difficultyLevel)
List<Levels> findAllByOrderByOrderLevelAsc()
boolean existsByOrderLevel(Integer orderLevel)
Optional<Levels> findTopByOrderLevelLessThanEqualOrderByOrderLevelDesc(Integer orderLevel)
```

## 🎮 Exemplo de Uso

### Criar um Nível
```java
LevelRequest request = new LevelRequest(
    1, // orderLevel
    "Iniciante das Sombras", // name
    "O Despertar do Código", // title
    "Sua jornada começa aqui. Aprenda os fundamentos da programação...", // description
    0, // xpRequired
    "/icons/level-1.png", // iconUrl
    DifficutyLevel.EASY // difficultyLevel
);

LevelResponse response = levelService.createLevel(request);
```

### Calcular Progressão do Jogador
```java
// Jogador tem 2500 XP e está no nível 3
Integer currentXp = 2500;
Integer currentOrderLevel = 3;

// Calcular nível alcançado
LevelResponse achievedLevel = levelService.calculateLevelByXp(currentXp);

// Calcular XP faltante para próximo nível
Integer xpNeeded = levelService.calculateXpToNextLevel(currentXp, currentOrderLevel);

// Calcular progresso percentual
Double progress = levelService.calculateLevelProgress(currentXp, currentOrderLevel);
```

## ✅ Validações Implementadas

### LevelRequest
- **orderLevel**: Não nulo, mínimo 1
- **name**: Não vazio, máximo 100 caracteres
- **title**: Não vazio, máximo 200 caracteres
- **description**: Não vazio, máximo 1000 caracteres
- **xpRequired**: Não nulo, mínimo 0
- **difficultyLevel**: Não nulo (EASY, MEDIUM, HARD, EXPERT)

### Validações de Negócio
- ✅ Impede criação de níveis com orderLevel duplicado
- ✅ Valida existência de níveis antes de atualizar/deletar
- ✅ Garante que orderLevel é sempre positivo
- ✅ Retorna valores seguros (sem negativos em cálculos de XP)

## 🚀 Próximos Passos Sugeridos

1. **Criar LevelController** para expor endpoints REST
2. **Implementar testes unitários** para LevelService
3. **Criar migration Flyway** (V8__create_tb_levels.sql) para criar a tabela
4. **Integrar com PlayerCharacter** para atualizar níveis automaticamente
5. **Adicionar eventos** quando jogador sobe de nível (ApplicationEvent)
6. **Criar seeds** com níveis padrão do sistema

## 🔐 Considerações de Segurança

- Os métodos de CRUD (criar, atualizar, deletar) devem ser protegidos com `@PreAuthorize("hasRole('ADMIN')")`
- Os métodos de consulta podem ser acessíveis para usuários autenticados
- As estatísticas podem ser públicas

## 📦 Dependências

- Spring Data JPA
- Lombok
- Jakarta Validation
- Jakarta Transaction

## ✨ Compilação

O projeto compila com sucesso! ✅

```bash
./mvnw clean compile
# BUILD SUCCESS
```

---

**Criado para**: Plataforma Educacional Gamificada Dark Fantasy  
**Versão**: 1.0  
**Data**: 05/01/2026

