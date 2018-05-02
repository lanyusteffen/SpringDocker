package stu.lanyu.springdocker.messagequeue.consumer;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import stu.lanyu.springdocker.business.readwrite.TaskWarningService;
import stu.lanyu.springdocker.domain.entity.TaskWarning;
import stu.lanyu.springdocker.messagequeue.contract.MessageProto;
import stu.lanyu.springdocker.utility.DateUtility;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class WarningSubscriber {

    @Qualifier(value = "TaskWarningServiceReadwrite")
    @Autowired(required = true)
    private TaskWarningService taskWarningService;

    @KafkaListener(topics = {"Warning"})
    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack) {

        Optional<String> kafkaMessage = Optional.ofNullable(record.value());

        if (kafkaMessage.isPresent()) {

            String message = kafkaMessage.get();

            processMessage(message);

            ack.acknowledge();
        }

    }

    public void processMessage(String message) {

        MessageProto.WarningBatchProto proto = null;

        try {

            byte[] decodedData = Base64.getDecoder().decode(message);

            proto = MessageProto.WarningBatchProto.parseFrom(decodedData);

            List<TaskWarning> taskWarningList = new ArrayList<>();

            for (MessageProto.WarningProto warningProto : proto.getWarningBatchList()) {

                TaskWarning taskWarning = new TaskWarning();

                taskWarning.setServiceIdentity(warningProto.getServiceIdentity());
                taskWarning.setJobGroup(warningProto.getJobGroup());
                taskWarning.setJobName(warningProto.getJobName());
                taskWarning.setWarningReason(warningProto.getWarningReason());
                taskWarning.setAddTime(DateUtility.getDate(warningProto.getWarningTime()));

                taskWarningList.add(taskWarning);
            }

            if (taskWarningList.size() > 0) {
                taskWarningService.saveBatch(taskWarningList);
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
