# 📧 Guia de Configuração de Email

## Visão Geral

Este guia explica como configurar o envio de emails para confirmação de cadastro na Gamified Platform.

## Funcionalidades Implementadas

✅ **Envio de email de confirmação** ao criar uma nova conta
✅ **Token de verificação único** com validade de 24 horas
✅ **Endpoint para verificar email** via link
✅ **Reenvio de email de verificação** caso o usuário não receba
✅ **Campos na entidade User** para controle de verificação

## Como Funciona

1. **Registro do Usuário**: Quando um novo usuário se registra via `/auth/register`, o sistema:
   - Cria a conta com `emailVerified = false`
   - Gera um token único (UUID)
   - Define expiração do token (24 horas)
   - Envia email com link de confirmação

2. **Verificação do Email**: O usuário clica no link recebido:
   - Link: `http://localhost:8080/auth/verify-email?token={token}`
   - Sistema valida o token
   - Marca email como verificado
   - Remove o token usado

3. **Reenvio**: Se o email não chegar, o usuário pode solicitar reenvio via `/auth/resend-verification`

## Configuração

### 1. Variáveis de Ambiente

Copie o arquivo `.env.example` e renomeie para `.env` (ou configure no seu IDE):

```env
# Email Configuration
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=seu-email@gmail.com
MAIL_PASSWORD=sua-senha-app
APP_BASE_URL=http://localhost:8080
```

### 2. Configuração para Gmail

#### Passo 1: Ativar Verificação em 2 Etapas
1. Acesse sua conta Google
2. Vá em **Segurança**
3. Ative **Verificação em duas etapas**

#### Passo 2: Criar Senha de App
1. Na mesma página de Segurança
2. Procure por **Senhas de app**
3. Selecione **Email** e **Outro (personalizado)**
4. Digite "Gamified Platform"
5. Copie a senha gerada de 16 caracteres
6. Use essa senha na variável `MAIL_PASSWORD`

### 3. Outros Provedores de Email

#### Outlook/Hotmail
```env
MAIL_HOST=smtp-mail.outlook.com
MAIL_PORT=587
MAIL_USERNAME=seu-email@outlook.com
MAIL_PASSWORD=sua-senha
```

#### Yahoo
```env
MAIL_HOST=smtp.mail.yahoo.com
MAIL_PORT=587
MAIL_USERNAME=seu-email@yahoo.com
MAIL_PASSWORD=senha-app
```

#### SendGrid (Recomendado para produção)
```env
MAIL_HOST=smtp.sendgrid.net
MAIL_PORT=587
MAIL_USERNAME=apikey
MAIL_PASSWORD=sua-api-key-sendgrid
```

#### Mailtrap (Para testes/desenvolvimento)
```env
MAIL_HOST=smtp.mailtrap.io
MAIL_PORT=2525
MAIL_USERNAME=seu-username-mailtrap
MAIL_PASSWORD=sua-senha-mailtrap
```

## API Endpoints

### 1. Registrar Usuário
```http
POST /auth/register
Content-Type: application/json

{
  "username": "warrior123",
  "email": "warrior@example.com",
  "password": "senha123"
}
```

**Resposta**: Usuário criado + Email de verificação enviado

### 2. Verificar Email
```http
GET /auth/verify-email?token=550e8400-e29b-41d4-a716-446655440000
```

**Resposta**: 
```
Email verified successfully! You can now login to your account.
```

### 3. Reenviar Email de Verificação
```http
POST /auth/resend-verification
Content-Type: application/json

{
  "email": "warrior@example.com"
}
```

**Resposta**: 
```
Verification email sent successfully!
```

## Database Migration

A migration `V6__add_email_confirmation_fields.sql` adiciona:

```sql
ALTER TABLE tb_user
ADD COLUMN email_verified BOOLEAN DEFAULT FALSE,
ADD COLUMN email_verification_token VARCHAR(255),
ADD COLUMN email_verification_token_expires_at TIMESTAMP;
```

## Testando Localmente

### Opção 1: Usar Mailtrap (Recomendado para Dev)

1. Crie conta grátis em [mailtrap.io](https://mailtrap.io)
2. Copie as credenciais SMTP
3. Configure no `.env`
4. Todos os emails serão capturados no Mailtrap

### Opção 2: Usar Gmail Real

1. Configure Gmail conforme instruções acima
2. Use seu email real
3. Receba emails de verdade

## Exemplo de Email Enviado

```
Olá, warrior123! 👋

Bem-vindo à Gamified Platform! 🎮⚔️

Você está a um passo de começar sua jornada épica no mundo Dark Fantasy da programação!

Para ativar sua conta, clique no link abaixo:
http://localhost:8080/auth/verify-email?token=550e8400-e29b-41d4-a716-446655440000

⚠️ Este link é válido por 24 horas.

Se você não criou uma conta na Gamified Platform, ignore este email.

Que sua jornada seja lendária! 🗡️

---
Equipe Gamified Platform
```

## Próximos Passos (Melhorias Futuras)

- [ ] Templates HTML para emails mais bonitos
- [ ] Adicionar logo da plataforma
- [ ] Criar página de confirmação no frontend
- [ ] Implementar reset de senha via email
- [ ] Adicionar rate limiting para reenvio
- [ ] Notificações de nova missão/conquista por email

## Troubleshooting

### Email não está sendo enviado

1. **Verifique as credenciais**: Certifique-se que `MAIL_USERNAME` e `MAIL_PASSWORD` estão corretas
2. **Verifique o host e porta**: Confirme que correspondem ao seu provedor
3. **Cheque os logs**: Procure por erros no console da aplicação
4. **Firewall**: Verifique se a porta 587 não está bloqueada

### Token expirado

- Tokens têm validade de 24 horas
- Solicite reenvio via `/auth/resend-verification`

### Email já verificado

- Não é possível verificar novamente
- Não é possível solicitar reenvio

## Segurança

✅ Tokens são UUID únicos e não previsíveis
✅ Tokens expiram em 24 horas
✅ Tokens são deletados após uso
✅ Email de verificação é enviado de forma assíncrona
✅ Validações apropriadas em todos os endpoints

## Contato

Em caso de dúvidas ou problemas, abra uma issue no repositório!

---
**Gamified Platform** - Transformando aprendizado em jornada épica! 🎮⚔️

