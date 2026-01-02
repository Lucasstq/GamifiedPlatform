# Anotações de Segurança - Guia de Uso

Este documento descreve as anotações de segurança personalizadas criadas para simplificar a autorização nos controllers.

## Lista de Anotações

### Gerenciamento de Usuários

#### `@CanReadUsers`
Permite leitura de informações de usuários.
- **Scopes requeridos**: `SCOPE_admin:all` ou `SCOPE_users:read`
- **Uso típico**: Endpoints GET que retornam dados de usuários

```java
@GetMapping("/{id}")
@CanReadUsers
public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
    return ResponseEntity.ok(getUserById.execute(id));
}
```

#### `@CanWriteUsers`
Permite criar e atualizar usuários.
- **Scopes requeridos**: `SCOPE_admin:all` ou `SCOPE_users:write`
- **Uso típico**: Endpoints POST/PUT para criação/atualização de usuários

```java
@PostMapping
@CanWriteUsers
public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
    return ResponseEntity.ok(createUserService.execute(request));
}
```

#### `@CanDeleteUsers`
Permite deletar usuários.
- **Scopes requeridos**: `SCOPE_admin:all` ou `SCOPE_users:delete`
- **Uso típico**: Endpoints DELETE para remoção de usuários

---

### 🔐 Gerenciamento de Perfil (Próprio Usuário)

#### `@CanReadProfile`
Permite ler o próprio perfil.
- **Scopes requeridos**: `SCOPE_admin:all` ou `SCOPE_profile:read`
- **Uso típico**: Endpoints para visualização de perfil próprio

```java
@GetMapping("/me")
@CanReadProfile
public ResponseEntity<UserResponse> getMyProfile() {
    return ResponseEntity.ok(profileService.getProfile());
}
```

#### `@CanWriteProfile`
Permite atualizar o próprio perfil.
- **Scopes requeridos**: `SCOPE_admin:all` ou `SCOPE_profile:write`
- **Uso típico**: Endpoints de atualização de perfil

```java
@PutMapping("/{id}")
@CanWriteProfile
public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
    return ResponseEntity.ok(updateUser.execute(id, request));
}
```

#### `@CanDeleteProfile`
Permite deletar a própria conta.
- **Scopes requeridos**: `SCOPE_admin:all` ou `SCOPE_profile:delete`
- **Uso típico**: Endpoint de exclusão de conta

```java
@DeleteMapping("/{id}")
@CanDeleteProfile
public ResponseEntity<Void> deleteUser(@PathVariable Long id, @RequestBody DeleteUserRequest request) {
    deleteUser.execute(id, request.password());
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
}
```

---

### Gerenciamento de Personagens

#### `@CanReadCharacter`
Permite ler informações de personagens.
- **Scopes requeridos**: `SCOPE_admin:all` ou `SCOPE_character:read`

```java
@GetMapping("/{id}")
@CanReadCharacter
public ResponseEntity<CharacterResponse> getCharacter(@PathVariable Long id) {
    return ResponseEntity.ok(characterService.getById(id));
}
```

#### `@CanWriteCharacter`
Permite criar e atualizar personagens.
- **Scopes requeridos**: `SCOPE_admin:all` ou `SCOPE_character:write`

```java
@PostMapping
@CanWriteCharacter
public ResponseEntity<CharacterResponse> createCharacter(@RequestBody CharacterRequest request) {
    return ResponseEntity.ok(characterService.create(request));
}
```

#### `@CanDeleteCharacter`
Permite deletar personagens.
- **Scopes requeridos**: `SCOPE_admin:all` ou `SCOPE_character:delete`

---

### Gerenciamento de Missões (Quests)

#### `@CanReadQuests`
Permite ler missões.
- **Scopes requeridos**: `SCOPE_admin:all` ou `SCOPE_quests:read`

```java
@GetMapping
@CanReadQuests
public ResponseEntity<List<QuestResponse>> getAllQuests() {
    return ResponseEntity.ok(questService.findAll());
}
```

#### `@CanWriteQuests`
Permite criar e atualizar missões.
- **Scopes requeridos**: `SCOPE_admin:all` ou `SCOPE_quests:write`

```java
@PostMapping
@CanWriteQuests
public ResponseEntity<QuestResponse> createQuest(@RequestBody QuestRequest request) {
    return ResponseEntity.ok(questService.create(request));
}
```

#### `@CanCompleteQuests`
Permite completar missões.
- **Scopes requeridos**: `SCOPE_admin:all` ou `SCOPE_quests:complete`

```java
@PostMapping("/{id}/complete")
@CanCompleteQuests
public ResponseEntity<Void> completeQuest(@PathVariable Long id) {
    questService.complete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
}
```

---

### Gerenciamento de Conquistas (Achievements)

#### `@CanReadAchievements`
Permite ler conquistas.
- **Scopes requeridos**: `SCOPE_admin:all` ou `SCOPE_achievements:read`

```java
@GetMapping
@CanReadAchievements
public ResponseEntity<List<AchievementResponse>> getAllAchievements() {
    return ResponseEntity.ok(achievementService.findAll());
}
```

---

### Administração

#### `@IsAdmin`
Requer permissão administrativa completa.
- **Scopes requeridos**: `SCOPE_admin:all`
- **Uso típico**: Endpoints exclusivos para administradores

```java
@DeleteMapping("/admin/users/{id}")
@IsAdmin
public ResponseEntity<Void> forceDeleteUser(@PathVariable Long id) {
    adminService.forceDelete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
}
```

---

## Como Usar

### 1. Importar a Anotação

```java
import dev.gamified.GamifiedPlatform.config.annotations.CanReadUsers;
```

### 2. Aplicar no Método ou Classe
```java
// Aplicar em método específico
@GetMapping("/{id}")
@CanReadUsers
public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
    // ...
}

// Aplicar na classe inteira (todos os métodos herdam)
@RestController
@RequestMapping("/users")
@CanReadUsers
public class UserController {
    // Todos os métodos requerem SCOPE_admin:all ou SCOPE_users:read
}
```

---

## Matriz de Permissões por Role

| Anotação              | ROLE_USER | ROLE_MENTOR | ROLE_ADMIN |
|-----------------------|-----------|-------------|------------|
| @CanReadUsers         | ❌        | ✅          | ✅         |
| @CanWriteUsers        | ❌        | ❌          | ✅         |
| @CanDeleteUsers       | ❌        | ❌          | ✅         |
| @CanReadProfile       | ✅        | ✅          | ✅         |
| @CanWriteProfile      | ✅        | ✅          | ✅         |
| @CanDeleteProfile     | ✅        | ✅          | ✅         |
| @CanReadCharacter     | ✅        | ✅          | ✅         |
| @CanWriteCharacter    | ✅        | ✅          | ✅         |
| @CanDeleteCharacter   | ❌        | ❌          | ✅         |
| @CanReadQuests        | ✅        | ✅          | ✅         |
| @CanWriteQuests       | ❌        | ✅          | ✅         |
| @CanCompleteQuests    | ✅        | ✅          | ✅         |
| @CanReadAchievements  | ✅        | ✅          | ✅         |
| @IsAdmin              | ❌        | ❌          | ✅         |

---

## Dicas de Uso

1. **Combine com validações de negócio**: As anotações verificam permissões, mas você ainda precisa validar se o usuário pode acessar aquele recurso específico (ex: usuário só pode editar seu próprio perfil).

2. **Use em controllers, não em services**: As anotações devem ser usadas nas camadas de apresentação (controllers), não na lógica de negócio.

3. **Considere usar no nível da classe**: Se todos os endpoints de um controller requerem a mesma permissão, aplique a anotação na classe.

4. **Admin tem acesso a tudo**: O scope `SCOPE_admin:all` concede acesso a todos os endpoints anotados.

---

## Referência aos Scopes

Os scopes são definidos em `ScopeType.java`:

```java
public enum ScopeType {
    USERS_READ("users:read", "Ler informações de usuários"),
    USERS_WRITE("users:write", "Criar/atualizar usuários"),
    USERS_DELETE("users:delete", "Deletar usuários"),
    PROFILE_READ("profile:read", "Ler próprio perfil"),
    PROFILE_WRITE("profile:write", "Atualizar próprio perfil"),
    PROFILE_DELETE("profile:delete", "Deletar própria conta"),
    CHARACTER_READ("character:read", "Ler personagens"),
    CHARACTER_WRITE("character:write", "Criar/atualizar personagens"),
    CHARACTER_DELETE("character:delete", "Deletar personagens"),
    QUESTS_READ("quests:read", "Ler missões"),
    QUESTS_WRITE("quests:write", "Criar/atualizar missões"),
    QUESTS_COMPLETE("quests:complete", "Completar missões"),
    ACHIEVEMENTS_READ("achievements:read", "Ler conquistas"),
    ADMIN_ALL("admin:all", "Acesso administrativo total")
}
```

