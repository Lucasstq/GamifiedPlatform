# 🔐 Login Social OAuth2 - Google e GitHub


## 🚀 Como Funciona

### Fluxo de Autenticação

```
1. Frontend → Redireciona para /oauth2/authorization/{provider}
2. Backend → Redireciona para Google/GitHub
3. Usuário → Autoriza a aplicação
4. Google/GitHub → Retorna para /login/oauth2/code/{provider}
5. Backend → Processa OAuth2:
   - Busca/Cria usuário
   - Gera tokens JWT
   - Registra auditoria
6. Backend → Redireciona para frontend com tokens
7. Frontend → Recebe tokens e autentica usuário
```

## 📋 Endpoints OAuth2

### Iniciar Login Social

#### Google
```
GET /oauth2/authorization/google
```

#### GitHub
```
GET /oauth2/authorization/github
```

### Callback (Automático)
```
GET /login/oauth2/code/google   # Callback do Google
GET /login/oauth2/code/github   # Callback do GitHub
```

### Redirecionamento Final
```
GET http://localhost:3000/oauth2/redirect?accessToken={token}&refreshToken={token}&tokenType=Bearer
```

## 🔧 Configuração

### Backend (application.yaml)

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
GOOGLE_CLIENT_ID=seu-client-id-google.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=seu-secret-google

# GitHub OAuth2
GITHUB_CLIENT_ID=seu-client-id-github
GITHUB_CLIENT_SECRET=seu-secret-github
```
```

## 🔍 Como Funciona Internamente

### Classes Implementadas

1. **`OAuth2UserService`** - Processa usuários OAuth2
   - Cria novos usuários
   - Vincula providers a usuários existentes
   - Atualiza dados do usuário

2. **`CustomOAuth2UserServiceAdapter`** - Adapter do Spring Security
   - Integra com OAuth2UserService
   - Extrai informações do provider

3. **`OAuth2AuthenticationSuccessHandler`** - Handler de sucesso
   - Gera tokens JWT
   - Registra auditoria
   - Redireciona com tokens

4. **`OAuth2AuthenticationFailureHandler`** - Handler de falha
   - Trata erros de autenticação
   - Redireciona com mensagem de erro

### Dados Extraídos por Provider

#### Google
- `sub` (Provider ID)
- `email`
- `name` (usado como username)
- `picture` (avatar URL)

#### GitHub
- `id` (Provider ID)
- `email`
- `login` (usado como username)
- `avatar_url`

## 📊 Fluxo de Dados

### Primeiro Login (Novo Usuário)

```
1. Usuário clica "Login com Google"
2. Backend recebe callback do Google
3. OAuth2UserService verifica que usuário não existe
4. Cria novo usuário:
   - Provider: GOOGLE
   - ProviderId: sub do Google
   - Email: do Google
   - Username: nome do Google
   - Role: PLAYER (padrão)
   - Active: true
   - EmailConfirmed: true (já confirmado pelo Google)
5. Cria personagem automaticamente
6. Atribui scopes padrão (profile:read, profile:write, etc)
7. Gera tokens JWT
8. Redireciona para frontend com tokens
```

### Login Subsequente (Usuário Existente)

```
1. Usuário clica "Login com Google"
2. Backend recebe callback do Google
3. OAuth2UserService encontra usuário existente
4. Atualiza dados se necessário (email, avatar)
5. Gera novos tokens JWT
6. Registra log de auditoria
7. Redireciona para frontend com tokens
```

### Vincular Provider a Usuário Existente

```
Exemplo: Usuário criado com username/password faz login com Google

1. Usuário clica "Login com Google"
2. Backend recebe callback do Google
3. OAuth2UserService verifica:
   - Não existe usuário com providerId
   - MAS existe usuário com mesmo email
4. Vincula Google ao usuário existente:
   - Atualiza provider: GOOGLE
   - Atualiza providerId: sub do Google
5. Usuário pode agora fazer login tanto com senha quanto com Google
```

## 🔐 Segurança

### ✅ Implementado
- ✅ Validação de tokens OAuth2
- ✅ CSRF protection
- ✅ State parameter validation
- ✅ Secure redirect URIs
- ✅ Auditoria de login
- ✅ Rate limiting
- ✅ Email verification automática (providers confiáveis)

### Campos de Segurança no User

```java
private AuthProvider provider;      // LOCAL, GOOGLE, GITHUB
private String providerId;          // ID único do provider
private Boolean emailConfirmed;     // true para OAuth2
```

## 🧪 Como Testar

### 1. Configurar Credenciais OAuth2

#### Google Cloud Console
1. Acesse https://console.cloud.google.com/
2. Crie um projeto
3. Ative Google+ API
4. Configure OAuth consent screen
5. Crie credenciais OAuth 2.0:
   - Authorized redirect URIs: `http://localhost:8080/login/oauth2/code/google`
6. Copie Client ID e Client Secret

#### GitHub Developer Settings
1. Acesse https://github.com/settings/developers
2. Clique em "New OAuth App"
3. Configure:
   - Homepage URL: `http://localhost:8080`
   - Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`
4. Copie Client ID e Client Secret

### 2. Configurar Variáveis de Ambiente

```bash
export GOOGLE_CLIENT_ID="seu-client-id.apps.googleusercontent.com"
export GOOGLE_CLIENT_SECRET="seu-secret"
export GITHUB_CLIENT_ID="seu-client-id"
export GITHUB_CLIENT_SECRET="seu-secret"
```

### 3. Iniciar Aplicação

```bash
./mvnw spring-boot:run
```

### 4. Testar Login

```bash
# Abrir no navegador
http://localhost:8080/oauth2/authorization/google
# ou
http://localhost:8080/oauth2/authorization/github
```

## 📝 Logs de Auditoria

Cada login OAuth2 é registrado:

```sql
INSERT INTO tb_security_audit_log (
  user_id,
  username,
  action,
  ip_address,
  user_agent,
  success,
  timestamp
) VALUES (
  1,
  'joao.silva',
  'LOGIN',
  '192.168.1.100',
  'Mozilla/5.0...',
  true,
  NOW()
);
```

## ⚠️ Possíveis Erros

### Erro: "redirect_uri_mismatch"
**Causa**: Redirect URI não cadastrada no provider
**Solução**: Adicionar `http://localhost:8080/login/oauth2/code/{provider}` nas configurações

### Erro: "invalid_client"
**Causa**: Client ID ou Secret incorretos
**Solução**: Verificar variáveis de ambiente

### Erro: "access_denied"
**Causa**: Usuário cancelou autorização
**Solução**: Tentar novamente

## 🎯 Próximos Passos

