package dev.gamified.GamifiedPlatform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:8080}")
    private String[] allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        //SEGURANÇA: Lista ESPECÍFICA de origens permitidas (não mais "*")
        corsConfiguration.setAllowedOrigins(Arrays.asList(allowedOrigins));

        // Métodos HTTP específicos
        corsConfiguration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        // Headers específicos permitidos
        corsConfiguration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept"
        ));

        // Headers expostos na resposta
        corsConfiguration.setExposedHeaders(Arrays.asList(
            "X-Total-Count",
            "X-Page-Number"
        ));

        // Permitir credenciais (necessário para cookies/auth)
        corsConfiguration.setAllowCredentials(true);

        // Cache de preflight por 1 hora
        corsConfiguration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

}
