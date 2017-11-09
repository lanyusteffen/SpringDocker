package stu.lanyu.springdocker.schedule;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import stu.lanyu.springdocker.business.readonly.JobMonitorInfoService;
import stu.lanyu.springdocker.business.readonly.TaskMonitorInfoService;
import stu.lanyu.springdocker.config.GlobalConfig;
import stu.lanyu.springdocker.contract.entity.HeartbeatInfo;
import stu.lanyu.springdocker.contract.entity.JobMonitorInfo;
import stu.lanyu.springdocker.domain.TaskMonitorInfo;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private JobMonitorInfoService jobMonitorInfoQueryService;

    @Scheduled(fixedDelay = 60000)
    public void checkHeartbeat() {

        Map<Object, Object> entries = redisTemplate.opsForHash().entries(GlobalConfig.Redis.REGISTER_HEARTBEAT_CACHE_KEY);

        OkHttpClient httpClient = new OkHttpClient();

        Gson gson = new Gson();

        for (Map.Entry<Object, Object> entry : entries.entrySet()
             ) {
            String url = entry.getValue().toString();
            String serviceIdentity = entry.getKey().toString();

            try {

                Request req = new Request.Builder()
                        .url(url).build();

                Response resp = httpClient.newCall(req).execute();
                HeartbeatInfo heartbeatInfo = gson.fromJson(resp.body().string(), HeartbeatInfo.class);

                TaskMonitorInfo taskMonitorInfo = taskMonitorInfoQueryService.getListPagedByServiceIdentity(serviceIdentity);

                if (taskMonitorInfo == null) {

                    taskMonitorInfo = new TaskMonitorInfo();
                    taskMonitorInfo.setJobs(new ArrayList<>());
                }

                if (resp.code() != 200) {

                    taskMonitorInfo.setHeartbeatBreak(true);
                }
                else {

                    taskMonitorInfo.setVeto(heartbeatInfo.isVetoForTask());
                    taskMonitorInfo.setHeartbeatBreak(false);

                    for (JobMonitorInfo monitorInfo : heartbeatInfo.getMonitorInfos()) {
                        stu.lanyu.springdocker.domain.JobMonitorInfo findedJobMonitorInfo = taskMonitorInfo.getJobs().stream()
                                .filter(j -> j.getJobName().equals(monitorInfo.getJobName()) && j.getJobGroup().equals(monitorInfo.getJobGroup()))
                                .findFirst()
                                .get();

                        if (findedJobMonitorInfo == null) {

                            findedJobMonitorInfo = new stu.lanyu.springdocker.domain.JobMonitorInfo();
                            findedJobMonitorInfo.setJobName(monitorInfo.getJobName());
                            findedJobMonitorInfo.setJobGroup(monitorInfo.getJobGroup());
                            findedJobMonitorInfo.setServiceIdentity(serviceIdentity);
                            taskMonitorInfo.getJobs().add(findedJobMonitorInfo);
                        }

                        taskMonitorInfo.getJobs().forEach(r -> {

                            r.setFiredTimes(monitorInfo.getFiredTimes());
                            r.setJobCompletedLastTime(new Date(monitorInfo.getJobCompletedLastTime()));
                            r.setJobFiredLastTime(new Date(monitorInfo.getJobFiredLastTime()));
                            r.setJobMissfiredLastTime(new Date(monitorInfo.getJobMissfireLastTime()));
                            r.setMissfireTimes(monitorInfo.getMissfireTimes());
                            r.setVeto(monitorInfo.isVeto());
                        });
                    }
                }

                taskMonitorInfoService.save(taskMonitorInfo);
            } catch (ConnectException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
