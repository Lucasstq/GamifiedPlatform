# 🎮 Gamified Platform - Dark Fantasy Learning

> Plataforma educacional gamificada que transforma o aprendizado de programação em uma jornada épica!

## 🚀 Início Rápido

### 1. Documentação da API
```bash
# Iniciar a aplicação
./mvnw spring-boot:run

# Acessar Swagger UI
http://localhost:8080/swagger-ui.html
```

### 2. Guias Disponíveis
- **[README Geral](documentations/README.md)** - Índice de toda documentação
- **[Guia Rápido Swagger](documentations/SWAGGER_QUICK_START.md)** - Como usar a API
- **[OAuth2 Integration](documentations/OAUTH2_INTEGRATION.md)** - Login Social
- **[API Examples](documentations/API_EXAMPLES.md)** - 31 exemplos práticos

## 📊 Funcionalidades

### 🎯 Sistema de Gamificação
- **10 Níveis** de progressão (Iniciante → Mestre)
- **Missões** de programação com XP
- **Bosses** ao final de cada nível
- **Badges** de conquistas
- **Ranking** global e por nível

### 🔐 Autenticação
- Login tradicional (username/password)
- **OAuth2 com Google**
- **OAuth2 com GitHub**
- JWT tokens (access + refresh)
- Auditoria de segurança

### 📚 Recursos Educacionais
- **Grimórios** (PDFs) desbloqueáveis por nível
- Upload de arquivos (MinIO)
- Sistema de notificações
- Progresso detalhado

### 🛠️ Infraestrutura
- Spring Boot 4.0.1
- PostgreSQL
- Redis (cache)
- MinIO (storage)
- Flyway (migrations)
- SpringDoc OpenAPI

## 📖 Documentação Completa

### Para Desenvolvedores Frontend
1. **[Guia Rápido](documentations/SWAGGER_QUICK_START.md)** ⭐ Comece aqui!
2. **[Exemplos de API](documentations/API_EXAMPLES.md)** - Código pronto
3. **[OAuth2](documentations/OAUTH2_INTEGRATION.md)** - Login social
4. **[Referência Rápida](documentations/QUICK_REFERENCE.md)** - Cartão de consulta

### Documentação Técnica
- [Swagger Documentation](documentations/SWAGGER_DOCUMENTATION.md)
- [API Endpoints](documentations/API_ENDPOINTS_DOCUMENTATION.md)
- [Security Annotations](documentations/SECURITY_ANNOTATIONS.md)
- [Pagination](documentations/PAGINATION_STANDARDIZATION.md)

## 🔧 Configuração

### Variáveis de Ambiente Necessárias

```bash
# Banco de Dados
POSTGRES_DB=gamified_platform
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# MinIO
MINIO_ENDPOINT=http://localhost:9000
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=minioadmin

# Email
MAIL_USERNAME=seu-email@gmail.com
MAIL_PASSWORD=sua-senha-app

# OAuth2 (opcional - para login social)
GOOGLE_CLIENT_ID=seu-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=seu-secret
GITHUB_CLIENT_ID=seu-client-id
GITHUB_CLIENT_SECRET=seu-secret
```

### Iniciar com Docker Compose

```bash
# Subir infraestrutura (PostgreSQL, Redis, MinIO)
docker-compose up -d

# Iniciar aplicação
./mvnw spring-boot:run
```

## 📱 Endpoints Principais

### Autenticação
```
POST   /auth/login                          # Login tradicional
POST   /auth/register                       # Registro
GET    /oauth2/authorization/google         # Login com Google
GET    /oauth2/authorization/github         # Login com GitHub
POST   /auth/refresh                        # Renovar token
```

### Usuários
```
GET    /users/{id}                          # Buscar usuário
GET    /users/{id}/profile                  # Perfil público
PUT    /users/{id}                          # Atualizar
```

### Níveis & Progressão
```
GET    /levels                              # Listar níveis
GET    /levels/user/{userId}                # Nível atual
GET    /levels/unlocked?currentXp={xp}      # Níveis desbloqueados
```

### Missões
```
GET    /missions                            # Listar missões
GET    /missions/level/{levelId}            # Missões do nível
```

### Ranking
```
GET    /api/ranking                         # Ranking global
GET    /api/ranking/me                      # Minha posição
```

**Veja todos os endpoints**: http://localhost:8080/swagger-ui.html

## 🎯 Exemplo de Uso

### Login e Buscar Níveis

```javascript
// 1. Login
const response = await fetch('http://localhost:8080/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    username: 'player',
    password: 'senha123'
  })
});
const { accessToken } = await response.json();

// 2. Buscar níveis
const levels = await fetch('http://localhost:8080/levels', {
  headers: { 'Authorization': `Bearer ${accessToken}` }
});
const data = await levels.json();
console.log(data);
```

### Login com Google

```javascript
// Redirecionar para Google OAuth2
window.location.href = 'http://localhost:8080/oauth2/authorization/google';

// Backend retorna para: http://localhost:3000/oauth2/redirect?accessToken={token}
```

## 🎨 Stack Tecnológica

### Backend
- **Java 17**
- **Spring Boot 4.0.1**
- **Spring Security** (JWT + OAuth2)
- **Spring Data JPA**
- **PostgreSQL**
- **Redis** (Cache)
- **MinIO** (Storage)
- **Flyway** (Migrations)
- **SpringDoc OpenAPI** (Swagger)

### Bibliotecas
- Lombok
- Validation
- Mail Sender

## 📂 Estrutura do Projeto

```
src/main/java/
├── config/              # Configurações (Security, Redis, MinIO, Swagger)
├── controller/          # Controllers REST (documentados com Swagger)
├── domain/             # Entidades JPA
├── dtos/               # Request/Response DTOs
├── enums/              # Enumerações
├── repository/         # Repositories JPA
├── services/           # Lógica de negócio
└── exceptions/         # Exceções customizadas

documentations/         # Documentação completa
├── README.md          # Índice geral
├── OAUTH2_INTEGRATION.md     # ⭐ Login Social
├── SWAGGER_QUICK_START.md    # ⭐ Guia rápido
├── API_EXAMPLES.md           # Exemplos práticos
└── ...
```

## 🧪 Testes

```bash
# Rodar testes
./mvnw test

# Rodar testes com cobertura
./mvnw test jacoco:report
```

## 📞 Suporte

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Documentação**: [`documentations/`](documentations/)
- **Issues**: GitHub Issues

---

**Desenvolvido com ❤️ para tornar o aprendizado de programação mais divertido e engajador!**
