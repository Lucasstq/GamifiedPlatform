package dev.gamified.GamifiedPlatform.config.annotations;
}
public @interface CanReadLevels {
@PreAuthorize("hasAnyAuthority('SCOPE_admin:all', 'SCOPE_levels:read')")
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})

import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;

import org.springframework.security.access.prepost.PreAuthorize;


