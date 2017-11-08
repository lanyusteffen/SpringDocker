package stu.lanyu.springdocker.message;

import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import stu.lanyu.springdocker.config.GlobalConfig;
import stu.lanyu.springdocker.domain.RegisterJob;
import stu.lanyu.springdocker.domain.RegisterTask;

import java.util.ArrayList;
import java.util.Base64;

@Configuration
public class RegisterMessageReceiver implements MessageListener {

    @Autowired
    private RedisTemplate<String, RegisterTask> redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {

        MessageProto.RegisterServiceProto proto = null;

        try {

            byte[] decodedData = Base64.getDecoder().decode(message.getBody());
            proto = MessageProto.RegisterServiceProto.parseFrom(decodedData);

            RegisterTask registerTask = new RegisterTask();

            registerTask.setActionToken(proto.getActionToken());
            registerTask.setRegisterTime(proto.getRegisterTime());
            registerTask.setServiceIdentity(proto.getServiceIdentity());
            registerTask.setBreakerUrl(proto.getBreakerUrl());

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

            if (redisTemplate.opsForHash().putIfAbsent(GlobalConfig.Redis.REGISTER_TASK_CACHE_KEY, proto.getServiceIdentity(), registerTask)) {
                // TODO 记录日志
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
