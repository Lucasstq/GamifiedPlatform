# Anotações de Segurança - Quick Reference

## Localização
```
src/main/java/dev/gamified/GamifiedPlatform/config/annotations/
```

## Anotações Disponíveis

| Anotação | Scopes Requeridos | Descrição |
|----------|-------------------|-----------|
| `@CanReadUsers` | `admin:all` ou `users:read` | Ler informações de usuários |
| `@CanWriteUsers` | `admin:all` ou `users:write` | Criar/atualizar usuários |
| `@CanDeleteUsers` | `admin:all` ou `users:delete` | Deletar usuários |
| `@CanReadProfile` | `admin:all` ou `profile:read` | Ler próprio perfil |
| `@CanWriteProfile` | `admin:all` ou `profile:write` | Atualizar próprio perfil |
| `@CanDeleteProfile` | `admin:all` ou `profile:delete` | Deletar própria conta |
| `@CanReadCharacter` | `admin:all` ou `character:read` | Ler personagens |
| `@CanWriteCharacter` | `admin:all` ou `character:write` | Criar/atualizar personagens |
| `@CanDeleteCharacter` | `admin:all` ou `character:delete` | Deletar personagens |
| `@CanReadQuests` | `admin:all` ou `quests:read` | Ler missões |
| `@CanWriteQuests` | `admin:all` ou `quests:write` | Criar/atualizar missões |
| `@CanCompleteQuests` | `admin:all` ou `quests:complete` | Completar missões |
| `@CanReadAchievements` | `admin:all` ou `achievements:read` | Ler conquistas |
| `@IsAdmin` | `admin:all` | Acesso administrativo total |

## Exemplo de Uso Rápido

```java
import dev.gamified.GamifiedPlatform.config.annotations.*;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @GetMapping("/{id}")
    @CanReadUsers
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }
    
    @PutMapping("/{id}")
    @CanWriteProfile
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }
    
    @DeleteMapping("/{id}")
    @CanDeleteProfile
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

## Documentação Completa

Para documentação detalhada com todos os exemplos, consulte: [SECURITY_ANNOTATIONS.md](SECURITY_ANNOTATIONS.md)

## Referência de Exemplos

Para ver exemplos práticos de uso em diferentes cenários, consulte:
```
src/main/java/dev/gamified/GamifiedPlatform/controller/examples/SecurityAnnotationExamples.java
```

## Dica Rápida

Em vez de usar:
```java
@PreAuthorize("hasAnyAuthority('SCOPE_admin:all', 'SCOPE_users:read')")
```

Use simplesmente:
```java
@CanReadUsers
```

**Mais limpo, mais legível, mais manutenível!**

