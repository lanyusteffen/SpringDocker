package stu.lanyu.springdocker.controller;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import stu.lanyu.springdocker.annotation.Approve;
import stu.lanyu.springdocker.config.GlobalConfig;
import stu.lanyu.springdocker.contract.entity.JobBreaker;
import stu.lanyu.springdocker.contract.entity.ServiceBreaker;
import stu.lanyu.springdocker.redis.entity.RegisterTask;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Map;

@RestController
@RequestMapping(value = "/breaker")
public class BreakerController {

    private OkHttpClient httpClient = null;

    public BreakerController() {

        httpClient = new OkHttpClient();
    }

    @Autowired
    private RedisTemplate<String, RegisterTask> redisTemplate;

    private ServiceBreaker getServiceBreaker(RegisterTask task, boolean isForTask, boolean isVeto, String jobName, String jobGroup) {

        ServiceBreaker serviceBreaker = new ServiceBreaker();

        if (isForTask) {
            serviceBreaker.setBreakerForTask(true);
            serviceBreaker.setActionToken(task.getActionToken());
            serviceBreaker.setAuthenticationFailure(false);
            serviceBreaker.setBreakerResult(false);
            serviceBreaker.setServiceIdentity(task.getServiceIdentity());
            serviceBreaker.setTaskVeto(isVeto);
        }
        else {
            serviceBreaker.setBreakerForTask(false);
            serviceBreaker.setAuthenticationFailure(false);

            JobBreaker jobBreaker = new JobBreaker();

            jobBreaker.setJobVeto(isVeto);
            jobBreaker.setBreakerResult(false);
            jobBreaker.setJobGroup(jobGroup);
            jobBreaker.setJobName(jobName);

            JobBreaker[] jobs = new JobBreaker[] { jobBreaker };
            serviceBreaker.setJobBreakers(jobs);
        }

        return serviceBreaker;
    }

    /**
     * 访问指定任务的断路WCF服务
     */
    private ServiceBreaker doBreakerService(RegisterTask task, boolean isForTask, boolean isVeto, String jobName, String jobGroup) {

        ServiceBreaker serviceBreaker = getServiceBreaker(task, isForTask, isVeto, jobName, jobGroup);

        try {

            Gson gson = new Gson();

            RequestBody body = RequestBody.create(GlobalConfig.WCFHost.JSON, gson.toJson(serviceBreaker));

            Request req = new Request.Builder()
                    .url(task.getBreakerUrl())
                    .post(body)
                    .build();

            Response resp = httpClient.newCall(req).execute();

            String respJson = resp.body().string();
            serviceBreaker = gson.fromJson(respJson, ServiceBreaker.class);
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return serviceBreaker;
    }

    @Approve
    @RequestMapping(value ="/doForTask", method = RequestMethod.GET)
    public boolean doForTask(String serviceIdentity, boolean isVeto) {

        Map<Object, Object> entries = redisTemplate.opsForHash().entries(GlobalConfig.Redis.REGISTER_TASK_CACHE_KEY);

        RegisterTask task = entries.entrySet().stream()
                .filter(map -> serviceIdentity.equals(map.getKey().toString()))
                .map(map -> RegisterTask.class.cast(map.getValue()))
                .findFirst().orElse(null);

        if (task != null) {
           ServiceBreaker serviceBreaker = doBreakerService(task, true, isVeto, null, null);
           return serviceBreaker.isBreakerResult();
        }

        return false;
    }

    @Approve
    @RequestMapping(value = "/doForJob", method = RequestMethod.GET)
    public boolean doForJob(String serviceIdentity, String jobName, String jobGroup, boolean isVeto) {

        Map<Object, Object> entries = redisTemplate.opsForHash().entries(GlobalConfig.Redis.REGISTER_TASK_CACHE_KEY);

        OkHttpClient httpClient = new OkHttpClient();

        RegisterTask task = entries.entrySet().stream()
                .filter(map -> serviceIdentity.equals(map.getKey().toString()))
                .map(map -> RegisterTask.class.cast(map.getValue()))
                .findFirst().orElse(null);

        if (task != null) {

            ServiceBreaker serviceBreaker = doBreakerService(task, false, isVeto, jobName, jobGroup);
            return serviceBreaker.isBreakerResult();
        }

        return false;
    }
}
