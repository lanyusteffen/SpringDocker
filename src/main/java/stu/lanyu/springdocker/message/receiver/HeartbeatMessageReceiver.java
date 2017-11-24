package stu.lanyu.springdocker.message.receiver;

import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import stu.lanyu.springdocker.config.GlobalConfig;
import stu.lanyu.springdocker.domain.User;
import stu.lanyu.springdocker.message.MessageProto;

import java.util.Base64;

public class HeartbeatMessageReceiver implements MessageListener {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {

        MessageProto.HeartbeatProto proto = null;

        try {

            byte[] decodedData = Base64.getDecoder().decode(message.getBody());
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
}
