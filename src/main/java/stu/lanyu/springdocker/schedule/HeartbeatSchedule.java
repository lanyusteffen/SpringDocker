package stu.lanyu.springdocker.schedule;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import stu.lanyu.springdocker.business.readonly.TaskMonitorInfoService;
import stu.lanyu.springdocker.config.GlobalConfig;
import stu.lanyu.springdocker.config.RedisMessageProperties;
import stu.lanyu.springdocker.contract.entity.HeartbeatInfo;
import stu.lanyu.springdocker.contract.entity.JobMonitorInfo;
import stu.lanyu.springdocker.domain.TaskMonitorInfo;
import stu.lanyu.springdocker.message.ScheduledExecutorServiceFacade;
import stu.lanyu.springdocker.message.subscriber.HeartbeatSubscriber;
import stu.lanyu.springdocker.message.subscriber.LogCollectSubscriber;
import stu.lanyu.springdocker.message.subscriber.RegisterSubscriber;
import stu.lanyu.springdocker.message.subscriber.WarningSubscriber;
import stu.lanyu.springdocker.utility.StringUtility;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
public class HeartbeatSchedule {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private stu.lanyu.springdocker.business.readwrite.TaskMonitorInfoService taskMonitorInfoService;

    @Autowired
    private TaskMonitorInfoService taskMonitorInfoQueryService;

    @Autowired
    private stu.lanyu.springdocker.business.readwrite.JobMonitorInfoService jobMonitorInfoService;

    private HeartbeatInfo doCheckHeartbeat(String url, OkHttpClient httpClient, Gson gson) {

        HeartbeatInfo result = null;

        try {
            Request req = new Request.Builder()
                    .url(url).build();
            Response resp = httpClient.newCall(req).execute();
            result = gson.fromJson(resp.body().string(), HeartbeatInfo.class);
        }
        catch (ConnectException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private void saveHeartbeatInfo(TaskMonitorInfo taskMonitorInfo, HeartbeatInfo result) {

        taskMonitorInfoService.save(taskMonitorInfo);
//        jobMonitorInfoService.saveBatch(taskMonitorInfo.getJobs());

        List<stu.lanyu.springdocker.domain.JobMonitorInfo> deletedJobList = new ArrayList<>();

        taskMonitorInfo.getJobs().stream().forEach(r -> {

            boolean isExisted = false;

            for (JobMonitorInfo jobMonitorInfo : result.getMonitorInfos()
                    ) {

                String currentJobName = (StringUtility.isNullOrEmpty(jobMonitorInfo.getJobName())
                        ? "" : jobMonitorInfo.getJobName());
                String currentJobGroup = (StringUtility.isNullOrEmpty(jobMonitorInfo.getJobGroup())
                        ? "" : jobMonitorInfo.getJobGroup());

                if (currentJobName.equals(r.getJobName()) && currentJobGroup.equals(r.getJobGroup())) {
                    isExisted = true;
                    break;
                }
            }

            if (!isExisted) {
                deletedJobList.add(r);
            }
        });

        if (deletedJobList.size() > 0) {
            jobMonitorInfoService.deleteBatch(deletedJobList);
        }
    }

    @Autowired(required = true)
    private ApplicationContext context;

    @Autowired(required = true)
    private RedisMessageProperties redisMessageProperties;

    private JedisPool getJedisPool() {
        return context.getBean("RedisSubscriberMessagePool",
                JedisPool.class);
    }

    private ScheduledExecutorService getHeartbeatScheduledExecutorService() {
        HeartbeatSubscriber heartbeatSubscriber = context.getBean(HeartbeatSubscriber.class);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.execute(() -> {

            JedisPool pool = getJedisPool();
            Jedis jedis = null;

            try {
                jedis = pool.getResource();
                jedis.subscribe(heartbeatSubscriber, GlobalConfig.Redis.ESFTASK_HEARTBEAT_CHANNEL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (jedis != null)
                    jedis.close();
            }
        });
        return service;
    }

    private ScheduledExecutorService getWarningScheduledExecutorService() {
        WarningSubscriber warningSubscriber = context.getBean(WarningSubscriber.class);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.execute(() -> {

            JedisPool pool = getJedisPool();
            Jedis jedis = null;

            try {
                jedis = pool.getResource();
                jedis.subscribe(warningSubscriber, GlobalConfig.Redis.ESFTASK_WARNING_CHANNEL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (jedis != null)
                    jedis.close();
            }
        });
        return service;
    }

    private ScheduledExecutorService getRegisterScheduledExecutorService() {
        RegisterSubscriber registerSubscriber = context.getBean(RegisterSubscriber.class);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.execute(() -> {

            JedisPool pool = getJedisPool();
            Jedis jedis = null;

            try {
                jedis = pool.getResource();
                jedis.subscribe(registerSubscriber, GlobalConfig.Redis.ESFTASK_REGISTER_CHANNEL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (jedis != null)
                    jedis.close();
            }
        });
        return service;
    }

    private ScheduledExecutorService getLogCollectScheduledExecutorService() {
        LogCollectSubscriber logCollectSubscriber = context.getBean(LogCollectSubscriber.class);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.execute(() -> {

            JedisPool pool = getJedisPool();
            Jedis jedis = null;

            try {
                jedis = pool.getResource();
                jedis.subscribe(logCollectSubscriber, GlobalConfig.Redis.ESFTASK_PUSHLOG_CHANNEL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (jedis != null)
                    jedis.close();
            }
        });
        return service;
    }

    @Scheduled(fixedDelay = 300000, initialDelay = 300000)
    public void checkSubscriber() {

        ScheduledExecutorServiceFacade serviceHeartbeatFacade = context.getBean("HeartbeatExecutorService", ScheduledExecutorServiceFacade.class);

        if (serviceHeartbeatFacade != null) {

            if (serviceHeartbeatFacade.getScheduleExecutorService().isShutdown()) {

                serviceHeartbeatFacade.setScheduleExecutorService(getHeartbeatScheduledExecutorService());
            }
        }

        ScheduledExecutorServiceFacade serviceLogCollectFacade = context.getBean("LogCollectExecutorService", ScheduledExecutorServiceFacade.class);

        if (serviceLogCollectFacade != null) {

            if (serviceLogCollectFacade.getScheduleExecutorService().isShutdown()) {

                serviceLogCollectFacade.setScheduleExecutorService(getLogCollectScheduledExecutorService());
            }
        }

        ScheduledExecutorServiceFacade serviceWarningFacade = context.getBean("WarningExecutorService", ScheduledExecutorServiceFacade.class);

        if (serviceWarningFacade != null) {

            if (serviceWarningFacade.getScheduleExecutorService().isShutdown()) {

                serviceWarningFacade.setScheduleExecutorService(getWarningScheduledExecutorService());
            }
        }

        ScheduledExecutorServiceFacade serviceRegisterFacade = context.getBean("RegisterExecutorService", ScheduledExecutorServiceFacade.class);

        if (serviceRegisterFacade != null) {

            if (serviceRegisterFacade.getScheduleExecutorService().isShutdown()) {

                serviceRegisterFacade.setScheduleExecutorService(getRegisterScheduledExecutorService());
            }
        }
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 60000)
    public void checkHeartbeat() {

        Map<Object, Object> entries = redisTemplate.opsForHash().entries(GlobalConfig.Redis.REGISTER_HEARTBEAT_CACHE_KEY);

        OkHttpClient httpClient = new OkHttpClient();

        Gson gson = new Gson();

        for (Map.Entry<Object, Object> entry : entries.entrySet()
             ) {
            String url = entry.getValue().toString();
            String serviceIdentity = entry.getKey().toString();

            try {
                HeartbeatInfo result = doCheckHeartbeat(url, httpClient, gson);

                TaskMonitorInfo taskMonitorInfo = taskMonitorInfoQueryService.getListPagedByServiceIdentity(serviceIdentity);

                if (taskMonitorInfo == null) {

                    taskMonitorInfo = new TaskMonitorInfo();
                    taskMonitorInfo.setJobs(new ArrayList<>());
                    taskMonitorInfo.setServiceIdentity(serviceIdentity);
                }

                taskMonitorInfo.setLastHeartbeatTime(new Date());
                taskMonitorInfo.setHeartbeatUrl(url);

                if (result == null) {
                    taskMonitorInfo.setHeartbeatBreak(true);
                }
                else {
                    taskMonitorInfo.setTaskVeto(result.isVetoForTask());
                    taskMonitorInfo.setHeartbeatBreak(false);

                    for (JobMonitorInfo jobMonitorInfo : result.getMonitorInfos()) {

                        String currentJobName = (StringUtility.isNullOrEmpty(jobMonitorInfo.getJobName())
                                ? "" : jobMonitorInfo.getJobName());
                        String currentJobGroup = (StringUtility.isNullOrEmpty(jobMonitorInfo.getJobGroup())
                                ? "" : jobMonitorInfo.getJobGroup());

                        stu.lanyu.springdocker.domain.JobMonitorInfo findedJobMonitorInfo = taskMonitorInfo.getJobs().stream()
                                .filter(j -> currentJobName.equals(j.getJobName()) && currentJobGroup.equals(j.getJobGroup()))
                                .findFirst()
                                .orElse(null);

                        if (findedJobMonitorInfo == null) {

                            findedJobMonitorInfo = new stu.lanyu.springdocker.domain.JobMonitorInfo();
                            findedJobMonitorInfo.setJobName(jobMonitorInfo.getJobName());
                            findedJobMonitorInfo.setJobGroup(jobMonitorInfo.getJobGroup());
                            findedJobMonitorInfo.setServiceIdentity(serviceIdentity);
                            findedJobMonitorInfo.setJobVeto(jobMonitorInfo.isJobVeto());
                            taskMonitorInfo.getJobs().add(findedJobMonitorInfo);
                        }

                        taskMonitorInfo.getJobs().forEach(r -> {

                            if (r.getJobName() == jobMonitorInfo.getJobName() && r.getJobGroup() == jobMonitorInfo.getJobGroup()) {

                                r.setFiredTimes(jobMonitorInfo.getFiredTimes());
                                r.setJobCompletedLastTime(new Date(jobMonitorInfo.getJobCompletedLastTime()));
                                r.setJobFiredLastTime(new Date(jobMonitorInfo.getJobFiredLastTime()));
                                r.setJobMissfiredLastTime(new Date(jobMonitorInfo.getJobMissfireLastTime()));
                                r.setMissfireTimes(jobMonitorInfo.getMissfireTimes());
                                r.setJobVeto(jobMonitorInfo.isJobVeto());
                            }
                        });
                    }

                    saveHeartbeatInfo(taskMonitorInfo, result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
