package stu.lanyu.springdocker.message.receiver;

import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import stu.lanyu.springdocker.business.readwrite.LogCollectService;
import stu.lanyu.springdocker.domain.LogCollect;
import stu.lanyu.springdocker.message.MessageProto;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

public class LogCollectMessageReceiver implements MessageListener {

    @Qualifier(value = "LogCollectServiceReadwrite")
    @Autowired(required = true)
    private LogCollectService logCollectService;

    @Override
    public void onMessage(Message message, byte[] pattern) {

        MessageProto.LogCollectProto proto = null;

        try {

            byte[] decodedData = Base64.getDecoder().decode(message.getBody());
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
}
