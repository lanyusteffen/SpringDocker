package stu.lanyu.springdocker.message.subscriber;

import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import redis.clients.jedis.JedisPubSub;
import stu.lanyu.springdocker.business.readwrite.TaskWarningService;
import stu.lanyu.springdocker.domain.TaskWarning;
import stu.lanyu.springdocker.message.MessageProto;
import stu.lanyu.springdocker.utility.DateUtility;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class WarningSubscriber extends JedisPubSub {

    @Qualifier(value = "TaskWarningServiceReadwrite")
    @Autowired(required = true)
    private TaskWarningService taskWarningService;

    public void onMessage(String channel, String message) {

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

    public void onSubscribe(String channel, int subscribedChannels) {
        System.out.println(String.format("subscribe redis channel '%s' success",
                channel));
    }

    public void onUnsubscribe(String channel, int subscribedChannels) {
        System.out.println(String.format("unsubscribe redis channel '%s'",
                channel));
    }
}
