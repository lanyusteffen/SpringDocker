package stu.lanyu.springdocker.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.resource.ClientResources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import stu.lanyu.springdocker.domain.User;
import stu.lanyu.springdocker.redis.RedisObjectSerializer;

import java.time.Duration;
import java.util.Optional;

@Configuration
public class RedisTemplateConfig {

    @Autowired(required = true)
    private RedisMessageProperties redisMessageProperties;

    private LettuceConnectionFactory getLettuceConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration =
                new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisMessageProperties.getHost());
        redisStandaloneConfiguration.setPort(redisMessageProperties.getPort());
        redisStandaloneConfiguration.setPassword(RedisPassword.of(redisMessageProperties.getPassword()));

        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, User> redisUserTemplate() {

        RedisTemplate<String, User> redisTemplate = new RedisTemplate<String, User>();

        redisTemplate.setConnectionFactory(getLettuceConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new RedisObjectSerializer());
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }
}
