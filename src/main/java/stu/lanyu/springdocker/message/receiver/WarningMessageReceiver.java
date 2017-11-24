package stu.lanyu.springdocker.message.receiver;

import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import stu.lanyu.springdocker.business.readwrite.TaskWarningService;
import stu.lanyu.springdocker.domain.TaskWarning;
import stu.lanyu.springdocker.message.MessageProto;

import java.util.Base64;
import java.util.Date;

@Configuration
public class WarningMessageReceiver implements MessageListener {

    @Qualifier(value = "TaskWarningServiceReadwrite")
    @Autowired(required = true)
    private TaskWarningService taskWarningService;

    @Override
    public void onMessage(Message message, byte[] pattern) {

        MessageProto.WarningProto proto = null;

        try {

            byte[] decodedData = Base64.getDecoder().decode(message.getBody());

            proto = MessageProto.WarningProto.parseFrom(decodedData);

            TaskWarning taskWarning = new TaskWarning();

            taskWarning.setServiceIdentity(proto.getServiceIdentity());
            taskWarning.setJobGroup(proto.getJobGroup());
            taskWarning.setJobName(proto.getJobName());
            taskWarning.setWarningReason(proto.getWarningReason());
            taskWarning.setAddTime(new Date(proto.getWarningTime()));

            taskWarningService.save(taskWarning);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
