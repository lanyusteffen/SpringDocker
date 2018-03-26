package stu.lanyu.springdocker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import stu.lanyu.springdocker.domain.User;
import stu.lanyu.springdocker.redis.RedisObjectSerializer;

@Configuration
public class RedisTemplateConfig {

    private LettuceConnectionFactory getLettuceConnectionFactory() {
        LettuceConnectionFactory lettuceConnectionFactory =
            new LettuceConnectionFactory();
        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;
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
