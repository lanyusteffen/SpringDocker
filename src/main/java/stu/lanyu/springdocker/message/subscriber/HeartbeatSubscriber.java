package stu.lanyu.springdocker.message.subscriber;

import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPubSub;
import stu.lanyu.springdocker.config.GlobalConfig;
import stu.lanyu.springdocker.message.MessageProto;

import java.util.Base64;

public class HeartbeatSubscriber extends JedisPubSub {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void onMessage(String channel, String message) {

        MessageProto.HeartbeatProto proto = null;

        try {

            byte[] decodedData = Base64.getDecoder().decode(message);
            proto = MessageProto.HeartbeatProto.parseFrom(decodedData);

            redisTemplate.opsForHash().put(GlobalConfig.Redis.REGISTER_HEARTBEAT_CACHE_KEY, proto.getServiceIdentity(),
                    proto.getHeartbeatUrl());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSubscribe(String channel, int subscribedChannels) {
        System.out.println(String.format("subscribe redis channel '%s' success",
                channel));
    }

    public void onUnsubscribe(String channel, int subscribedChannels) {
        System.out.println(String.format("unsubscribe redis channel '%s'",
                channel));
    }
}
