package planit.massiverstandard.batch.job.listener;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import planit.massiverstandard.log.entity.BatchErrorLog;
import planit.massiverstandard.log.repository.BatchErrorLogRepository;

import java.util.UUID;

@Component
@StepScope
public class StepFailureListener implements StepExecutionListener {

    @Value("#{jobParameters['unitId']}")
    private String unitId;
    @Value("#{stepExecution.stepName}")
    private String stepName;
    private final BatchErrorLogRepository repo;

    public StepFailureListener(BatchErrorLogRepository repo) {
        this.repo = repo;
    }

    @Override
    public void beforeStep(StepExecution se) {
    }

    @Override
    public ExitStatus afterStep(StepExecution se) {
        if (se.getStatus() == BatchStatus.FAILED) {
            Throwable t = se.getFailureExceptions().getFirst();
            String fullStack = ExceptionUtils.getStackTrace(t);

            BatchErrorLog errorLog = BatchErrorLog.builder()
                .unitId(UUID.fromString(unitId))
                .groupId(null) // todo: groupId은 JobParametersExtractor에서 가져와야 함
                .stepName(stepName)
                .exceptionClass(t.getClass().getName())
                .exceptionMessage(t.getMessage())
                .exceptionStackTrace(fullStack)
                .build();

            repo.save(errorLog);
        }
        return se.getExitStatus();
    }
}
