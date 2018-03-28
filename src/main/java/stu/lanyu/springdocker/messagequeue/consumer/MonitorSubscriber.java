package stu.lanyu.springdocker.messagequeue.consumer;

import com.google.protobuf.InvalidProtocolBufferException;
import io.lettuce.core.pubsub.RedisPubSubListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import stu.lanyu.springdocker.business.readonly.TaskMonitorInfoService;
import stu.lanyu.springdocker.domain.entity.JobMonitorInfo;
import stu.lanyu.springdocker.domain.entity.TaskMonitorInfo;
import stu.lanyu.springdocker.messagequeue.contract.MessageProto;
import stu.lanyu.springdocker.utility.DateUtility;
import stu.lanyu.springdocker.utility.StringUtility;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class MonitorSubscriber implements RedisPubSubListener<String, String> {

    @Autowired
    @Qualifier(value = "TaskMonitorInfoServiceReadwrite")
    private stu.lanyu.springdocker.business.readwrite.TaskMonitorInfoService taskMonitorInfoService;

    @Autowired
    @Qualifier(value = "TaskMonitorInfoServiceReadonly")
    private TaskMonitorInfoService taskMonitorInfoQueryService;

    @Autowired
    @Qualifier(value = "JobMonitorInfoServiceReadwrite")
    private stu.lanyu.springdocker.business.readwrite.JobMonitorInfoService jobMonitorInfoService;

    private void addJob(TaskMonitorInfo taskMonitorInfo, MessageProto.MonitorTaskProto monitorTaskProto) {

        List<JobMonitorInfo> jobMonitorInfoList = monitorTaskProto.getJobsList()
            .stream()
            .map(r -> {
                JobMonitorInfo jobMonitorInfo =
                        new JobMonitorInfo();

                jobMonitorInfo.setJobVeto(r.getJobVeto());
                jobMonitorInfo.setFiredTimes(r.getFiredTimes());
                jobMonitorInfo.setJobCompletedLastTime(DateUtility.getDate(r.getJobCompletedLastTime()));
                jobMonitorInfo.setJobName(r.getJobName());
                jobMonitorInfo.setJobGroup(r.getJogGroup());
                jobMonitorInfo.setJobFiredLastTime(DateUtility.getDate(r.getJobFiredLastTime()));
                jobMonitorInfo.setJobMissfiredLastTime(DateUtility.getDate(r.getJobMissfiredLastTime()));
                jobMonitorInfo.setMissfireTimes(r.getMissfireTimes());
                jobMonitorInfo.setServiceIdentity(taskMonitorInfo.getServiceIdentity());

                return jobMonitorInfo;
            })
            .collect(Collectors.toList());

        taskMonitorInfo.setJobs(jobMonitorInfoList);
    }

    private boolean compareInteractionJob(JobMonitorInfo jobMonitorInfo, MessageProto.MonitorJobProto monitorJobProto) {

        String existedJobName = jobMonitorInfo.getJobName();
        String existedJobGroup = jobMonitorInfo.getJobGroup();
        String jobName = monitorJobProto.getJobName();
        String jobGroup = monitorJobProto.getJogGroup();

        if (StringUtility.isNullOrEmpty(existedJobGroup)
                && StringUtility.isNullOrEmpty(jobGroup)) {
            return true;
        } else if (StringUtility.isNullOrEmpty(existedJobGroup) &&
                !StringUtility.isNullOrEmpty(jobGroup)) {
            return false;
        } else if (!StringUtility.isNullOrEmpty(existedJobGroup) &&
                StringUtility.isNullOrEmpty(jobGroup)) {
            return false;
        }
        else {
            return jobName.equals(existedJobName) && jobGroup.equals(existedJobGroup);
        }
    }

    private void updateTaskNewJob(TaskMonitorInfo taskMonitorInfo, MessageProto.MonitorTaskProto monitorTaskProto) {

        List<JobMonitorInfo> newJobMonitorInfoList = monitorTaskProto.getJobsList()
                .stream()
                .filter(r -> taskMonitorInfo.getJobs().stream().filter(t -> compareInteractionJob(t, r)).count() == 0)
                .map(n -> {
                    JobMonitorInfo jobMonitorInfo =
                            new JobMonitorInfo();

                    jobMonitorInfo.setFiredTimes(n.getFiredTimes());
                    jobMonitorInfo.setJobCompletedLastTime(DateUtility.getDate(n.getJobCompletedLastTime()));
                    jobMonitorInfo.setJobFiredLastTime(DateUtility.getDate(n.getJobFiredLastTime()));
                    jobMonitorInfo.setJobGroup(n.getJogGroup());
                    jobMonitorInfo.setJobName(n.getJobName());
                    jobMonitorInfo.setJobMissfiredLastTime(DateUtility.getDate(n.getJobMissfiredLastTime()));
                    jobMonitorInfo.setJobVeto(n.getJobVeto());
                    jobMonitorInfo.setMissfireTimes(n.getMissfireTimes());
                    jobMonitorInfo.setServiceIdentity(taskMonitorInfo.getServiceIdentity());

                    return jobMonitorInfo;
                })
                .collect(Collectors.toList());

        jobMonitorInfoService.saveBatch(newJobMonitorInfoList);
    }

    private void updateJob(TaskMonitorInfo taskMonitorInfo, MessageProto.MonitorTaskProto monitorTaskProto) {

        List<JobMonitorInfo> existedJobMonitorInfoListForTask =
                taskMonitorInfo.getJobs();

        for(int i = 0; i < existedJobMonitorInfoListForTask.size(); i++) {

            JobMonitorInfo jobMonitorInfo = existedJobMonitorInfoListForTask.get(i);

            MessageProto.MonitorJobProto updatedJobMonitorInfo =
                    monitorTaskProto.getJobsList()
                    .stream()
                    .filter(r -> compareInteractionJob(jobMonitorInfo, r))
                    .findFirst()
                    .orElse(null);

            if (updatedJobMonitorInfo == null) {
                continue;
            }

            jobMonitorInfo.setFiredTimes(updatedJobMonitorInfo.getFiredTimes());
            jobMonitorInfo.setJobCompletedLastTime(DateUtility.getDate(updatedJobMonitorInfo.getJobCompletedLastTime()));
            jobMonitorInfo.setJobFiredLastTime(DateUtility.getDate(updatedJobMonitorInfo.getJobFiredLastTime()));
            jobMonitorInfo.setJobGroup(updatedJobMonitorInfo.getJogGroup());
            jobMonitorInfo.setJobName(updatedJobMonitorInfo.getJobName());
            jobMonitorInfo.setJobMissfiredLastTime(DateUtility.getDate(updatedJobMonitorInfo.getJobMissfiredLastTime()));
            jobMonitorInfo.setJobVeto(updatedJobMonitorInfo.getJobVeto());
            jobMonitorInfo.setMissfireTimes(updatedJobMonitorInfo.getMissfireTimes());
        }
    }

    @Override
    public void message(String channel, String message) {

        MessageProto.MonitorProto proto = null;

        try {

            byte[] decodedData = Base64.getDecoder().decode(message);
            proto = MessageProto.MonitorProto.parseFrom(decodedData);

            List<String> serviceIdentityList = proto.getMonitorTaskBatchList()
                    .stream()
                    .map(r -> r.getServiceIdentity())
                    .collect(Collectors.toList());

            List<TaskMonitorInfo> taskMonitorInfoList = taskMonitorInfoQueryService
                    .getTaskMonitorInfoByServiceIdentityInRange(serviceIdentityList);

            List<String> existedServiceIdentityList = taskMonitorInfoList
                    .stream()
                    .map(r -> r.getServiceIdentity())
                    .collect(Collectors.toList());

            // Proto中任务ServiceIdentity再数据库中不存在的, 则为新增
            List<TaskMonitorInfo> newTaskMonitorInfoList = proto.getMonitorTaskBatchList()
                    .stream()
                    .filter(n -> !existedServiceIdentityList.contains(n.getServiceIdentity()))
                    .map(r -> {
                        TaskMonitorInfo taskMonitorInfo = new TaskMonitorInfo();

                        taskMonitorInfo.setLastHeartbeatTime(DateUtility.getDate(r.getLastHeartbeatTime()));
                        taskMonitorInfo.setBreakerUrl(r.getBreakerUrl());
                        taskMonitorInfo.setHeartbeatBreak(r.getIsHeartbeatBreak());
                        taskMonitorInfo.setTaskVeto(r.getTaskVeto());
                        taskMonitorInfo.setActionToken(r.getActionToken());
                        taskMonitorInfo.setServiceIdentity(r.getServiceIdentity());
                        taskMonitorInfo.setRegisterTime(DateUtility.getDate(r.getRegisterTime()));

                        // 添加Job
                        addJob(taskMonitorInfo, r);

                        return taskMonitorInfo;
                    })
                    .collect(Collectors.toList());

            for (int i = 0; i < taskMonitorInfoList.size(); i++) {

                TaskMonitorInfo taskMonitorInfo = taskMonitorInfoList.get(i);
                MessageProto.MonitorTaskProto monitorTaskProto = proto.getMonitorTaskBatchList()
                        .stream()
                        .filter(r -> r.getServiceIdentity().equals(taskMonitorInfo.getServiceIdentity()))
                        .findFirst()
                        .orElse(null);

                taskMonitorInfo.setTaskVeto(monitorTaskProto.getTaskVeto());
                taskMonitorInfo.setHeartbeatBreak(monitorTaskProto.getIsHeartbeatBreak());
                taskMonitorInfo.setBreakerUrl(monitorTaskProto.getBreakerUrl());
                taskMonitorInfo.setLastHeartbeatTime(DateUtility.getDate(monitorTaskProto.getLastHeartbeatTime()));
                taskMonitorInfo.setActionToken(monitorTaskProto.getActionToken());
                taskMonitorInfo.setRegisterTime(DateUtility.getDate(monitorTaskProto.getRegisterTime()));

                updateTaskNewJob(taskMonitorInfo, monitorTaskProto);
                updateJob(taskMonitorInfo, monitorTaskProto);
            }

            taskMonitorInfoService.saveBatch(newTaskMonitorInfoList);
            taskMonitorInfoService.saveBatch(taskMonitorInfoList);
        } catch (InvalidProtocolBufferException e) {
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
