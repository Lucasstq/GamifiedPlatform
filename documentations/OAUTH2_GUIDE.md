# 🔐 Guia OAuth2 - Login Social (Google & GitHub)

### Handlers Implementados
- ✅ `CustomOAuth2UserServiceAdapter` - Processa usuários OAuth2
- ✅ `OAuth2AuthenticationSuccessHandler` - Sucesso no login
- ✅ `OAuth2AuthenticationFailureHandler` - Falha no login

## 🚀 Como Funciona

### 1. Fluxo de Autenticação OAuth2

```
Frontend                Backend               Provider (Google/GitHub)
   |                       |                            |
   |---(1) Iniciar OAuth-->|                            |
   |                       |---(2) Redirecionar-------->|
   |                       |                            |
   |<-------------(3) Login no Provider----------------|
   |                       |                            |
   |                       |<---(4) Código de Autoriza-|
   |                       |                            |
   |                       |---(5) Trocar por token---->|
   |                       |                            |
   |                       |<---(6) Dados do usuário----|
   |                       |                            |
   |<--(7) Redirecionar com JWT Token------------------|
   |                       |                            |
```

### 2. Endpoints OAuth2

#### Google Login
```
GET /oauth2/authorization/google
```

#### GitHub Login
```
GET /oauth2/authorization/github
```

#### Callback (Automático)
```
GET /login/oauth2/code/{provider}
```
```

## 🔄 Recebendo o Token JWT

### 1. Configurar Redirect URI

O backend está configurado para redirecionar para:
```
http://localhost:3000/oauth2/redirect
```

### 2. Criar Página de Redirect

```javascript
// src/pages/OAuth2Redirect.jsx (React)
import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

function OAuth2Redirect() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  useEffect(() => {
    // Pegar token da URL
    const token = searchParams.get('token');
    const error = searchParams.get('error');

    if (error) {
      console.error('Erro no login OAuth2:', error);
      navigate('/login?error=oauth2_failed');
      return;
    }

    if (token) {
      // Salvar token
      localStorage.setItem('accessToken', token);
      
      // Buscar informações do usuário
      fetchUserInfo(token).then(() => {
        navigate('/dashboard');
      });
    } else {
      navigate('/login');
    }
  }, [searchParams, navigate]);

  return <div>Processando login...</div>;
}

async function fetchUserInfo(token) {
  const response = await fetch('http://localhost:8080/users/me', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  const user = await response.json();
  localStorage.setItem('user', JSON.stringify(user));
}

export default OAuth2Redirect;
```

### 3. Configurar Rota

```javascript
// React Router
<Route path="/oauth2/redirect" element={<OAuth2Redirect />} />

// Vue Router
{
  path: '/oauth2/redirect',
  component: OAuth2Redirect
}

// Angular Router
{
  path: 'oauth2/redirect',
  component: OAuth2RedirectComponent
}
```

## ⚙️ Configuração Backend (Já Implementado)

### application.yaml

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope:
              - email
              - profile
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
          github:
            client-id: ${GITHUB_CLIENT_ID}
            client-secret: ${GITHUB_CLIENT_SECRET}
            scope:
              - user:email
              - read:user
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"

app:
  oauth2:
    redirect-uri: http://localhost:3000/oauth2/redirect
```

### Variáveis de Ambiente Necessárias

```bash
# Google OAuth2
GOOGLE_CLIENT_ID=seu-google-client-id
GOOGLE_CLIENT_SECRET=seu-google-client-secret

# GitHub OAuth2
GITHUB_CLIENT_ID=seu-github-client-id
GITHUB_CLIENT_SECRET=seu-github-client-secret
```

## 🔧 Como Obter Credenciais OAuth2

### Google Cloud Console

1. Acesse: https://console.cloud.google.com/
2. Crie um novo projeto ou selecione existente
3. Vá em "APIs & Services" → "Credentials"
4. Clique em "Create Credentials" → "OAuth 2.0 Client ID"
5. Configure:
   - Application type: Web application
   - Authorized redirect URIs: 
     - `http://localhost:8080/login/oauth2/code/google`
     - `https://seu-dominio.com/login/oauth2/code/google` (produção)
6. Copie Client ID e Client Secret

### GitHub Developer Settings

1. Acesse: https://github.com/settings/developers
2. Clique em "New OAuth App"
3. Preencha:
   - Application name: Gamified Platform
   - Homepage URL: `http://localhost:3000`
   - Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`
4. Clique em "Register application"
5. Copie Client ID
6. Clique em "Generate a new client secret"
7. Copie Client Secret

## 📊 Fluxo Detalhado

### 1. Usuário Clica no Botão
```javascript
window.location.href = 'http://localhost:8080/oauth2/authorization/google';
```

### 2. Backend Redireciona para Provider
```
https://accounts.google.com/o/oauth2/v2/auth?
  client_id=...
  &redirect_uri=http://localhost:8080/login/oauth2/code/google
  &response_type=code
  &scope=email profile
```

### 3. Usuário Faz Login no Provider
- Google: Login com conta Google
- GitHub: Login com conta GitHub

### 4. Provider Redireciona de Volta
```
http://localhost:8080/login/oauth2/code/google?code=authorization_code
```

### 5. Backend Processa
- Troca código por access token
- Busca informações do usuário
- Cria ou atualiza usuário no banco
- Gera JWT token

### 6. Backend Redireciona para Frontend
```
http://localhost:3000/oauth2/redirect?token=jwt_token
```

### 7. Frontend Salva Token
```javascript
localStorage.setItem('accessToken', token);
```

## 🎨 Componentes de UI Prontos

### Botões Material Design

```jsx
// Google Button
<button 
  onClick={loginWithGoogle}
  style={{
    backgroundColor: '#fff',
    color: '#757575',
    border: '1px solid #ddd',
    padding: '10px 20px',
    borderRadius: '4px',
    display: 'flex',
    alignItems: 'center',
    gap: '10px'
  }}
>
  <img src="/google-icon.svg" width="20" />
  Continuar com Google
</button>

// GitHub Button
<button 
  onClick={loginWithGitHub}
  style={{
    backgroundColor: '#24292e',
    color: '#fff',
    border: 'none',
    padding: '10px 20px',
    borderRadius: '4px',
    display: 'flex',
    alignItems: 'center',
    gap: '10px'
  }}
>
  <img src="/github-icon.svg" width="20" />
  Continuar com GitHub
</button>
```

## 🔒 Segurança

### CSRF Protection
✅ Habilitado automaticamente pelo Spring Security

### State Parameter
✅ Validado automaticamente para prevenir CSRF

## 🐛 Troubleshooting

### Erro: redirect_uri_mismatch
**Causa**: URI de redirecionamento não está configurada no provider

**Solução**: 
1. Verifique a URI no console do Google/GitHub
2. Deve ser exatamente: `http://localhost:8080/login/oauth2/code/google` (ou github)

### Erro: invalid_client
**Causa**: Client ID ou Secret incorretos

**Solução**: 
1. Verifique as variáveis de ambiente
2. Regenere o Client Secret se necessário

### Usuário não é redirecionado
**Causa**: Redirect URI do frontend não configurada

**Solução**:
1. Verifique `app.oauth2.redirect-uri` no `application.yaml`
2. Crie a rota `/oauth2/redirect` no frontend

---

**Dúvidas?** Consulte a [documentação oficial do Spring Security OAuth2](https://docs.spring.io/spring-security/reference/servlet/oauth2/login/core.html)

