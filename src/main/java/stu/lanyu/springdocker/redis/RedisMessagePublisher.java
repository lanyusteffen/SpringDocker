package stu.lanyu.springdocker.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import stu.lanyu.springdocker.contract.IMessagePublisher;

public class RedisMessagePublisher implements IMessagePublisher {

    private RedisTemplate<String, String> redisTemplate;
    private ChannelTopic topic;

    private RedisMessagePublisher() {
    }

    public RedisMessagePublisher(RedisTemplate<String, String> redisTemplate,
                                 ChannelTopic topic) {

        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    public void publish(String message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
