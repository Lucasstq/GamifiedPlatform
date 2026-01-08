# Rate Limiting - Documentação

## Visão Geral

Sistema de rate limiting implementado com Redis para proteger endpoints contra abuso.

## Limites Configurados

### Login
- **Limite**: 5 tentativas a cada 15 minutos
- **Chave**: `rate_limit:login:{identifier}`
- **Uso**: Prevenir ataques de força bruta

### Email Verification
- **Limite**: 3 emails a cada 1 hora
- **Chave**: `rate_limit:email_verification:{email}`
- **Uso**: Prevenir spam de emails

### Mission Submission
- **Limite**: 10 submissões a cada 5 minutos
- **Chave**: `rate_limit:mission_submission:{userId}`
- **Uso**: Prevenir spam de submissões
- **Implementado em**: `SubmitMission.execute()`

### Boss Submission
- **Limite**: 3 tentativas a cada 1 hora
- **Chave**: `rate_limit:boss_submission:{userId}`
- **Uso**: Limitar tentativas de bosses
- **Implementado em**: `SubmitBossFightService.execute()`

### IP Rate Limiting
- **Limite**: 100 requisições por minuto
- **Chave**: `rate_limit:ip:{ipAddress}`
- **Uso**: Proteção geral contra DDoS

## Implementação

### RateLimitService

```java
public boolean isMissionSubmissionAllowed(Long userId);
public boolean isBossSubmissionAllowed(Long userId);
public boolean isLoginAllowed(String identifier);
public boolean isEmailVerificationAllowed(String email);
public boolean isIpAllowed(String ipAddress);
```

### Como Usar

```java
if (!rateLimitService.isMissionSubmissionAllowed(userId)) {
    throw new BusinessException("Rate limit exceeded. You can only submit 10 missions every 5 minutes.");
}
```

## Mensagens de Erro

### Mission Submission
```
Rate limit exceeded. You can only submit 10 missions every 5 minutes.
```

### Boss Submission
```
Rate limit exceeded. You can only submit 3 boss attempts every hour.
```

## Redis Keys

Todas as chaves seguem o padrão:
```
rate_limit:{tipo}:{identificador}
```

Exemplos:
- `rate_limit:mission_submission:123`
- `rate_limit:boss_submission:123`
- `rate_limit:login:user@email.com`

## TTL (Time To Live)

Os contadores expiram automaticamente após o período da janela:
- Mission: 5 minutos
- Boss: 1 hora
- Login: 15 minutos
- Email: 1 hora
- IP: 1 minuto

## Resetting

O service fornece método para resetar manualmente:

```java
rateLimitService.reset("mission_submission:123");
```

## Monitoramento

Para verificar tentativas restantes:

```java
int remaining = rateLimitService.getRemainingAttempts("mission_submission:123", 10);
long resetTime = rateLimitService.getResetTime("mission_submission:123");
```

## Considerações

1. **Redis**: Rate limiting depende do Redis funcionando
2. **Failover**: Em caso de erro no Redis, permite a requisição (fail-open)
3. **Granularidade**: Limites são por usuário, não por sessão
4. **Ajustes**: Valores podem ser ajustados conforme necessidade

