package it.moneyverse.core.boot;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.moneyverse.core.utils.properties.RedisProperties;
import java.time.Duration;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
@EnableConfigurationProperties(RedisProperties.class)
public class RedisAutoConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(RedisAutoConfiguration.class);

  private final RedisProperties properties;

  public RedisAutoConfiguration(RedisProperties properties) {
    this.properties = properties;
    LOGGER.info("Starting to load beans from {}", RedisAutoConfiguration.class);
  }

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
    redisStandaloneConfiguration.setHostName(properties.getHost());
    redisStandaloneConfiguration.setPort(properties.getPort());
    redisStandaloneConfiguration.setPassword(RedisPassword.of(properties.getPassword()));

    LettuceClientConfiguration.LettuceClientConfigurationBuilder lettuceClientConfiguration =
        LettuceClientConfiguration.builder();
    lettuceClientConfiguration.commandTimeout(
        Duration.ofSeconds(properties.getConnectionTimeout()));

    return new LettuceConnectionFactory(
        redisStandaloneConfiguration, lettuceClientConfiguration.build());
  }

  @Bean
  public RedisTemplate<UUID, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<UUID, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
        new Jackson2JsonRedisSerializer<>(Object.class);

    template.setValueSerializer(jackson2JsonRedisSerializer);
    template.setHashValueSerializer(jackson2JsonRedisSerializer);
    template.setKeySerializer(new StringRedisSerializer());
    return template;
  }

  @Bean
  public RedisCacheConfiguration defaultCacheConfiguration() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    objectMapper.activateDefaultTyping(
        objectMapper.getPolymorphicTypeValidator(),
        ObjectMapper.DefaultTyping.NON_FINAL,
        JsonTypeInfo.As.PROPERTY);
    return RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofMinutes(30))
        .disableCachingNullValues()
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                new Jackson2JsonRedisSerializer<>(objectMapper, Object.class)));
  }

  @Bean
  public RedisCacheManager cacheManager(
      RedisConnectionFactory redisConnectionFactory,
      RedisCacheConfiguration defaultCacheConfiguration) {
    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(defaultCacheConfiguration)
        .build();
  }
}
