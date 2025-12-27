package dev.gamified.GamifiedPlatform.enums;

import lombok.Getter;

@Getter
public enum ScopeType {
    // Permissões de usuários (gerenciamento geral)
    USERS_READ("users:read", "Ler informações de usuários"),
    USERS_WRITE("users:write", "Criar/atualizar usuários"),
    USERS_DELETE("users:delete", "Deletar usuários"),

    // Permissões de perfil (próprio usuário)
    PROFILE_READ("profile:read", "Ler próprio perfil"),
    PROFILE_WRITE("profile:write", "Atualizar próprio perfil"),
    PROFILE_DELETE("profile:delete", "Deletar própria conta"),

    // Permissões de personagem
    CHARACTER_READ("character:read", "Ler personagens"),
    CHARACTER_WRITE("character:write", "Criar/atualizar personagens"),
    CHARACTER_DELETE("character:delete", "Deletar personagens"),

    // Permissões de missões
    QUESTS_READ("quests:read", "Ler missões"),
    QUESTS_WRITE("quests:write", "Criar/atualizar missões"),
    QUESTS_COMPLETE("quests:complete", "Completar missões"),

    // Permissões de conquistas
    ACHIEVEMENTS_READ("achievements:read", "Ler conquistas"),

    // Permissão administrativa
    ADMIN_ALL("admin:all", "Acesso administrativo total");

    private final String scope;
    private final String description;

    ScopeType(String scope, String description) {
        this.scope = scope;
        this.description = description;
    }

    /**
     * Retorna os scopes padrão para um usuário comum (ROLE_USER)
     */
    public static ScopeType[] getDefaultUserScopes() {
        return new ScopeType[]{
                PROFILE_READ,
                PROFILE_WRITE,
                PROFILE_DELETE,
                CHARACTER_READ,
                CHARACTER_WRITE,
                QUESTS_READ,
                QUESTS_COMPLETE,
                ACHIEVEMENTS_READ
        };
    }

    /**
     * Retorna os scopes padrão para um mentor (ROLE_MENTOR)
     */
    public static ScopeType[] getMentorScopes() {
        return new ScopeType[]{
                USERS_READ,
                PROFILE_READ,
                PROFILE_WRITE,
                PROFILE_DELETE,
                CHARACTER_READ,
                CHARACTER_WRITE,
                QUESTS_READ,
                QUESTS_WRITE,
                QUESTS_COMPLETE,
                ACHIEVEMENTS_READ
        };
    }

    /**
     * Retorna os scopes para um administrador (ROLE_ADMIN)
     */
    public static ScopeType[] getAdminScopes() {
        return new ScopeType[]{
                ADMIN_ALL
        };
    }

    /**
     * Retorna scopes baseados na role
     */
    public static ScopeType[] getScopesByRole(Roles role) {
        return switch (role) {
            case ROLE_USER -> getDefaultUserScopes();
            case ROLE_MENTOR -> getMentorScopes();
            case ROLE_ADMIN -> getAdminScopes();
        };
    }
}

