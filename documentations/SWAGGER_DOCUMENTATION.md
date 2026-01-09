# Documentação Swagger/OpenAPI - Gamified Platform

## 📚 Visão Geral

A API do Gamified Platform agora está completamente documentada com Swagger/OpenAPI 3.0, facilitando o desenvolvimento do frontend e a integração com outros sistemas.

## 🚀 Acesso à Documentação

Após iniciar a aplicação, a documentação estará disponível em:

- **Swagger UI (Interface Interativa)**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **OpenAPI YAML**: http://localhost:8080/v3/api-docs.yaml

## 🎯 Funcionalidades Implementadas

### 1. Configuração OpenAPI

**Arquivo**: `src/main/java/dev/gamified/GamifiedPlatform/config/OpenApiConfig.java`

- ✅ Informações da API (título, descrição, versão, contato, licença)
- ✅ Servidor de desenvolvimento configurado
- ✅ Autenticação JWT Bearer configurada
- ✅ Tags organizadas por domínio de funcionalidade
- ✅ Documentação abrangente no cabeçalho

### 2. Configuração Spring Doc

**Arquivo**: `src/main/resources/application.yaml`

```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
    enabled: true
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    operationsSorter: method
    tagsSorter: alpha
    displayRequestDuration: true
    tryItOutEnabled: true
    filter: true
  show-actuator: false
  default-consumes-media-type: application/json
  default-produces-media-type: application/json
```

### 3. Controllers Documentados

Todos os principais controllers foram anotados com documentação Swagger completa:

#### ✅ AuthController (`/auth`)
- `POST /auth/login` - Login com credenciais
- `POST /auth/refresh` - Renovar access token
- `POST /auth/logout` - Logout do dispositivo atual
- `POST /auth/logout-all-devices` - Logout de todos os dispositivos
- `POST /auth/register` - Registrar novo usuário
- `GET /auth/verify-email` - Verificar email
- `POST /auth/resend-verification` - Reenviar email de verificação

#### ✅ UserController (`/users`)
- `PUT /users/{id}` - Atualizar usuário
- `GET /users/{id}` - Buscar usuário por ID
- `GET /users/search` - Buscar usuário por username
- `GET /users/{id}/profile` - Buscar perfil público
- `PATCH /users/{id}/change-password` - Alterar senha
- `DELETE /users/{id}` - Deletar usuário (soft delete)

#### ✅ LevelQueryController (`/levels`)
- `GET /levels` - Listar todos os níveis
- `GET /levels/{id}` - Buscar nível por ID
- `GET /levels/order/{orderLevel}` - Buscar nível por ordem
- `GET /levels/difficulty/{difficulty}` - Buscar níveis por dificuldade
- `GET /levels/user/{userId}` - Buscar nível atual do usuário
- `GET /levels/next/{currentOrderLevel}` - Buscar próximo nível
- `GET /levels/unlocked` - Listar níveis desbloqueados
- `GET /levels/locked` - Listar níveis bloqueados
- `GET /levels/stats` - Obter estatísticas do sistema de níveis

#### ✅ MissionController (`/missions`)
- `GET /missions` - Listar todas as missões
- `GET /missions/level/{levelId}` - Listar missões por nível
- `GET /missions/{missionId}` - Buscar missão por ID
- `POST /missions` - Criar nova missão (Admin)
- `PUT /missions/{missionId}` - Atualizar missão (Admin)
- `DELETE /missions/{missionId}` - Deletar missão (Admin)

#### ✅ BossController (`/bosses`)
- `GET /bosses` - Listar todos os bosses
- `POST /bosses` - Criar novo boss (Admin)
- `GET /bosses/level/{levelId}/progress` - Verificar progresso do boss no nível
- `GET /bosses/{bossId}/progress` - Obter progresso do boss
- `POST /bosses/{bossId}/start` - Iniciar luta contra boss
- `POST /bosses/{bossId}/submit` - Submeter solução da luta
- `POST /bosses/submissions/{userBossId}/evaluate` - Avaliar submissão (Mentor/Admin)
- `GET /bosses/pending` - Listar avaliações pendentes (Mentor/Admin)
- `GET /bosses/my-evaluations` - Listar minhas avaliações (Mentor/Admin)

#### ✅ BadgeController (`/badges`)
- `GET /badges` - Listar todos os badges
- `GET /badges/user/{userId}` - Listar badges do usuário
- `GET /badges/user/{userId}/progress` - Obter progresso de badges

#### ✅ GrimoireController (`/grimoires`)
- `GET /grimoires` - Listar todos os grimórios
- `GET /grimoires/{levelId}` - Buscar informações do grimório
- `GET /grimoires/{levelId}/download` - Download do grimório (PDF)
- `POST /grimoires/admin/{levelId}` - Upload de grimório (Admin)
- `DELETE /grimoires/admin/{levelId}` - Deletar grimório (Admin)

#### ✅ RankingController (`/api/ranking`)
- `GET /api/ranking` - Buscar ranking global
- `GET /api/ranking/me` - Buscar minha posição no ranking
- `GET /api/ranking/level/{levelId}` - Buscar ranking por nível
- `POST /api/ranking/refresh` - Atualizar cache do ranking (Admin)

#### ✅ NotificationController (`/notifications`)
- `GET /notifications` - Listar minhas notificações
- `GET /notifications/unread/count` - Contar notificações não lidas
- `PUT /notifications/{notificationId}/read` - Marcar notificação como lida
- `PUT /notifications/read-all` - Marcar todas como lidas

## 🔐 Autenticação no Swagger UI

### Como Testar Endpoints Protegidos:

1. **Fazer Login**:
   - Navegue até o endpoint `POST /auth/login`
   - Clique em "Try it out"
   - Insira suas credenciais:
     ```json
     {
       "username": "seu_usuario",
       "password": "sua_senha"
     }
     ```
   - Clique em "Execute"
   - Copie o `accessToken` da resposta

2. **Autorizar no Swagger**:
   - Clique no botão **"Authorize"** no topo da página (ícone de cadeado)
   - Cole o token JWT no campo (não precisa adicionar "Bearer ", o Swagger faz isso automaticamente)
   - Clique em "Authorize"
   - Clique em "Close"

3. **Testar Endpoints**:
   - Agora todos os endpoints protegidos enviarão automaticamente o token JWT
   - Teste qualquer endpoint clicando em "Try it out"

## 📋 Anotações Utilizadas

### Anotações de Classe
```java
@Tag(name = "Nome", description = "Descrição do domínio")
@SecurityRequirement(name = "bearerAuth")
```

### Anotações de Método
```java
@Operation(
    summary = "Resumo curto",
    description = "Descrição detalhada"
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Sucesso"),
    @ApiResponse(responseCode = "404", description = "Não encontrado")
})
```

### Anotações de Parâmetro
```java
@Parameter(description = "Descrição do parâmetro", required = true)
```

## 🎨 Tags Organizadas

Os endpoints estão organizados nas seguintes tags:

1. **Autenticação** - Login, registro e gerenciamento de sessão
2. **Usuários** - Gerenciamento de perfis de usuário
3. **Níveis** - Sistema de progressão e níveis de conhecimento
4. **Missões** - Desafios de programação e sistema de XP
5. **Bosses** - Desafios épicos ao final de cada nível
6. **Badges** - Sistema de conquistas e distintivos
7. **Grimórios** - Materiais educacionais e PDFs desbloqueáveis
8. **Ranking** - Classificação global de jogadores
9. **Notificações** - Sistema de notificações em tempo real
10. **Admin** - Endpoints administrativos

## 📊 Paginação

Todos os endpoints que retornam listas suportam paginação via parâmetros query:

- `page`: Número da página (padrão: 0)
- `size`: Tamanho da página (padrão: 20)
- `sort`: Campo e direção (ex: `name,asc` ou `createdAt,desc`)

**Exemplo**:
```
GET /levels?page=0&size=10&sort=orderLevel,asc
```

## 🔍 Filtros e Buscas

Muitos endpoints suportam filtros adicionais:

- `/levels/difficulty/{difficulty}` - Filtra por dificuldade
- `/levels/unlocked?currentXp=1000` - Filtra por XP
- `/notifications?onlyUnread=true` - Filtra apenas não lidas

## 🎯 Códigos de Status HTTP

A API utiliza os seguintes códigos de status:

| Código | Significado | Uso |
|--------|------------|-----|
| 200 | OK | Requisição bem-sucedida |
| 201 | Created | Recurso criado com sucesso |
| 204 | No Content | Requisição bem-sucedida sem conteúdo |
| 400 | Bad Request | Dados inválidos |
| 401 | Unauthorized | Não autenticado |
| 403 | Forbidden | Sem permissão |
| 404 | Not Found | Recurso não encontrado |
| 429 | Too Many Requests | Rate limit excedido |
| 500 | Internal Server Error | Erro do servidor |

## 🛠️ Para Desenvolvedores Frontend

### Gerando Cliente Typescript/Javascript

Você pode gerar automaticamente um cliente TypeScript usando o OpenAPI Generator:

```bash
# Instalar o OpenAPI Generator
npm install @openapitools/openapi-generator-cli -g

# Gerar cliente TypeScript
openapi-generator-cli generate \
  -i http://localhost:8080/v3/api-docs \
  -g typescript-fetch \
  -o ./src/api-client
```

### Usando com Swagger Codegen

```bash
# Gerar cliente com Swagger Codegen
swagger-codegen generate \
  -i http://localhost:8080/v3/api-docs \
  -l typescript-axios \
  -o ./src/api-client
```

### Exemplo de Uso Direto (Fetch API)

```typescript
// Login
const response = await fetch('http://localhost:8080/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ username: 'user', password: 'pass' })
});
const { accessToken } = await response.json();

// Usar o token
const levels = await fetch('http://localhost:8080/levels', {
  headers: { 'Authorization': `Bearer ${accessToken}` }
});
const data = await levels.json();
```

## 🔄 Próximos Passos

### Controllers Pendentes

Os seguintes controllers ainda precisam ser documentados:

- [ ] `LevelAdminController` - Endpoints administrativos de níveis
- [ ] `UserMissionController` - Gerenciamento de missões do usuário
- [ ] `AdminDashboardController` - Dashboard administrativo

### Melhorias Futuras

- [ ] Adicionar exemplos de requisição/resposta
- [ ] Documentar modelos de dados (DTOs) com `@Schema`
- [ ] Adicionar descrições mais detalhadas para enums
- [ ] Configurar múltiplos ambientes (dev, staging, prod)
- [ ] Adicionar versionamento da API

## 📚 Recursos Adicionais

- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Swagger UI](https://swagger.io/tools/swagger-ui/)

## 🐛 Troubleshooting

### Swagger UI não carrega

1. Verifique se a aplicação está rodando
2. Acesse http://localhost:8080/swagger-ui.html (com barra no final)
3. Verifique o console do navegador para erros

### Endpoints não aparecem

1. Verifique se o controller tem `@RestController`
2. Verifique se os métodos têm annotations de mapeamento (`@GetMapping`, etc)
3. Reinicie a aplicação

### Autenticação não funciona

1. Faça login via `/auth/login` primeiro
2. Copie o `accessToken` (não o `refreshToken`)
3. No botão "Authorize", cole apenas o token (sem "Bearer ")
4. Verifique se o token não expirou (15 minutos)

## ✅ Checklist de Verificação

- [x] Dependência SpringDoc OpenAPI adicionada no `pom.xml`
- [x] Configuração SpringDoc no `application.yaml`
- [x] Classe `OpenApiConfig` criada e configurada
- [x] Controllers principais anotados
- [x] Autenticação JWT configurada no Swagger
- [x] Tags organizadas por domínio
- [x] Documentação testada e funcionando
