package stu.lanyu.springdocker.message.subscriber;

import com.google.protobuf.InvalidProtocolBufferException;
import io.lettuce.core.pubsub.RedisPubSubListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import stu.lanyu.springdocker.business.readwrite.LogCollectService;
import stu.lanyu.springdocker.domain.LogCollect;
import stu.lanyu.springdocker.message.MessageProto;
import stu.lanyu.springdocker.utility.DateUtility;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class LogCollectSubscriber implements RedisPubSubListener<String, String> {

    @Qualifier(value = "LogCollectServiceReadwrite")
    @Autowired(required = true)
    private LogCollectService logCollectService;

    @Override
    public void message(String channel, String message) {

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
    }

    @Override
    public void message(String pattern, String channel, String message) {

        message(channel, message);
    }

    @Override
    public void subscribed(String channel, long count) {

        System.out.println(String.format("subscribe redis channel '%s' success",
                channel));
    }

    @Override
    public void psubscribed(String pattern, long count) {

        System.out.println(String.format("subscribe redis channel with match pattern '%s' success",
                pattern));
    }

    @Override
    public void unsubscribed(String channel, long count) {
        System.out.println(String.format("unsubscribe redis channel '%s'",
                channel));
    }

    @Override
    public void punsubscribed(String pattern, long count) {

        System.out.println(String.format("unsubscribe redis channel with match pattern '%s'",
                pattern));
    }
}
