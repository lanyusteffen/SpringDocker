package stu.lanyu.springdocker.message.subscriber;

import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPubSub;
import stu.lanyu.springdocker.business.readonly.TaskMonitorInfoService;
import stu.lanyu.springdocker.domain.JobMonitorInfo;
import stu.lanyu.springdocker.domain.TaskMonitorInfo;
import stu.lanyu.springdocker.message.MessageProto;
import stu.lanyu.springdocker.utility.DateUtility;
import stu.lanyu.springdocker.utility.StringUtility;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MonitorSubscriber extends JedisPubSub {

    @Autowired
    private stu.lanyu.springdocker.business.readwrite.TaskMonitorInfoService taskMonitorInfoService;

    @Autowired
    private TaskMonitorInfoService taskMonitorInfoQueryService;

    @Autowired
    private stu.lanyu.springdocker.business.readwrite.JobMonitorInfoService jobMonitorInfoService;

    public void onMessage(String channel, String message) {

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

            List<TaskMonitorInfo> newTaskMonitorInfoList = proto.getMonitorTaskBatchList()
                .stream()
                .filter(n -> !existedServiceIdentityList.contains(n.getServiceIdentity()))
                .map(r -> {
                    TaskMonitorInfo taskMonitorInfo = new TaskMonitorInfo();

                    taskMonitorInfo.setLastHeartbeatTime(new Date(r.getLastHeartbeatTime()));
                    taskMonitorInfo.setBreakerUrl(r.getBreakerUrl());
                    taskMonitorInfo.setHeartbeatBreak(r.getIsHeartbeatBreak());
                    taskMonitorInfo.setTaskVeto(r.getTaskVeto());
                    taskMonitorInfo.setActionToken(r.getActionToken());
                    taskMonitorInfo.setRegisterTime(new Date(r.getRegisterTime()));

                    addJob(taskMonitorInfo, r);

                    return taskMonitorInfo;
                })
                .collect(Collectors.toList());

            for (int i = 0; i < taskMonitorInfoList.size(); i++) {
                TaskMonitorInfo taskMonitorInfo = taskMonitorInfoList.get(i);
                MessageProto.MonitorTaskProto monitorTaskProto = proto.getMonitorTaskBatchList()
                        .stream()
                        .filter(r -> r.getServiceIdentity() == taskMonitorInfo.getServiceIdentity())
                        .findFirst()
                        .orElse(null);

                taskMonitorInfo.setTaskVeto(monitorTaskProto.getTaskVeto());
                taskMonitorInfo.setHeartbeatBreak(monitorTaskProto.getIsHeartbeatBreak());
                taskMonitorInfo.setBreakerUrl(monitorTaskProto.getBreakerUrl());
                taskMonitorInfo.setLastHeartbeatTime(new Date(monitorTaskProto.getLastHeartbeatTime()));
                taskMonitorInfo.setActionToken(monitorTaskProto.getActionToken());
                taskMonitorInfo.setRegisterTime(new Date(monitorTaskProto.getRegisterTime()));

                updateJob(taskMonitorInfo, monitorTaskProto);
            }

            taskMonitorInfoService.saveBatch(newTaskMonitorInfoList);
            taskMonitorInfoService.saveBatch(taskMonitorInfoList);
            deleteUnusedJob(taskMonitorInfoList, proto);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addJob(TaskMonitorInfo taskMonitorInfo, MessageProto.MonitorTaskProto monitorTaskProto) {

        List<stu.lanyu.springdocker.domain.JobMonitorInfo> jobMonitorInfoList =
                new ArrayList<>();

        monitorTaskProto.getJobsList()
            .stream()
            .map(r -> {
                stu.lanyu.springdocker.domain.JobMonitorInfo jobMonitorInfo =
                        new stu.lanyu.springdocker.domain.JobMonitorInfo();

                jobMonitorInfo.setJobVeto(r.getJobVeto());
                jobMonitorInfo.setFiredTimes(r.getFiredTimes());
                jobMonitorInfo.setJobCompletedLastTime(new Date(r.getJobCompletedLastTime()));
                jobMonitorInfo.setJobName(r.getJobName());
                jobMonitorInfo.setJobGroup(r.getJogGroup());
                jobMonitorInfo.setJobFiredLastTime(new Date(r.getJobFiredLastTime()));
                jobMonitorInfo.setJobMissfiredLastTime(new Date(r.getJobMissfiredLastTime()));
                jobMonitorInfo.setMissfireTimes(r.getMissfireTimes());
                jobMonitorInfo.setServiceIdentity(taskMonitorInfo.getServiceIdentity());

                return jobMonitorInfo;
            })
            .collect(Collectors.toList());

        taskMonitorInfo.setJobs(jobMonitorInfoList);
    }

    private void updateJob(TaskMonitorInfo taskMonitorInfo, MessageProto.MonitorTaskProto monitorTaskProto) {

        List<stu.lanyu.springdocker.domain.JobMonitorInfo>
            newJobMonitorInfoList = monitorTaskProto.getJobsList()
            .stream()
            .filter(r -> taskMonitorInfo.getJobs().stream().filter(t -> {
                String existedJobName = t.getJobName();
                String existedJobGroup = t.getJobGroup();
                String jobName = r.getJobName();
                String jobGroup = r.getJogGroup();

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
            }).count() == 0)
            .map(n -> {
                stu.lanyu.springdocker.domain.JobMonitorInfo jobMonitorInfo =
                        new stu.lanyu.springdocker.domain.JobMonitorInfo();

                jobMonitorInfo.setFiredTimes(n.getFiredTimes());
                jobMonitorInfo.setJobCompletedLastTime(new Date(n.getJobCompletedLastTime()));
                jobMonitorInfo.setJobFiredLastTime(new Date(n.getJobFiredLastTime()));
                jobMonitorInfo.setJobGroup(n.getJogGroup());
                jobMonitorInfo.setJobName(n.getJobName());
                jobMonitorInfo.setJobMissfiredLastTime(new Date(n.getJobMissfiredLastTime()));
                jobMonitorInfo.setJobVeto(n.getJobVeto());
                jobMonitorInfo.setMissfireTimes(n.getMissfireTimes());
                jobMonitorInfo.setServiceIdentity(taskMonitorInfo.getServiceIdentity());

                return jobMonitorInfo;
            })
            .collect(Collectors.toList());

        jobMonitorInfoService.saveBatch(newJobMonitorInfoList);

        List<JobMonitorInfo> existedJobMonitorInfoListForTask =
                taskMonitorInfo.getJobs();

        for(int i = 0; i < existedJobMonitorInfoListForTask.size(); i++) {

            JobMonitorInfo jobMonitorInfo = existedJobMonitorInfoListForTask.get(i);

            stu.lanyu.springdocker.contract.entity.JobMonitorInfo updatedJobMonitorInfo =
                    monitorTaskProto.getJobsList()
                    .stream()
                    .filter(r -> {
                        String jobName = r.getJobName();
                        String jobGroup = r.getJogGroup();

                        String existedJobName = jobMonitorInfo.getJobName();
                        String existedJobGroup = jobMonitorInfo.getJobGroup();

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
                    }).orElse(null);

            if (updatedJobMonitorInfo == null) {
                continue;
            }

            jobMonitorInfo.setFiredTimes(updatedJobMonitorInfo.getFiredTimes());
            jobMonitorInfo.setJobCompletedLastTime(new Date(updatedJobMonitorInfo.getJobCompletedLastTime()));
            jobMonitorInfo.setJobFiredLastTime(new Date(updatedJobMonitorInfo.getJobFiredLastTime()));
            jobMonitorInfo.setJobGroup(updatedJobMonitorInfo.getJogGroup());
            jobMonitorInfo.setJobName(updatedJobMonitorInfo.getJobName());
            jobMonitorInfo.setJobMissfiredLastTime(new Date(updatedJobMonitorInfo.getJobMissfiredLastTime()));
            jobMonitorInfo.setJobVeto(updatedJobMonitorInfo.getJobVeto());
            jobMonitorInfo.setMissfireTimes(updatedJobMonitorInfo.getMissfireTimes());
        }
    }

    private void deleteUnusedJob(List<TaskMonitorInfo> taskMonitorInfoList, MessageProto.MonitorProto proto) {

        List<stu.lanyu.springdocker.domain.JobMonitorInfo> deletedJobList = new ArrayList<>();

        // TODO 获取删除调度作业元素

        if (deletedJobList.size() > 0) {

            for(int i = 0; i < deletedJobList.size(); i++) {

                stu.lanyu.springdocker.domain.JobMonitorInfo job = deletedJobList.get(i);

                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" +
                        "Delete job '" + (StringUtility.isNullOrEmpty(job.getJobGroup()) ? "" : job.getJobGroup() + "-") + job.getJobName() + "' in task '" + job.getServiceIdentity() + "'!");
            }

            jobMonitorInfoService.deleteBatch(deletedJobList);
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
