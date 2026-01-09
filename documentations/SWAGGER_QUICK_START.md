# 🚀 Guia Rápido - Swagger/OpenAPI para Frontend

## ✅ O que foi implementado

### 1. Configuração Completa do Swagger/OpenAPI 3.0

✅ **Arquivo de Configuração**: `OpenApiConfig.java`
- Informações da API (título, descrição, versão, licença)
- Autenticação JWT Bearer configurada
- Tags organizadas por funcionalidade
- Servidor de desenvolvimento configurado

✅ **Configuração SpringDoc**: `application.yaml`
- Swagger UI habilitado em `/swagger-ui.html`
- OpenAPI JSON em `/v3/api-docs`
- Interface otimizada para desenvolvimento

### 2. Controllers Documentados

Todos os principais controllers foram completamente anotados com:
- ✅ **AuthController** - 7 endpoints de autenticação
- ✅ **UserController** - 6 endpoints de usuário
- ✅ **LevelQueryController** - 9 endpoints de níveis
- ✅ **MissionController** - 6 endpoints de missões
- ✅ **BossController** - 9 endpoints de bosses
- ✅ **BadgeController** - 3 endpoints de badges
- ✅ **GrimoireController** - 5 endpoints de grimórios
- ✅ **RankingController** - 4 endpoints de ranking
- ✅ **NotificationController** - 4 endpoints de notificações

**Total**: 53+ endpoints documentados!

## 🎯 Como Acessar

### Swagger UI (Interface Interativa)
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI JSON
```
http://localhost:8080/v3/api-docs
```

### OpenAPI YAML
```
http://localhost:8080/v3/api-docs.yaml
```

## 🔐 Como Usar a Autenticação

### Opção 1: Login Tradicional (Username/Password)

### 1. Fazer Login
```bash
POST /auth/login
Body: {
  "username": "seu_usuario",
  "password": "sua_senha"
}
```

### 2. Copiar o Token
- Copie o valor do campo `accessToken` da resposta

### 3. Autorizar no Swagger
- Clique no botão **"Authorize"** 🔒 (canto superior direito)
- Cole o token JWT (sem adicionar "Bearer ")
- Clique em "Authorize" e depois "Close"

### 4. Testar Endpoints
- Agora você pode testar qualquer endpoint protegido!
- O token é automaticamente enviado em todas as requisições

## 📱 Para o Frontend

### Exemplo de Requisição com Fetch

```javascript
// 1. Login
const loginResponse = await fetch('http://localhost:8080/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    username: 'user',
    password: 'pass'
  })
});

const { accessToken } = await loginResponse.json();

// 2. Usar o token para acessar endpoints protegidos
const levelsResponse = await fetch('http://localhost:8080/levels?page=0&size=10', {
  headers: {
    'Authorization': `Bearer ${accessToken}`
  }
});

const levels = await levelsResponse.json();
console.log(levels);
```

### Exemplo com Axios

```javascript
import axios from 'axios';

// Configurar axios com interceptor
const api = axios.create({
  baseURL: 'http://localhost:8080'
});

// Adicionar token automaticamente
api.interceptors.request.use(config => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Usar a API
const { data: levels } = await api.get('/levels');
const { data: missions } = await api.get('/missions/level/1');
```

## 🎨 Organização por Tags

Os endpoints estão organizados em 10 categorias:

1. **🔐 Autenticação** - Login, registro, logout
2. **👤 Usuários** - Perfis e gerenciamento
3. **📊 Níveis** - Sistema de progressão
4. **🎯 Missões** - Desafios e XP
5. **👹 Bosses** - Desafios épicos
6. **🏆 Badges** - Conquistas
7. **📚 Grimórios** - Materiais educacionais
8. **🏅 Ranking** - Classificação global
9. **🔔 Notificações** - Sistema de notificações
10. **⚙️ Admin** - Endpoints administrativos

## 📊 Paginação Padrão

Todos os endpoints de listagem suportam:

```javascript
// Parâmetros de paginação
{
  page: 0,        // Número da página (começa em 0)
  size: 20,       // Itens por página
  sort: 'name,asc' // Ordenação: campo,direção
}

// Exemplo
GET /levels?page=0&size=10&sort=orderLevel,asc
GET /missions?page=1&size=5&sort=createdAt,desc
```

### Resposta Paginada

```json
{
  "content": [...],      // Array de itens
  "pageable": {...},     // Informações da página
  "totalPages": 5,       // Total de páginas
  "totalElements": 50,   // Total de elementos
  "size": 10,           // Tamanho da página
  "number": 0,          // Número da página atual
  "first": true,        // É a primeira página?
  "last": false         // É a última página?
}
```

## 🎯 Principais Endpoints para Frontend

### Autenticação
- `POST /auth/login` - Login
- `POST /auth/register` - Registro
- `POST /auth/refresh` - Renovar token
- `POST /auth/logout` - Logout

### Usuário
- `GET /users/{id}` - Buscar usuário
- `GET /users/{id}/profile` - Perfil público
- `PUT /users/{id}` - Atualizar perfil
- `PATCH /users/{id}/change-password` - Mudar senha

### Níveis
- `GET /levels` - Listar todos
- `GET /levels/{id}` - Buscar por ID
- `GET /levels/user/{userId}` - Nível atual do usuário
- `GET /levels/unlocked?currentXp=1000` - Níveis desbloqueados

### Missões
- `GET /missions` - Listar todas
- `GET /missions/level/{levelId}` - Missões do nível
- `GET /missions/{id}` - Detalhes da missão

### Bosses
- `GET /bosses` - Listar todos
- `GET /bosses/{bossId}/progress` - Progresso do boss
- `POST /bosses/{bossId}/start` - Iniciar luta
- `POST /bosses/{bossId}/submit` - Submeter solução

### Badges
- `GET /badges` - Listar todos
- `GET /badges/user/{userId}` - Badges do usuário
- `GET /badges/user/{userId}/progress` - Progresso

### Grimórios
- `GET /grimoires` - Listar todos
- `GET /grimoires/{levelId}` - Info do grimório
- `GET /grimoires/{levelId}/download` - Download PDF

### Ranking
- `GET /api/ranking` - Ranking global
- `GET /api/ranking/me` - Minha posição
- `GET /api/ranking/level/{levelId}` - Ranking por nível

### Notificações
- `GET /notifications` - Minhas notificações
- `GET /notifications/unread/count` - Contador
- `PUT /notifications/{id}/read` - Marcar como lida
- `PUT /notifications/read-all` - Marcar todas

## 🔄 Gerar Cliente TypeScript Automaticamente

```bash
# Usando OpenAPI Generator
npx @openapitools/openapi-generator-cli generate \
  -i http://localhost:8080/v3/api-docs \
  -g typescript-fetch \
  -o ./src/generated-api

# Usando Swagger Codegen
npx swagger-codegen-cli generate \
  -i http://localhost:8080/v3/api-docs \
  -l typescript-axios \
  -o ./src/api-client
```

## 📝 Notas Importantes

### Tokens JWT
- **Access Token**: Expira em 15 minutos
- **Refresh Token**: Expira em 7 dias
- Use `/auth/refresh` para renovar o access token

### Permissões
Alguns endpoints requerem permissões específicas:
- `ADMIN` - Apenas administradores
- `MENTOR` - Mentores e admins
- `users:read`, `profile:write`, etc - Permissões por escopo

### CORS
O backend está configurado para aceitar requisições de:
- `http://localhost:3000`
- `http://localhost:8080`

## 🎉 Pronto para Usar!

A documentação Swagger está completa e pronta. Explore a interface interativa e teste todos os endpoints disponíveis!

**URL**: http://localhost:8080/swagger-ui.html

---

**Dúvidas?** Consulte a documentação completa em `documentations/SWAGGER_DOCUMENTATION.md`

