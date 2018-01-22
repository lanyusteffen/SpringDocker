package stu.lanyu.springdocker.controller;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import stu.lanyu.springdocker.annotation.Approve;
import stu.lanyu.springdocker.business.readonly.TaskMonitorInfoService;
import stu.lanyu.springdocker.config.GlobalConfig;
import stu.lanyu.springdocker.contract.entity.JobBreaker;
import stu.lanyu.springdocker.contract.entity.ServiceBreaker;
import stu.lanyu.springdocker.domain.TaskMonitorInfo;
import stu.lanyu.springdocker.utility.StringUtility;

import java.io.IOException;
import java.net.ConnectException;

@RestController
@RequestMapping(value = "/breaker")
public class BreakerController {

    @Qualifier(value = "TaskMonitorInfoServiceReadwrite")
    @Autowired(required = true)
    private stu.lanyu.springdocker.business.readwrite.TaskMonitorInfoService taskMonitorInfoService;

    @Qualifier(value = "TaskMonitorInfoServiceReadonly")
    @Autowired(required = true)
    private TaskMonitorInfoService taskMonitorInfoQueryService;

    private OkHttpClient httpClient = null;

    public BreakerController() {

        httpClient = new OkHttpClient();
    }

    private ServiceBreaker getServiceBreaker(TaskMonitorInfo taskMonitorInfo, boolean isForTask, boolean isVeto, String jobName, String jobGroup) {

        ServiceBreaker serviceBreaker = new ServiceBreaker();

        serviceBreaker.setActionToken(taskMonitorInfo.getActionToken());
        serviceBreaker.setAuthenticationFailure(false);
        serviceBreaker.setBreakerResult(false);
        serviceBreaker.setServiceIdentity(taskMonitorInfo.getServiceIdentity());

        if (isForTask) {
            serviceBreaker.setBreakerForTask(true);
            serviceBreaker.setTaskVeto(isVeto);
        }
        else {
            serviceBreaker.setBreakerForTask(false);

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
    private ServiceBreaker doBreakerService(TaskMonitorInfo taskMonitorInfo, boolean isForTask, boolean isVeto, String jobName, String jobGroup) {

        ServiceBreaker serviceBreaker = getServiceBreaker(taskMonitorInfo, isForTask, isVeto, jobName, jobGroup);

        try {

            Gson gson = new Gson();

            RequestBody body = RequestBody.create(GlobalConfig.WCFHost.JSON, gson.toJson(serviceBreaker));

            Request req = new Request.Builder()
                    .url(taskMonitorInfo.getBreakerUrl())
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

    private void saveTaskMonitorBreakerResult(String serviceIdentity, boolean isVeto) {

        TaskMonitorInfo taskMonitorInfo = taskMonitorInfoQueryService.getTaskMonitorInfoByServiceIdentity(serviceIdentity);
        taskMonitorInfo.setTaskVeto(isVeto);
        taskMonitorInfoService.save(taskMonitorInfo);
    }

    @Approve
    @RequestMapping(value ="/doForTask", method = RequestMethod.GET)
    public boolean doForTask(String serviceIdentity, boolean isVeto) {

        TaskMonitorInfo taskMonitorInfo = taskMonitorInfoQueryService.getTaskMonitorInfoByServiceIdentity(serviceIdentity);

        if (taskMonitorInfo != null) {
           ServiceBreaker serviceBreaker = doBreakerService(taskMonitorInfo, true, isVeto, null, null);

           boolean result = serviceBreaker.isBreakerResult();

           if (result) {
               saveTaskMonitorBreakerResult(serviceIdentity, isVeto);
           }

           return result;
        }

        return false;
    }

    private void setJobMonitorBreakerResult(String serviceIdentity, String jobName, String jobGroup, boolean isVeto) {

        TaskMonitorInfo taskMonitorInfo = taskMonitorInfoQueryService.getTaskMonitorInfoByServiceIdentity(serviceIdentity);

        taskMonitorInfo.getJobs().forEach(r -> {

            String eachJobName = (StringUtility.isNullOrEmpty(r.getJobName())
                    ? "" : r.getJobName());
            String eachJobGroup = (StringUtility.isNullOrEmpty(r.getJobGroup())
                    ? "" : r.getJobGroup());

            if (eachJobName.equals(jobName) && eachJobGroup.equals(jobGroup)) {

                r.setJobVeto(isVeto);
                // TODO break逻辑
            }
        });

        taskMonitorInfoService.save(taskMonitorInfo);
    }

    @Approve
    @RequestMapping(value = "/doForJob", method = RequestMethod.GET)
    public boolean doForJob(String serviceIdentity, String jobName, String jobGroup, boolean isVeto) {

        TaskMonitorInfo taskMonitorInfo = taskMonitorInfoQueryService.getTaskMonitorInfoByServiceIdentity(serviceIdentity);

        if (taskMonitorInfo != null) {

            ServiceBreaker serviceBreaker = doBreakerService(taskMonitorInfo, false, isVeto, jobName, jobGroup);
            boolean result = serviceBreaker.getJobBreakers()[0].isBreakerResult();

            if (result) {
                setJobMonitorBreakerResult(serviceIdentity, jobName, jobGroup, isVeto);
            }

            return result;
        }

        return false;
    }
}
