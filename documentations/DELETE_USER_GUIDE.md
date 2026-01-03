# Guia de Deleção de Usuário com Validação de Senha

## Visão Geral

Foi implementada uma regra de negócio que exige que o usuário forneça sua senha correta para deletar sua conta. Isso adiciona uma camada extra de segurança, evitando deleções acidentais ou não autorizadas.

## Implementação

### 1. Segurança de Senhas

As senhas agora são armazenadas com hash usando **BCrypt** (Spring Security Crypto):

- **Ao criar usuário**: A senha é automaticamente hasheada antes de ser salva no banco de dados
- **Ao atualizar usuário**: Se uma nova senha for fornecida, ela será hasheada
- **Ao deletar usuário**: A senha fornecida é comparada com o hash armazenado

### 2. Endpoint de Deleção

**DELETE** `/users/{id}`

**Request Body:**
```json
{
  "password": "senha_do_usuario"
}
```

**Respostas:**

- **204 No Content**: Usuário deletado com sucesso
- **401 Unauthorized**: Senha incorreta
  ```json
  {
    "timestamp": "2025-12-21T14:30:00",
    "status": 401,
    "error": "Unauthorized",
    "message": "Invalid password. Cannot delete user."
  }
  ```
- **404 Not Found**: Usuário não encontrado
  ```json
  {
    "timestamp": "2025-12-21T14:30:00",
    "status": 404,
    "error": "Not Found",
    "message": "User not found"
  }
  ```

### 3. Exemplo de Uso com cURL

```bash
# Deletar usuário com ID 1
curl -X DELETE http://localhost:8080/users/1 \
  -H "Content-Type: application/json" \
  -d '{"password": "minha_senha_segura"}'
```

## Arquivos Criados/Modificados

### Criados:
1. **DeleteUserRequest.java** - DTO para receber a senha na requisição de deleção
2. **InvalidPasswordException.java** - Exception customizada para senha inválida
3. **SecurityConfig.java** - Configuração do BCryptPasswordEncoder
4. **GlobalExceptionHandler.java** - Handler global para tratamento de exceções

### Modificados:
1. **UserService.java**
   - Adicionado `PasswordEncoder` como dependência
   - Método `createUser()` agora faz hash da senha
   - Método `updateUser()` faz hash da nova senha (se fornecida)
   - Método `deleteUser()` agora valida a senha antes de deletar

2. **UserController.java**
   - Adicionado endpoint DELETE `/users/{id}`
   - Aceita `DeleteUserRequest` no body

3. **pom.xml**
   - Adicionada dependência `spring-security-crypto`

## Fluxo de Deleção

1. Cliente envia requisição DELETE com o ID do usuário e a senha
2. UserService busca o usuário no banco de dados
3. A senha fornecida é comparada com o hash armazenado usando BCrypt
4. Se a senha estiver incorreta, lança `InvalidPasswordException` (401)
5. Se a senha estiver correta:
   - Deleta o PlayerCharacter associado (se existir)
   - Deleta o usuário
   - Retorna status 204 No Content

## Considerações de Segurança

- ✅ Senhas são armazenadas com hash BCrypt (nunca em texto puro)
- ✅ Comparação segura usando `passwordEncoder.matches()`
- ✅ Validação obrigatória da senha para deleção
- ✅ Mensagens de erro apropriadas sem expor informações sensíveis
- ✅ Deleção em cascata do PlayerCharacter associado

## Próximas Melhorias Sugeridas

1. Adicionar autenticação JWT para verificar que o usuário está deletando sua própria conta
2. Implementar rate limiting para prevenir ataques de força bruta
3. Adicionar logs de auditoria para rastrear deleções de conta
4. Implementar soft delete em vez de hard delete (manter registros históricos)
5. Adicionar confirmação por email antes de deletar a conta

