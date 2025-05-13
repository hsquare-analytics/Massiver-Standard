package planit.massiverstandard.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import planit.massiverstandard.batch.job.ScheduleJob;
import planit.massiverstandard.batch.usecase.ExecuteSchedule;
import planit.massiverstandard.batch.usecase.CheckSchedule;
import planit.massiverstandard.batch.usecase.ExecuteGroup;
import planit.massiverstandard.batch.usecase.ExecuteUnit;
import planit.massiverstandard.batch.vo.FlattenResult;
import planit.massiverstandard.group.entity.Group;
import planit.massiverstandard.group.service.FindGroup;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService implements ExecuteUnit, ExecuteSchedule, ExecuteGroup, CheckSchedule {

    private final Scheduler scheduler;
    private final FindGroup findGroup;
    private final BatchJobLauncher batchJobLauncher;

    /**
     * 기존 동기 runBatchJob을 호출하는 비동기 래퍼
     */
    @Override
    @Async
    public CompletableFuture<Void> asyncUnit(UUID unitId) {
        try {
            batchJobLauncher.runBatchJob(unitId);   // 실제 Job 실행
        } catch (Exception e) {
            log.error("[Async] Job 실행 중 오류", e);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void asyncGroup(UUID groupId) {
        try {
            CompletableFuture.runAsync(() -> executeGroup(groupId));
        } catch (Exception e) {
            throw new RuntimeException("Job 실행 실패", e);
        }
    }

    @Override
    public void executeGroup(UUID groupId) {
        FlattenResult flattenResult = batchJobLauncher.runGroup(groupId);
        batchJobLauncher.runFlattenedGraph(flattenResult.unitGraph(), flattenResult.unitInDegree());
    }

    @Override
    @Transactional("transactionManager")
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
