package dev.gamified.GamifiedPlatform.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração da documentação OpenAPI/Swagger para a API Gamified Platform.
 *
 * A documentação estará disponível em:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Bean
    public OpenAPI gamifiedPlatformOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url(baseUrl)
                                .description("Servidor de Desenvolvimento")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", securityScheme())
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .tags(apiTags());
    }

    private Info apiInfo() {
        return new Info()
                .title("Gamified Platform API")
                .description("""
                        # Plataforma Educacional Gamificada - Dark Fantasy Theme
                        
                        API REST para uma plataforma educacional gamificada com temática Dark Fantasy que transforma 
                        o aprendizado de programação em uma jornada épica.
                        
                        ## Funcionalidades Principais
                        
                        - **Sistema de Níveis**: Evolua através de 10 níveis de conhecimento
                        - **Missões**: Complete desafios de programação para ganhar XP
                        - **Bosses**: Enfrente desafios épicos ao final de cada nível
                        - **Badges**: Conquiste distintivos especiais
                        - **Grimórios**: Desbloqueie materiais educacionais ao progredir
                        - **Ranking**: Compare seu progresso com outros jogadores
                        - **Notificações**: Receba atualizações sobre seu progresso
                        
                        ## Autenticação
                        
                        A API utiliza JWT Bearer Token para autenticação. Para acessar endpoints protegidos:
                        
                        1. Faça login via `/auth/login` com suas credenciais
                        2. Use o token JWT retornado no header `Authorization: Bearer {token}`
                        3. O token expira em 15 minutos, use `/auth/refresh` para renovar
                        
                        ## Paginação
                        
                        Endpoints que retornam listas suportam paginação via parâmetros:
                        - `page`: número da página (padrão: 0)
                        - `size`: tamanho da página (padrão: 20)
                        - `sort`: ordenação (ex: `name,asc` ou `createdAt,desc`)
                        
                        ## Códigos de Status
                        
                        - `200 OK`: Requisição bem-sucedida
                        - `201 Created`: Recurso criado com sucesso
                        - `204 No Content`: Requisição bem-sucedida sem conteúdo
                        - `400 Bad Request`: Dados inválidos
                        - `401 Unauthorized`: Não autenticado
                        - `403 Forbidden`: Sem permissão
                        - `404 Not Found`: Recurso não encontrado
                        - `429 Too Many Requests`: Rate limit excedido
                        - `500 Internal Server Error`: Erro do servidor
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("Gamified Platform Team")
                        .email("support@gamifiedplatform.dev")
                )
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT")
                );
    }

    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("""
                        JWT Bearer Token Authentication
                        
                        Para obter um token:
                        1. Faça uma requisição POST para `/auth/login` com username e password
                        2. Copie o token JWT do campo `accessToken` na resposta
                        3. Clique no botão 'Authorize' acima e insira o token
                        
                        O token expira em 15 minutos. Use `/auth/refresh` para renovar.
                        """);
    }

    private List<Tag> apiTags() {
        return List.of(
                new Tag()
                        .name("Autenticação")
                        .description("Endpoints para login, registro e gerenciamento de sessão"),
                new Tag()
                        .name("Usuários")
                        .description("Gerenciamento de perfis de usuário"),
                new Tag()
                        .name("Níveis")
                        .description("Sistema de progressão e níveis de conhecimento"),
                new Tag()
                        .name("Missões")
                        .description("Desafios de programação e sistema de XP"),
                new Tag()
                        .name("Bosses")
                        .description("Desafios épicos ao final de cada nível"),
                new Tag()
                        .name("Badges")
                        .description("Sistema de conquistas e distintivos"),
                new Tag()
                        .name("Grimórios")
                        .description("Materiais educacionais e PDFs desbloqueáveis"),
                new Tag()
                        .name("Ranking")
                        .description("Classificação global de jogadores"),
                new Tag()
                        .name("Notificações")
                        .description("Sistema de notificações em tempo real"),
                new Tag()
                        .name("Admin")
                        .description("Endpoints administrativos (apenas ADMIN)")
        );
    }
}

