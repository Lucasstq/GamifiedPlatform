package dev.gamified.GamifiedPlatform.config.security;

import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.enums.AuthProvider;
import dev.gamified.GamifiedPlatform.services.auth.OAuth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapter que integra o OAuth2UserService customizado ao fluxo do Spring Security OAuth2.
 * Processa usuários OAuth2 durante a autenticação e armazena informações no contexto.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserServiceAdapter implements org.springframework.security.oauth2.client.userinfo.OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final OAuth2UserService oAuth2UserService;
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            // Carregar dados do OAuth2 provider usando o serviço padrão
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            // Obter provider (google, github, etc)
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());

            log.info("Processing OAuth2 user from provider: {}", provider);

            // Processar usuário com o serviço customizado
            User user = oAuth2UserService.processOAuth2User(provider, oAuth2User.getAttributes());

            // Adicionar userId aos atributos para uso posterior nos handlers
            Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
            attributes.put("userId", user.getId());
            attributes.put("username", user.getUsername());

            // Determinar o atributo de nome usado pelo provider
            String nameAttributeKey = userRequest.getClientRegistration()
                    .getProviderDetails()
                    .getUserInfoEndpoint()
                    .getUserNameAttributeName();

            // Retornar OAuth2User com os atributos atualizados
            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())),
                    attributes,
                    nameAttributeKey
            );

        } catch (Exception e) {
            log.error("Error processing OAuth2 user: {}", e.getMessage(), e);
            throw new OAuth2AuthenticationException("Failed to process OAuth2 user: " + e.getMessage());
        }
    }
}

