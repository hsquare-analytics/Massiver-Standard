package planit.massiverstandard.batch;

import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import planit.massiverstandard.batch.job.ScheduleJob;
import planit.massiverstandard.batch.usecase.ExecuteSchedule;
import planit.massiverstandard.batch.usecase.CheckSchedule;
import planit.massiverstandard.batch.usecase.ExecuteGroup;
import planit.massiverstandard.group.entity.Group;
import planit.massiverstandard.group.service.FindGroup;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatchService implements ExecuteSchedule, ExecuteGroup, CheckSchedule {

    private final Scheduler scheduler;
    private final FindGroup findGroup;
    private final BatchJobLauncher batchJobLauncher;

    @Override
    public void executeGroup(UUID groupId) {
        batchJobLauncher.runGroup(groupId);
    }

    @Override
    @Transactional
    public void active(UUID groupID) {
        Group group = findGroup.byId(groupID);

        if (group.getIsActive()) {
            deActive(groupID);
        }

        try {
            JobDetail jobDetail = JobBuilder.newJob(ScheduleJob.class)
                .withIdentity(String.valueOf(group.getId()))
                .usingJobData("groupId", String.valueOf(group.getId()))
                .build();

            Set<Trigger> triggers = group.getSchedules().stream()
                .map(schedule -> TriggerBuilder.newTrigger()
                    .forJob(jobDetail)
                    .withIdentity("trigger_" + group.getId() + "_" + schedule.getCron())
                    .withSchedule(CronScheduleBuilder.cronSchedule(schedule.getCron()))
                    .build())
                .collect(Collectors.toSet());

            scheduler.scheduleJob(jobDetail, triggers, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean deActive(UUID groupId) {
        try {
            JobKey jobKey = new JobKey(String.valueOf(groupId));
            return scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            throw new RuntimeException("Job 삭제 실패", e);
        }
    }

    @Override
    public List<? extends Trigger> check(UUID groupId) {
        try {
            JobKey jobKey = new JobKey(String.valueOf(groupId));

            if (!scheduler.checkExists(jobKey)) {
                throw new RuntimeException("해당 Job이 존재하지 않습니다.");
            }

            return scheduler.getTriggersOfJob(jobKey);
        } catch (SchedulerException e) {
            throw new RuntimeException("Trigger 조회 실패", e);
        }
    }

}
