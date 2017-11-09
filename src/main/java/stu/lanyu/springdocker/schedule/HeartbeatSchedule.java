package stu.lanyu.springdocker.schedule;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import stu.lanyu.springdocker.business.readonly.TaskMonitorInfoService;
import stu.lanyu.springdocker.config.GlobalConfig;
import stu.lanyu.springdocker.contract.entity.HeartbeatInfo;
import stu.lanyu.springdocker.contract.entity.JobMonitorInfo;
import stu.lanyu.springdocker.domain.TaskMonitorInfo;
import stu.lanyu.springdocker.utility.StringUtility;

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

                    for (JobMonitorInfo jobMonitorInfo : heartbeatInfo.getMonitorInfos()) {

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
                            taskMonitorInfo.getJobs().add(findedJobMonitorInfo);
                        }

                        taskMonitorInfo.getJobs().forEach(r -> {

                            if (r.getJobName() == jobMonitorInfo.getJobName() && r.getJobGroup() == jobMonitorInfo.getJobGroup()) {

                                r.setFiredTimes(jobMonitorInfo.getFiredTimes());
                                r.setJobCompletedLastTime(new Date(jobMonitorInfo.getJobCompletedLastTime()));
                                r.setJobFiredLastTime(new Date(jobMonitorInfo.getJobFiredLastTime()));
                                r.setJobMissfiredLastTime(new Date(jobMonitorInfo.getJobMissfireLastTime()));
                                r.setMissfireTimes(jobMonitorInfo.getMissfireTimes());
                                r.setVeto(jobMonitorInfo.isVeto());
                            }
                        });
                    }
                }

                taskMonitorInfoService.save(taskMonitorInfo);

                List<stu.lanyu.springdocker.domain.JobMonitorInfo> deletedJobList = new ArrayList<>();

                taskMonitorInfo.getJobs().stream().forEach(r -> {

                    for (JobMonitorInfo jobMonitorInfo : heartbeatInfo.getMonitorInfos()
                         ) {

                        String currentJobName = (StringUtility.isNullOrEmpty(jobMonitorInfo.getJobName())
                                ? "" : jobMonitorInfo.getJobName());
                        String currentJobGroup = (StringUtility.isNullOrEmpty(jobMonitorInfo.getJobGroup())
                                ? "" : jobMonitorInfo.getJobGroup());

                        if (currentJobName.equals(r.getJobName()) && currentJobGroup.equals(r.getJobGroup())) {

                            deletedJobList.add(r);
                        }
                    }
                });

                jobMonitorInfoService.delete(deletedJobList);
            } catch (ConnectException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
