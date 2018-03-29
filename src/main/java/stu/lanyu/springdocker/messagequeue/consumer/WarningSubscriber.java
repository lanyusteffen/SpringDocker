package stu.lanyu.springdocker.messagequeue.consumer;

import com.google.protobuf.InvalidProtocolBufferException;
import io.lettuce.core.pubsub.RedisPubSubListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import stu.lanyu.springdocker.business.readwrite.TaskWarningService;
import stu.lanyu.springdocker.domain.entity.TaskWarning;
import stu.lanyu.springdocker.messagequeue.contract.MessageProto;
import stu.lanyu.springdocker.utility.DateUtility;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class WarningSubscriber implements RedisPubSubListener<String, String> {

    @Qualifier(value = "TaskWarningServiceReadwrite")
    @Autowired(required = true)
    private TaskWarningService taskWarningService;

    @Override
    public void message(String channel, String message) {

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
