package planit.massiverstandard.batch.job;

import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import planit.massiverstandard.batch.BatchJobLauncher;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ScheduleJob implements Job {

    private final BatchJobLauncher batchJobLauncher;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String groupId = jobExecutionContext.getJobDetail().getJobDataMap().getString("groupId");
        batchJobLauncher.runGroup(UUID.fromString(groupId));
    }

}
