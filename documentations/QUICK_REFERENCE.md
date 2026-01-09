# 🚀 Swagger - Cartão de Referência Rápida

## 📍 URLs Principais

```
Swagger UI:  http://localhost:8080/swagger-ui.html
OpenAPI JSON: http://localhost:8080/v3/api-docs
Base API:     http://localhost:8080
```

## 🔐 Autenticação Rápida

### 1. Login
```bash
POST /auth/login
{
  "username": "seu_usuario",
  "password": "sua_senha"
}
```

### 2. Usar Token no Swagger
1. Copie o `accessToken` da resposta
2. Clique em **"Authorize"** 🔒
3. Cole o token
4. Clique em "Authorize" → "Close"

### 3. Usar Token no Código
```javascript
headers: {
  'Authorization': `Bearer ${accessToken}`
}
```

## 📋 Endpoints Mais Usados

### Autenticação
```
POST   /auth/login           # Login tradicional
POST   /auth/register        # Registro
POST   /auth/refresh         # Renovar token
POST   /auth/logout          # Logout
GET    /oauth2/authorization/google   # Login com Google
GET    /oauth2/authorization/github   # Login com GitHub
```

### Usuários
```
GET    /users/{id}           # Buscar usuário
GET    /users/{id}/profile   # Perfil público
PUT    /users/{id}           # Atualizar
```

### Níveis
```
GET    /levels               # Listar todos
GET    /levels/{id}          # Buscar por ID
GET    /levels/user/{userId} # Nível do usuário
GET    /levels/unlocked?currentXp=1000
```

### Missões
```
GET    /missions             # Listar todas
GET    /missions/level/{levelId}
GET    /missions/{id}        # Detalhes
```

### Bosses
```
GET    /bosses               # Listar todos
POST   /bosses/{id}/start    # Iniciar luta
POST   /bosses/{id}/submit   # Submeter solução
```

### Badges
```
GET    /badges               # Listar todos
GET    /badges/user/{userId} # Badges do usuário
```

### Ranking
```
GET    /api/ranking          # Ranking global
GET    /api/ranking/me       # Minha posição
```

### Notificações
```
GET    /notifications        # Minhas notificações
GET    /notifications/unread/count
PUT    /notifications/read-all
```

## 🔄 Paginação

```javascript
?page=0          // Página (começa em 0)
&size=20         // Itens por página
&sort=name,asc   // Ordenação
```

**Exemplo**:
```
GET /levels?page=0&size=10&sort=orderLevel,asc
```

## 📊 Códigos de Status

```
200  OK                  ✅ Sucesso
201  Created             ✅ Criado
204  No Content          ✅ Sucesso sem conteúdo
400  Bad Request         ❌ Dados inválidos
401  Unauthorized        ❌ Não autenticado
403  Forbidden           ❌ Sem permissão
404  Not Found           ❌ Não encontrado
429  Too Many Requests   ❌ Rate limit
500  Server Error        ❌ Erro do servidor
```

## 🎯 Tags/Categorias

```
🔐 Autenticação      - Login e registro
👤 Usuários          - Perfis
📊 Níveis            - Progressão
🎯 Missões           - Desafios
👹 Bosses            - Desafios épicos
🏆 Badges            - Conquistas
📚 Grimórios         - Materiais PDF
🏅 Ranking           - Classificação
🔔 Notificações      - Notificações
⚙️ Admin             - Administrativo
```

## 💡 Exemplos Rápidos

### Fetch API
```javascript
const res = await fetch('http://localhost:8080/levels', {
  headers: { 'Authorization': `Bearer ${token}` }
});
const data = await res.json();
```

### Axios
```javascript
const { data } = await axios.get('/levels', {
  headers: { 'Authorization': `Bearer ${token}` }
});
```

## 🛠️ Configurar Cliente Axios

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080'
});

api.interceptors.request.use(config => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
```

## 📚 Documentação Completa

```
/documentations/README.md                - Índice
/documentations/SWAGGER_QUICK_START.md   - Guia rápido
/documentations/API_EXAMPLES.md          - 31 exemplos
/documentations/SWAGGER_DOCUMENTATION.md - Completa
```

## ⚡ Dicas Rápidas

1. **Token expira em 15 minutos** → Use `/auth/refresh`
2. **Sempre valide erros 401/403** → Redirecione para login
3. **Use paginação** → Listas grandes
4. **Cache inteligente** → Dados que não mudam
5. **Loading states** → Sempre mostre feedback

## 🎉 Pronto!

Acesse: **http://localhost:8080/swagger-ui.html**

---

**Dúvidas?** Consulte `/documentations/README.md`

