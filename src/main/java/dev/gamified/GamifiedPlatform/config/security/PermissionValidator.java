package dev.gamified.GamifiedPlatform.config.security;

import dev.gamified.GamifiedPlatform.enums.Roles;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;

/*
  Classe utilitária para validações de permissão comuns.
  Centraliza lógica de autorização duplicada em múltiplos services.
 */
public final class PermissionValidator {

    private PermissionValidator() {
        throw new UnsupportedOperationException("Utility class");
    }

    //Valida se o usuário autenticado é o dono do recurso ou admin.
    public static void validateResourceOwnerOrAdmin(Long resourceOwnerId) {
        if (!SecurityUtils.isResourceOwnerOrAdmin(resourceOwnerId)) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
    }


     //Valida se o usuário autenticado é o dono do recurso, admin ou mentor.
    public static void validateResourceOwnerAdminOrMentor(Long resourceOwnerId) {
        if (!SecurityUtils.isResourceOwnerOrAdmin(resourceOwnerId) &&
            !SecurityUtils.hasRole(Roles.ROLE_MENTOR)) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
    }


     //Valida se o usuário autenticado tem role de admin.
    public static void validateAdminRole() {
        if (!SecurityUtils.hasAdminRole()) {
            throw new AccessDeniedException("Administrator privileges required");
        }
    }

    //Valida se o usuário autenticado tem uma role específica.
    public static void validateRole(Roles role) {
        if (!SecurityUtils.hasRole(role)) {
            throw new AccessDeniedException(
                String.format("Role %s is required for this operation", role.name())
            );
        }
    }
}

