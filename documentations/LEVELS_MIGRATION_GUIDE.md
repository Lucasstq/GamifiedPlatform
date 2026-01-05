# Guia de Migrations - Levels e Scopes

## Resumo das Alterações

### 1. Nova Migration V8 - Tabela de Levels
Criada a migration `V8__create_tb_levels.sql` que:

- **Cria a tabela `tb_levels`** com as seguintes colunas:
  - `id`: Chave primária auto-incrementada
  - `order_level`: Ordem do nível (único)
  - `name`: Nome do nível
  - `title`: Título do nível
  - `description`: Descrição detalhada (até 1000 caracteres)
  - `xp_required`: XP necessário para alcançar o nível
  - `icon_url`: URL do ícone (opcional)
  - `difficulty_level`: Nível de dificuldade (EASY, MEDIUM, HARD, EXPERT)
  - `created_at`: Data de criação (padrão: timestamp atual)
  - `updated_at`: Data de atualização

- **Cria índices** para otimizar consultas:
  - `idx_levels_order`: Índice na coluna `order_level`
  - `idx_levels_difficulty`: Índice na coluna `difficulty_level`

- **Insere 10 níveis iniciais** com dados de exemplo, distribuídos entre os diferentes níveis de dificuldade:
  - Níveis 1-3: EASY
  - Níveis 4-6: MEDIUM
  - Níveis 7-8: HARD
  - Níveis 9-10: EXPERT

### 2. Nova Migration V9 - Correção de Scopes
Criada a migration `V9__add_missing_scopes.sql` para corrigir os problemas com scopes:

#### Problema Identificado
1. A migration V4 criou as tabelas de scopes mas não inseriu nenhum scope
2. A migration V5 tentava atribuir scopes aos usuários, mas os scopes não existiam no banco
3. O scope `levels:read` estava sendo usado no `CreateUserService` mas não estava sendo atribuído

#### Solução Implementada (V9)
1. **Insere TODOS os scopes necessários** com `ON CONFLICT DO NOTHING`:
   ```sql
   INSERT INTO tb_scopes (name) VALUES
       ('profile:read'), ('profile:write'), ('profile:delete'),
       ('character:read'), ('character:write'), ('levels:read'),
       ('quests:read'), ('quests:write'), ('quests:complete'),
       ('achievements:read'), ('users:read'), ('admin:all')
   ON CONFLICT (name) DO NOTHING;
   ```

2. **Atribui o scope `levels:read`** aos usuários existentes:
   - Para `ROLE_USER`: adiciona `levels:read`
   - Para `ROLE_MENTOR`: adiciona `levels:read`
   - Usa `NOT EXISTS` para evitar duplicatas
   - Usa `ON CONFLICT DO NOTHING` como segurança adicional

#### Alinhamento com CreateUserService
Após a V9, os scopes estarão alinhados com o método `getScopeNamesByRole()` do `CreateUserService`:

**ROLE_USER** recebe:
- profile:read, profile:write, profile:delete
- character:read, character:write
- levels:read ✅ (adicionado na V9)
- quests:read, quests:complete
- achievements:read

**ROLE_MENTOR** recebe:
- users:read
- profile:read, profile:write, profile:delete
- character:read, character:write
- levels:read ✅ (adicionado na V9)
- quests:read, quests:write, quests:complete
- achievements:read

**ROLE_ADMIN** recebe:
- admin:all

## Ordem de Execução das Migrations
1. V1: Cria tabela de usuários
2. V2: Cria tabela de personagens
3. V3: Adiciona campos active e role aos usuários
4. V4: Cria tabelas de scopes e relacionamento user_scopes
5. V5: Atribui scopes aos usuários existentes ⚠️ (assume que scopes já existem)
6. V6: Adiciona campos de confirmação de email
7. V7: Altera coluna active
8. V8: **Cria tabela de levels** ✅ (novo)
9. V9: **Insere scopes e corrige atribuições** ✅ (novo)

## ⚠️ IMPORTANTE: Regras de Migrations no Flyway

### NUNCA altere migrations já executadas!
- Flyway mantém um checksum de cada migration
- Se você alterar uma migration executada, o Flyway detecta e FALHA
- Sempre crie uma NOVA migration para correções

### Como funciona o Flyway:
1. Cada migration executada é registrada na tabela `flyway_schema_history`
2. O Flyway calcula um checksum do conteúdo da migration
3. Na próxima execução, ele verifica se o checksum mudou
4. Se mudou = ERRO e a aplicação não inicia

### Solução correta (implementada):
- ✅ V5 mantida no estado original
- ✅ V9 criada para adicionar os scopes faltantes
- ✅ V9 é idempotente (pode ser executada múltiplas vezes)

## Como Aplicar as Migrations

### Cenário 1: Banco de dados limpo (primeira vez)
```bash
./mvnw flyway:migrate
```
Todas as migrations (V1 até V9) serão executadas em ordem.

### Cenário 2: Já executou até V7 (mais comum)
```bash
./mvnw flyway:migrate
```
Apenas V8 e V9 serão executadas. As anteriores já estão no histórico do Flyway.

### Cenário 3: Resetar banco de dados (APENAS em desenvolvimento!)
```bash
./mvnw flyway:clean    # ⚠️ APAGA TODOS OS DADOS!
./mvnw flyway:migrate  # Executa todas as migrations novamente
```

### Verificar status das migrations
```bash
./mvnw flyway:info
```
Mostra quais migrations foram executadas e quais estão pendentes.

## Verificações Importantes

### 1. Após executar V8 (Levels)
```sql
-- Verificar criação da tabela
SELECT * FROM tb_levels ORDER BY order_level;

-- Deve retornar 10 níveis
SELECT COUNT(*) FROM tb_levels;  -- Esperado: 10
```

### 2. Após executar V9 (Scopes)
```sql
-- Verificar todos os scopes
SELECT * FROM tb_scopes ORDER BY name;

-- Deve retornar 12 scopes
SELECT COUNT(*) FROM tb_scopes;  -- Esperado: 12

-- Verificar usuários com levels:read
SELECT u.username, u.role, s.name
FROM tb_user u
JOIN tb_user_scopes us ON u.id = us.user_id
JOIN tb_scopes s ON us.scopes_id = s.id
WHERE s.name = 'levels:read'
ORDER BY u.username;
```

### 3. Verificar integridade da entidade Levels
A entidade `Levels.java` está alinhada com a migration:
- ✅ Todos os campos mapeados corretamente
- ✅ Enum `DifficutyLevel` com valores corretos (EASY, MEDIUM, HARD, EXPERT)
- ✅ Anotações JPA corretas

### 4. Testar CreateUserService
```bash
# Criar um novo usuário via API
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "teste",
    "email": "teste@example.com",
    "password": "Senha123!"
  }'

# Verificar os scopes atribuídos
# O novo usuário deve ter 9 scopes incluindo levels:read
```

## Possíveis Problemas e Soluções

### Problema: "FlywayValidateException: Validate failed: Migration checksum mismatch"
**Causa**: Você alterou uma migration já executada.
**Solução**: 
1. Reverta a alteração na migration original
2. Crie uma nova migration para a correção
3. Execute `./mvnw flyway:migrate`

### Problema: "scopes not found in database"
**Causa**: A V9 não foi executada ainda.
**Solução**: Execute `./mvnw flyway:migrate` para aplicar a V9.

### Problema: Duplicate key error ao executar V9
**Causa**: Os scopes ou atribuições já existem no banco.
**Solução**: Não é um problema! A V9 usa `ON CONFLICT DO NOTHING` e é idempotente.

## Próximos Passos Recomendados

1. **Aplicar as migrations**:
   ```bash
   ./mvnw flyway:migrate
   ```

2. **Verificar o status**:
   ```bash
   ./mvnw flyway:info
   ```

3. **Testar a criação de usuário**:
   - Criar um novo usuário via API
   - Verificar se os scopes são atribuídos corretamente
   - Confirmar que o scope `levels:read` está presente

4. **Testar endpoints de Levels**:
   - GET /api/levels - Listar todos os níveis
   - GET /api/levels/{id} - Buscar nível específico
   - Verificar se a autenticação com scope `levels:read` funciona

5. **Commit das alterações**:
   ```bash
   git add src/main/resources/db/migration/V8__create_tb_levels.sql
   git add src/main/resources/db/migration/V9__add_missing_scopes.sql
   git commit -m "feat: adiciona migration para tabela de levels e corrige scopes"
   ```

## Estrutura Final das Migrations

```
db/migration/
├── V1__create_tb_user.sql
├── V2__create_tb_player_character.sql
├── V3__add_active_and_role_to_user.sql
├── V4__create_scopes_and_user_scopes.sql
├── V5__assign_default_scopes_to_existing_users.sql (original mantida)
├── V6__add_email_confirmation_fields.sql
├── V7__alter_column_active.sql
├── V8__create_tb_levels.sql (✨ NOVA)
└── V9__add_missing_scopes.sql (✨ NOVA)
```

## Observações Técnicas

- A migration V8 usa `GENERATED ALWAYS AS IDENTITY` (padrão PostgreSQL 10+)
- Os índices foram criados para otimizar consultas frequentes
- A V9 usa `ON CONFLICT (name) DO NOTHING` para garantir idempotência
- A V9 usa `NOT EXISTS` para evitar duplicatas nas atribuições
- Os dados de exemplo são em português para melhor UX
- Todas as migrations são compatíveis com PostgreSQL 12+

## Boas Práticas para Futuras Migrations

1. **NUNCA altere migrations executadas** - sempre crie uma nova
2. **Use nomes descritivos** - V10__add_user_profile_picture.sql
3. **Seja idempotente** - use `IF NOT EXISTS`, `ON CONFLICT`, etc.
4. **Teste localmente** primeiro antes de aplicar em produção
5. **Documente mudanças complexas** - adicione comentários no SQL
6. **Faça backup** antes de aplicar migrations em produção

