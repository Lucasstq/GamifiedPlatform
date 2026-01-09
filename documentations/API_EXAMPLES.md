# 📋 Exemplos de Requisições - API Gamified Platform

Este documento contém exemplos práticos de como consumir a API do Gamified Platform.

## 🔐 Autenticação

### 1. Registrar Novo Usuário

```javascript
// POST /auth/register
const response = await fetch('http://localhost:8080/auth/register', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    username: 'novousuario',
    email: 'usuario@email.com',
    password: 'SenhaForte123!'
  })
});

const user = await response.json();
console.log('Usuário criado:', user);
// Resposta: { id, username, email, role, active, ... }
```

### 2. Fazer Login

```javascript
// POST /auth/login
const response = await fetch('http://localhost:8080/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    username: 'novousuario',
    password: 'SenhaForte123!'
  })
});

const { accessToken, refreshToken, user } = await response.json();
localStorage.setItem('accessToken', accessToken);
localStorage.setItem('refreshToken', refreshToken);
console.log('Login realizado:', user);
```

### 3. Renovar Token

```javascript
// POST /auth/refresh
const refreshToken = localStorage.getItem('refreshToken');

const response = await fetch('http://localhost:8080/auth/refresh', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    refreshToken: refreshToken
  })
});

const { accessToken: newAccessToken } = await response.json();
localStorage.setItem('accessToken', newAccessToken);
```

### 4. Logout

```javascript
// POST /auth/logout
const refreshToken = localStorage.getItem('refreshToken');

await fetch('http://localhost:8080/auth/logout', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    refreshToken: refreshToken
  })
});

localStorage.removeItem('accessToken');
localStorage.removeItem('refreshToken');
```

## 👤 Usuários

### 5. Buscar Perfil do Usuário

```javascript
// GET /users/{id}
const userId = 1;
const token = localStorage.getItem('accessToken');

const response = await fetch(`http://localhost:8080/users/${userId}`, {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const user = await response.json();
console.log('Perfil:', user);
```

### 6. Buscar Perfil Público com Estatísticas

```javascript
// GET /users/{id}/profile
const userId = 1;
const token = localStorage.getItem('accessToken');

const response = await fetch(`http://localhost:8080/users/${userId}/profile`, {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const profile = await response.json();
console.log('XP Total:', profile.totalXp);
console.log('Nível:', profile.currentLevel);
console.log('Missões Completas:', profile.completedMissions);
console.log('Bosses Derrotados:', profile.defeatedBosses);
```

### 7. Atualizar Perfil

```javascript
// PUT /users/{id}
const userId = 1;
const token = localStorage.getItem('accessToken');

const response = await fetch(`http://localhost:8080/users/${userId}`, {
  method: 'PUT',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    email: 'novoemail@email.com'
  })
});

const updatedUser = await response.json();
```

### 8. Alterar Senha

```javascript
// PATCH /users/{id}/change-password
const userId = 1;
const token = localStorage.getItem('accessToken');

await fetch(`http://localhost:8080/users/${userId}/change-password`, {
  method: 'PATCH',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    currentPassword: 'SenhaAntiga123!',
    newPassword: 'NovaSenha456!'
  })
});
```

## 📊 Níveis

### 9. Listar Todos os Níveis (Paginado)

```javascript
// GET /levels?page=0&size=10&sort=orderLevel,asc
const token = localStorage.getItem('accessToken');

const response = await fetch('http://localhost:8080/levels?page=0&size=10&sort=orderLevel,asc', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const { content, totalElements, totalPages } = await response.json();
console.log(`Total de níveis: ${totalElements}`);
console.log('Níveis:', content);
```

### 10. Buscar Nível Atual do Usuário

```javascript
// GET /levels/user/{userId}
const userId = 1;
const token = localStorage.getItem('accessToken');

const response = await fetch(`http://localhost:8080/levels/user/${userId}`, {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const currentLevel = await response.json();
console.log('Nível Atual:', currentLevel.name);
console.log('XP Necessário:', currentLevel.requiredXp);
```

### 11. Buscar Próximo Nível

```javascript
// GET /levels/next/{currentOrderLevel}
const currentOrder = 3;
const token = localStorage.getItem('accessToken');

const response = await fetch(`http://localhost:8080/levels/next/${currentOrder}`, {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const nextLevel = await response.json();
console.log('Próximo Nível:', nextLevel.name);
```

### 12. Listar Níveis Desbloqueados

```javascript
// GET /levels/unlocked?currentXp=1500
const userXp = 1500;
const token = localStorage.getItem('accessToken');

const response = await fetch(`http://localhost:8080/levels/unlocked?currentXp=${userXp}`, {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const { content: unlockedLevels } = await response.json();
console.log('Níveis Desbloqueados:', unlockedLevels);
```

## 🎯 Missões

### 13. Listar Todas as Missões

```javascript
// GET /missions?page=0&size=20
const token = localStorage.getItem('accessToken');

const response = await fetch('http://localhost:8080/missions?page=0&size=20', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const { content: missions } = await response.json();
console.log('Missões:', missions);
```

### 14. Listar Missões de um Nível

```javascript
// GET /missions/level/{levelId}
const levelId = 1;
const token = localStorage.getItem('accessToken');

const response = await fetch(`http://localhost:8080/missions/level/${levelId}`, {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const { content: levelMissions } = await response.json();
console.log('Missões do Nível 1:', levelMissions);
```

### 15. Buscar Detalhes da Missão

```javascript
// GET /missions/{missionId}
const missionId = 5;
const token = localStorage.getItem('accessToken');

const response = await fetch(`http://localhost:8080/missions/${missionId}`, {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const mission = await response.json();
console.log('Missão:', mission.title);
console.log('Descrição:', mission.description);
console.log('XP Recompensa:', mission.xpReward);
```

## 👹 Bosses

### 16. Listar Todos os Bosses

```javascript
// GET /bosses
const token = localStorage.getItem('accessToken');

const response = await fetch('http://localhost:8080/bosses', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const { content: bosses } = await response.json();
console.log('Bosses:', bosses);
```

### 17. Verificar Progresso do Boss no Nível

```javascript
// GET /bosses/level/{levelId}/progress
const levelId = 3;
const token = localStorage.getItem('accessToken');

const response = await fetch(`http://localhost:8080/bosses/level/${levelId}/progress`, {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const progress = await response.json();
console.log('Progresso no Nível:', progress.levelProgress);
console.log('Boss Desbloqueado?', progress.bossUnlocked);
```

### 18. Iniciar Luta Contra Boss

```javascript
// POST /bosses/{bossId}/start
const bossId = 1;
const token = localStorage.getItem('accessToken');

const response = await fetch(`http://localhost:8080/bosses/${bossId}/start`, {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const bossFight = await response.json();
console.log('Luta Iniciada:', bossFight);
```

### 19. Submeter Solução da Luta

```javascript
// POST /bosses/{bossId}/submit
const bossId = 1;
const token = localStorage.getItem('accessToken');

const response = await fetch(`http://localhost:8080/bosses/${bossId}/submit`, {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    solutionUrl: 'https://github.com/usuario/projeto-boss-1',
    notes: 'Implementei usando Clean Architecture'
  })
});

const submission = await response.json();
console.log('Submissão enviada:', submission);
```

## 🏆 Badges

### 20. Listar Todos os Badges

```javascript
// GET /badges
const token = localStorage.getItem('accessToken');

const response = await fetch('http://localhost:8080/badges', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const { content: badges } = await response.json();
console.log('Badges Disponíveis:', badges);
```

### 21. Listar Badges do Usuário

```javascript
// GET /badges/user/{userId}
const userId = 1;
const token = localStorage.getItem('accessToken');

const response = await fetch(`http://localhost:8080/badges/user/${userId}`, {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const { content: userBadges } = await response.json();
console.log('Meus Badges:', userBadges);
```

### 22. Progresso de Badges

```javascript
// GET /badges/user/{userId}/progress
const userId = 1;
const token = localStorage.getItem('accessToken');

const response = await fetch(`http://localhost:8080/badges/user/${userId}/progress`, {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const progress = await response.json();
console.log(`Badges Conquistados: ${progress.earned}/${progress.total}`);
console.log(`Progresso: ${progress.percentage}%`);
```

## 📚 Grimórios

### 23. Listar Grimórios Disponíveis

```javascript
// GET /grimoires
const token = localStorage.getItem('accessToken');

const response = await fetch('http://localhost:8080/grimoires', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const { content: grimoires } = await response.json();
console.log('Grimórios:', grimoires);
```

### 24. Download de Grimório

```javascript
// GET /grimoires/{levelId}/download
const levelId = 3;
const token = localStorage.getItem('accessToken');

const response = await fetch(`http://localhost:8080/grimoires/${levelId}/download`, {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

// Baixar como arquivo
const blob = await response.blob();
const url = window.URL.createObjectURL(blob);
const a = document.createElement('a');
a.href = url;
a.download = 'grimorio-nivel-3.pdf';
a.click();
```

## 🏅 Ranking

### 25. Ranking Global

```javascript
// GET /api/ranking?page=0&size=50
const token = localStorage.getItem('accessToken');

const response = await fetch('http://localhost:8080/api/ranking?page=0&size=50', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const { content: topPlayers } = await response.json();
console.log('Top 50 Jogadores:', topPlayers);
```

### 26. Minha Posição no Ranking

```javascript
// GET /api/ranking/me
const token = localStorage.getItem('accessToken');

const response = await fetch('http://localhost:8080/api/ranking/me', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const myRanking = await response.json();
console.log('Minha Posição:', myRanking.position);
console.log('Meu XP:', myRanking.totalXp);
```

### 27. Ranking por Nível

```javascript
// GET /api/ranking/level/{levelId}
const levelId = 5;
const token = localStorage.getItem('accessToken');

const response = await fetch(`http://localhost:8080/api/ranking/level/${levelId}`, {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const { content: levelRanking } = await response.json();
console.log('Top Jogadores do Nível 5:', levelRanking);
```

## 🔔 Notificações

### 28. Listar Minhas Notificações

```javascript
// GET /notifications?page=0&size=20&onlyUnread=false
const token = localStorage.getItem('accessToken');

const response = await fetch('http://localhost:8080/notifications?page=0&size=20&onlyUnread=false', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const { content: notifications } = await response.json();
console.log('Notificações:', notifications);
```

### 29. Contar Notificações Não Lidas

```javascript
// GET /notifications/unread/count
const token = localStorage.getItem('accessToken');

const response = await fetch('http://localhost:8080/notifications/unread/count', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const count = await response.json();
console.log(`Você tem ${count} notificações não lidas`);
```

### 30. Marcar Notificação como Lida

```javascript
// PUT /notifications/{notificationId}/read
const notificationId = 42;
const token = localStorage.getItem('accessToken');

await fetch(`http://localhost:8080/notifications/${notificationId}/read`, {
  method: 'PUT',
  headers: {
    'Authorization': `Bearer ${token}`
  }
});
```

### 31. Marcar Todas como Lidas

```javascript
// PUT /notifications/read-all
const token = localStorage.getItem('accessToken');

await fetch('http://localhost:8080/notifications/read-all', {
  method: 'PUT',
  headers: {
    'Authorization': `Bearer ${token}`
  }
});
```

## 🛠️ Utilitários

### Helper Function: API Client com Axios

```javascript
import axios from 'axios';

// Criar instância do axios
const api = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json'
  }
});

// Interceptor para adicionar token
api.interceptors.request.use(config => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Interceptor para renovar token automaticamente
api.interceptors.response.use(
  response => response,
  async error => {
    if (error.response?.status === 401) {
      const refreshToken = localStorage.getItem('refreshToken');
      
      if (refreshToken) {
        try {
          const { data } = await axios.post('http://localhost:8080/auth/refresh', {
            refreshToken
          });
          
          localStorage.setItem('accessToken', data.accessToken);
          
          // Retry original request
          error.config.headers.Authorization = `Bearer ${data.accessToken}`;
          return axios(error.config);
        } catch (refreshError) {
          // Refresh token expirado, redirecionar para login
          localStorage.clear();
          window.location.href = '/login';
        }
      }
    }
    return Promise.reject(error);
  }
);

export default api;
```

### Uso do API Client

```javascript
import api from './api-client';

// Usar em qualquer lugar
const fetchLevels = async () => {
  const { data } = await api.get('/levels');
  return data;
};

const updateProfile = async (userId, userData) => {
  const { data } = await api.put(`/users/${userId}`, userData);
  return data;
};
```

## 🎯 Dicas de Desenvolvimento

1. **Sempre validar tokens**: Verifique se o token existe antes de fazer requisições
2. **Tratar erros**: Implemente tratamento de erros 401 (não autenticado) e 403 (sem permissão)
3. **Renovar tokens**: Implemente renovação automática do access token
4. **Cache inteligente**: Use cache para dados que não mudam frequentemente (badges, níveis)
5. **Paginação**: Sempre use paginação para listas grandes
6. **Loading states**: Mostre indicadores de carregamento durante requisições

---

**Documentação Completa**: Acesse http://localhost:8080/swagger-ui.html para explorar todos os endpoints interativamente!

