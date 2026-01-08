package dev.gamified.GamifiedPlatform.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuração do Redis para cache e armazenamento de dados.
 * Utiliza RedisSerializer para serialização JSON com suporte a tipos complexos.
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * Configura o ObjectMapper para serialização JSON no Redis.
     * - Suporta tipos polimórficos (herança)
     * - Suporta Java 8 Time API (LocalDateTime, etc)
     * - Adiciona informações de tipo para deserialização correta
     */
    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Suporte para LocalDateTime, LocalDate, etc
        mapper.registerModule(new JavaTimeModule());

        // Validador para tipos polimórficos (segurança)
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("dev.codegrimoire")
                .build();

        // Adiciona informações de tipo no JSON para deserialização correta
        mapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        return mapper;
    }

    /**
     * Template genérico para operações com Redis.
     * Usado para operações customizadas que não são cache.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        RedisSerializer<String> keySerializer = RedisSerializer.string();
        RedisSerializer<Object> valueSerializer =
                RedisSerializer.json();

        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * Gerenciador de cache do Spring com Redis.
     * Define TTLs diferentes para cada tipo de cache.
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        RedisSerializer<Object> jsonSerializer =
                RedisSerializer.json();

        // Configuração padrão para todos os caches
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))  // 1 hora padrão
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(jsonSerializer))
                .disableCachingNullValues();

        // Configurações específicas por cache
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Cache de níveis - praticamente imutável, TTL longo
        cacheConfigurations.put("levels", defaultConfig.entryTtl(Duration.ofHours(24)));

        // Cache de missões - muda pouco, TTL longo
        cacheConfigurations.put("missions", defaultConfig.entryTtl(Duration.ofHours(12)));

        // Cache de missões por nível - muda pouco
        cacheConfigurations.put("missionsByLevel", defaultConfig.entryTtl(Duration.ofHours(12)));

        // Cache de scopes - praticamente imutável
        cacheConfigurations.put("scopes", defaultConfig.entryTtl(Duration.ofHours(24)));

        // Cache de tokens de verificação - expira em 24h
        cacheConfigurations.put("emailVerificationTokens", defaultConfig.entryTtl(Duration.ofHours(24)));

        // Rate limiting - expira rápido
        cacheConfigurations.put("rateLimiting", defaultConfig.entryTtl(Duration.ofMinutes(5)));

        // Cache de ranking global - atualizado a cada 5 minutos
        cacheConfigurations.put("ranking", defaultConfig.entryTtl(Duration.ofMinutes(5)));

        // Cache de ranking por nível - atualizado a cada 5 minutos
        cacheConfigurations.put("rankingByLevel", defaultConfig.entryTtl(Duration.ofMinutes(5)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }
}

