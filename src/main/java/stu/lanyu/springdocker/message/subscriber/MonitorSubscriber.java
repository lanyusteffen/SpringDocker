package stu.lanyu.springdocker.message.subscriber;

import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPubSub;
import stu.lanyu.springdocker.config.GlobalConfig;
import stu.lanyu.springdocker.message.MessageProto;
import stu.lanyu.springdocker.redis.entity.RegisterJob;
import stu.lanyu.springdocker.redis.entity.RegisterTask;

import java.util.ArrayList;
import java.util.Base64;

public class MonitorSubscriber extends JedisPubSub {

    @Autowired
    private RedisTemplate<String, RegisterTask> redisTemplate;

    public void onMessage(String channel, String message) {

        MessageProto.MonitorTaskProto proto = null;

        try {

            byte[] decodedData = Base64.getDecoder().decode(message);
            proto = MessageProto.MonitorTaskProto.parseFrom(decodedData);

            RegisterTask registerTask = new RegisterTask();

            registerTask.setActionToken(proto.getActionToken());
            registerTask.setRegisterTime(proto.getRegisterTime());
            registerTask.setServiceIdentity(proto.getServiceIdentity());
            registerTask.setBreakerUrl(proto.g());

            ArrayList<RegisterJob> registerJobs = new ArrayList<RegisterJob>();

            for (stu.lanyu.springdocker.message.MessageProto.RegisterJobProto registerJobProto : proto.getRegisterJobsList()
                    ) {

                RegisterJob registerJob = new RegisterJob();

                registerJob.setJobClassType(registerJobProto.getJobClassType());
                registerJob.setJobGroup(registerJobProto.getJogGroup());
                registerJob.setJobName(registerJobProto.getJobName());
                registerJob.setRepeatCount(registerJobProto.getRepeatCount());
                registerJob.setRepeatInterval(registerJobProto.getRepeatInterval());
                registerJob.setTriggerGroup(registerJobProto.getTriggerGroup());
                registerJob.setTriggerName(registerJobProto.getTriggerName());

                registerJobs.add(registerJob);
            }

            registerTask.setRegisterJobs(registerJobs);

            redisTemplate.opsForHash().put(GlobalConfig.Redis.REGISTER_TASK_CACHE_KEY, proto.getServiceIdentity(), registerTask);
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
