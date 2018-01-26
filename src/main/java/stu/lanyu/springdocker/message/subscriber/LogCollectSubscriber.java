package stu.lanyu.springdocker.message.subscriber;

import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import redis.clients.jedis.JedisPubSub;
import stu.lanyu.springdocker.business.readwrite.LogCollectService;
import stu.lanyu.springdocker.domain.LogCollect;
import stu.lanyu.springdocker.message.MessageProto;
import stu.lanyu.springdocker.utility.DateUtility;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class LogCollectSubscriber extends JedisPubSub {

    @Qualifier(value = "LogCollectServiceReadwrite")
    @Autowired(required = true)
    private LogCollectService logCollectService;

    public void onMessage(String channel, String message) {

        MessageProto.LogCollectBatchProto proto = null;

        try {

            byte[] decodedData = Base64.getDecoder().decode(message);
            proto = MessageProto.LogCollectBatchProto.parseFrom(decodedData);

            List<LogCollect> logCollectArrayList = new ArrayList<LogCollect>();

            for (MessageProto.LogCollectProto logCollectProto : proto.getLogBatchList()) {

                for (MessageProto.LogProto logProto : logCollectProto.getLogsList()) {

                    LogCollect logCollect = new LogCollect();
                    logCollect.setLogTime(DateUtility.getDate(logProto.getLogTime()));
                    logCollect.setBody(logProto.getBody());
                    logCollect.setLevel(logProto.getLevel());
                    logCollect.setServiceIdentity(logCollectProto.getServiceIdentity());

                    logCollectArrayList.add(logCollect);
                }
            }

            if (logCollectArrayList.size() > 0) {
                logCollectService.saveInBatch(logCollectArrayList);
            }
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
