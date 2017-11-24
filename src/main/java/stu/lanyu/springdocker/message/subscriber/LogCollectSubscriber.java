package stu.lanyu.springdocker.message.subscriber;

import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import redis.clients.jedis.JedisPubSub;
import stu.lanyu.springdocker.business.readwrite.LogCollectService;
import stu.lanyu.springdocker.domain.LogCollect;
import stu.lanyu.springdocker.message.MessageProto;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

public class LogCollectSubscriber extends JedisPubSub {

    @Qualifier(value = "LogCollectServiceReadwrite")
    @Autowired(required = true)
    private LogCollectService logCollectService;

    public void onMessage(String channel, String message) {

        MessageProto.LogCollectProto proto = null;

        try {

            byte[] decodedData = Base64.getDecoder().decode(message);
            proto = MessageProto.LogCollectProto.parseFrom(decodedData);

            ArrayList<LogCollect> logCollectArrayList = new ArrayList<LogCollect>();

            for (MessageProto.LogProto log : proto.getLogsList()) {

                LogCollect logCollect = new LogCollect();

                logCollect.setLogTime(new Date(log.getLogTime()));
                logCollect.setBody(log.getBody());
                logCollect.setLevel(log.getLevel());
                logCollect.setServiceIdentity(proto.getServiceIdentity());

                logCollectArrayList.add(logCollect);
            }

            logCollectService.saveInBatch(logCollectArrayList);
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
