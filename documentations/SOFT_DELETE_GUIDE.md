# Soft Delete - Documentação

## Visão Geral

Sistema de soft delete implementado para usuários, permitindo "deletar" usuários sem remover permanentemente do banco de dados.

## Implementação

### Campos Adicionados

Na entidade `User`:

```java
@Column(name = "deleted")
private Boolean deleted = false;

@Column(name = "deleted_at")
private LocalDateTime deletedAt;
```

### Migração

```sql
ALTER TABLE tb_user ADD COLUMN deleted BOOLEAN DEFAULT false;
ALTER TABLE tb_user ADD COLUMN deleted_at TIMESTAMP;
CREATE INDEX idx_user_deleted ON tb_user(deleted) WHERE deleted = false;
```

## Comportamento

### Ao Deletar Usuário

Quando `DeleteUserService.execute()` é chamado:

1. Valida senha do usuário
2. Marca `deleted = true`
3. Define `deletedAt = now()`
4. Define `active = false`
5. Deleta personagem associado
6. **NÃO** remove do banco de dados

### Queries Atualizadas

Todas as queries de busca de usuários foram atualizadas para excluir deletados:

```java
// Antes
@Query("SELECT u FROM User u WHERE u.username = :username")

// Depois
@Query("SELECT u FROM User u WHERE u.username = :username AND u.deleted = false")
```

### Endpoints Afetados

- `findUserByUsername()` - Login
- `findByEmail()` - Verificação de email
- `findByEmailVerificationToken()` - Confirmação de email
- `existsByUsername()` - Validação de cadastro
- `existsByEmail()` - Validação de cadastro

## Vantagens

1. **Auditoria**: Mantém histórico completo
2. **Recuperação**: Possível restaurar usuários
3. **Integridade**: Mantém relações com dados históricos
4. **Compliance**: Atende requisitos de LGPD/GDPR com retenção

## Restauração

Para restaurar um usuário (adicionar endpoint se necessário):

```java
user.setDeleted(false);
user.setDeletedAt(null);
user.setActive(true);
userRepository.save(user);
```

## Hard Delete

Para deletar permanentemente (só admin):

```java
// Buscar incluindo deletados
User user = userRepository.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

// Deletar permanentemente
userRepository.delete(user);
```

## Índices de Performance

Índice parcial criado para otimizar queries de usuários não deletados:

```sql
CREATE INDEX idx_user_deleted ON tb_user(deleted) WHERE deleted = false;
CREATE INDEX idx_user_email_active ON tb_user(email) WHERE deleted = false AND active = true;
CREATE INDEX idx_user_username_active ON tb_user(username) WHERE deleted = false AND active = true;
```

## Estatísticas

Nova query para contar usuários ativos:

```java
@Query("SELECT COUNT(u) FROM User u WHERE u.deleted = false")
Long countActiveUsers();
```

Usado no dashboard admin para diferenciar:
- `totalUsers`: Todos os usuários (incluindo deletados)
- `activeUsers`: Apenas não deletados

## Considerações

1. **Email/Username**: Usuários deletados ainda ocupam email/username
   - Solução futura: Adicionar sufixo `_deleted_{timestamp}` ao deletar

2. **LGPD**: Soft delete não é anonimização
   - Implementar job de anonimização após período legal

3. **Performance**: Índices parciais garantem performance nas queries comuns

