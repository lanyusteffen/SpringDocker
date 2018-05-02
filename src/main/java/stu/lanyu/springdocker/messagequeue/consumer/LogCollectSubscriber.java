package stu.lanyu.springdocker.messagequeue.consumer;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import stu.lanyu.springdocker.business.readwrite.LogCollectService;
import stu.lanyu.springdocker.domain.entity.LogCollect;
import stu.lanyu.springdocker.messagequeue.contract.MessageProto;
import stu.lanyu.springdocker.utility.DateUtility;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Component
public class LogCollectSubscriber {

    @Qualifier(value = "LogCollectServiceReadwrite")
    @Autowired(required = true)
    private LogCollectService logCollectService;

    @KafkaListener(topics = {"LogCollect"})
    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack) {

        Optional<String> kafkaMessage = Optional.ofNullable(record.value());

        if (kafkaMessage.isPresent()) {

            String message = kafkaMessage.get();

            processMessage(message);

            ack.acknowledge();
        }
    }

    public void processMessage(String message) {

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
}
