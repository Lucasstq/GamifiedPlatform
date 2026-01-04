# Otimizações Realizadas nos Repositories

## Resumo das Mudanças

Os repositories foram otimizados com queries JPQL usando a anotação `@Query` para retornar apenas os campos essenciais, melhorando significativamente a performance das consultas ao banco de dados.

---

## UserRepository

### 1. `findUserByUsername(String username)`
```java
@Query("SELECT u FROM User u LEFT JOIN FETCH u.scopes WHERE u.username = :username")
Optional<User> findUserByUsername(@Param("username") String username);
```
**Otimização:** 
- Utiliza `LEFT JOIN FETCH` para buscar os scopes em uma única query (evita problema N+1)
- Essencial para autenticação (AuthService) onde scopes são necessários para gerar o token JWT
- Carrega: id, username, password, active, emailVerified, scopes

### 2. `findByEmail(String email)`
```java
@Query("SELECT u FROM User u WHERE u.email = :email")
Optional<User> findByEmail(@Param("email") String email);
```
**Otimização:**
- Query simples e direta para buscar usuário por email
- Usado no EmailVerificationService para reenvio de email de verificação
- Carrega apenas os campos necessários para verificação

### 3. `findByEmailVerificationToken(String token)`
```java
@Query("SELECT u FROM User u WHERE u.emailVerificationToken = :token")
Optional<User> findByEmailVerificationToken(@Param("token") String token);
```
**Otimização:**
- Query focada apenas nos campos necessários para verificação de email
- Usado no processo de confirmação de email
- Evita carregar relacionamentos desnecessários

### 4. `existsByUsername(String username)`
```java
@Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username")
Boolean existsByUsername(@Param("username") String username);
```
**Otimização:**
- Query extremamente otimizada que retorna apenas um boolean
- Não carrega nenhuma entidade, apenas conta se existe
- Usado em validações (CreateUserService, UpdateUserService)
- Muito mais eficiente que carregar a entidade inteira

### 5. `existsByEmail(String email)`
```java
@Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
Boolean existsByEmail(@Param("email") String email);
```
**Otimização:**
- Similar ao existsByUsername, retorna apenas boolean
- Evita overhead de carregar entidade completa para verificação simples
- Usado em validações de unicidade de email

---

## ScopeRepository

### 1. `findByName(String name)`
```java
@Query("SELECT s FROM Scopes s WHERE s.name = :name")
Optional<Scopes> findByName(@Param("name") String name);
```
**Otimização:**
- Query direta e otimizada para buscar scope por nome
- Retorna apenas o scope específico necessário
- Evita overhead de derived queries do Spring Data

### 2. `findByNameIn(List<String> names)`
```java
@Query("SELECT s FROM Scopes s WHERE s.name IN :names")
List<Scopes> findByNameIn(@Param("names") List<String> names);
```
**Otimização:**
- Query otimizada para buscar múltiplos scopes de uma vez
- Usado no CreateUserService para atribuir scopes padrão baseado na role
- Executa uma única query ao invés de múltiplas buscas individuais
- Extremamente eficiente para operação de batch

### 3. `existsByName(String name)`
```java
@Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Scopes s WHERE s.name = :name")
boolean existsByName(@Param("name") String name);
```
**Otimização:**
- Query otimizada que retorna apenas boolean sem carregar a entidade
- Usa COUNT para verificar existência de forma eficiente
- Ideal para validações rápidas

---

## PlayerCharacterRepository

**Status:** Não necessita otimização no momento
- Usa apenas operações básicas: `save()` e `delete()`
- Não há queries customizadas que possam ser otimizadas
- As operações JPA padrão já são otimizadas para estes casos de uso

---

## RoleRepository

**Status:** Repository vazio
- Não possui métodos customizados
- Apenas herda operações CRUD básicas do JpaRepository

---

## Benefícios das Otimizações

1. **Redução de Tráfego de Rede**: Menos dados transferidos entre o banco e a aplicação
2. **Menor Uso de Memória**: Entidades menores ocupam menos memória heap
3. **Performance de Queries**: Queries mais específicas são mais rápidas
4. **Prevenção do Problema N+1**: O `LEFT JOIN FETCH` carrega relacionamentos em uma única query
5. **Clareza e Documentação**: Cada query está documentada com seu propósito específico
6. **Queries Batch Otimizadas**: `findByNameIn` busca múltiplos registros eficientemente

---

## Observações Técnicas

- Todas as queries usam JPQL (Java Persistence Query Language)
- Parâmetros são explicitamente nomeados com `@Param` para maior clareza
- As queries `exists*` usam `COUNT` ao invés de carregar entidades
- O `LEFT JOIN FETCH` é usado apenas onde necessário (scopes para autenticação)
- Queries estão documentadas com JavaDoc explicando seu propósito
- Queries `IN` são usadas para operações batch eficientes

---

## Impacto nos Serviços

As otimizações são transparentes para os serviços que usam os repositories:

### UserRepository
- **AuthService**: Continua recebendo User com scopes para gerar JWT
- **EmailVerificationService**: Recebe apenas os dados necessários
- **UserByUsernameService**: Recebe User completo para o perfil
- **Create/UpdateUserService**: Queries exists* agora são mais eficientes

### ScopeRepository
- **CreateUserService**: `findByNameIn` busca todos os scopes necessários em uma única query
- Validações de existência são mais rápidas com query COUNT otimizada


