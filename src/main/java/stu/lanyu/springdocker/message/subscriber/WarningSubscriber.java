package stu.lanyu.springdocker.message.subscriber;

import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import redis.clients.jedis.JedisPubSub;
import stu.lanyu.springdocker.business.readwrite.TaskWarningService;
import stu.lanyu.springdocker.domain.TaskWarning;
import stu.lanyu.springdocker.message.MessageProto;

import java.util.Base64;
import java.util.Date;

public class WarningSubscriber extends JedisPubSub {

    @Qualifier(value = "TaskWarningServiceReadwrite")
    @Autowired(required = true)
    private TaskWarningService taskWarningService;

    public void onMessage(String channel, String message) {

        MessageProto.WarningProto proto = null;

        try {

            byte[] decodedData = Base64.getDecoder().decode(message);

            proto = MessageProto.WarningProto.parseFrom(decodedData);

            TaskWarning taskWarning = new TaskWarning();

            taskWarning.setServiceIdentity(proto.getServiceIdentity());
            taskWarning.setJobGroup(proto.getJobGroup());
            taskWarning.setJobName(proto.getJobName());
            taskWarning.setWarningReason(proto.getWarningReason());
            taskWarning.setAddTime(new Date());

            taskWarningService.save(taskWarning);
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
