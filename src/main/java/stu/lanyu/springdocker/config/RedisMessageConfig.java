package stu.lanyu.springdocker.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.sentinel.api.StatefulRedisSentinelConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties(RedisMessageProperties.class)
public class RedisMessageConfig {

    @Autowired(required = true)
    private RedisMessageProperties redisMessageProperties;

    @Autowired(required = true)
    private ApplicationContext context;

    @Autowired(required = true)
    private RedisProperties properties;

    // Construct the RedisSentinelConfiguration using all the nodes in RedisSentinelNodes
    RedisURI sentinelConfiguration () {

       RedisURI.Builder builder = RedisURI.builder().withSentinelMasterId(properties.getSentinel().getMaster());

        List<String> nodes = properties.getSentinel().getNodes();

        for (int i = 0; i < nodes.size(); i++) {

            String node  = nodes.get(i);
            String[] ipAndPort = node.split(":");

            builder.withSentinel(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
        }

        return builder.build();
    }

    @Bean
    StatefulRedisSentinelConnection<String, String> redisConnectionFactory() {

        RedisClient client = RedisClient.create(sentinelConfiguration());
        StatefulRedisSentinelConnection<String, String> connection = client.connectSentinel();
        return connection;
    }
}
