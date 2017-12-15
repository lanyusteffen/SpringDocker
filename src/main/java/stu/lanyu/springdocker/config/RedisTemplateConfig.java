package stu.lanyu.springdocker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import stu.lanyu.springdocker.redis.entity.RegisterTask;
import stu.lanyu.springdocker.domain.User;
import stu.lanyu.springdocker.redis.RedisObjectSerializer;

@Configuration
public class RedisTemplateConfig {

    @Autowired
    @Qualifier("redisConnectionFactory")
    private JedisConnectionFactory jedisConnectionFactory;

    @Bean
    public RedisTemplate<String, User> redisUserTemplate() {

        RedisTemplate<String, User> redisTemplate = new RedisTemplate<String, User>();

        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new RedisObjectSerializer());
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, RegisterTask> redisRegisterTaskTemplate() {

        RedisTemplate<String, RegisterTask> redisTemplate = new RedisTemplate<String, RegisterTask>();

        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new RedisObjectSerializer());
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }
}
