package planit.massiverstandard.batch.job.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import planit.massiverstandard.log.entity.BatchErrorLog;
import planit.massiverstandard.log.repository.BatchErrorLogRepository;

import java.util.UUID;

@Slf4j
@Component
@StepScope
public class BatchErrorRecordingListener implements ItemReadListener<Object[]>, ItemProcessListener<Object[], Object[]>, ItemWriteListener<Object[]> {


    @Value("#{jobParameters['unitId']}")
    private String unitId;

    @Value("#{stepExecution.stepName}")
    private String stepName;

    private final BatchErrorLogRepository repo;

    public BatchErrorRecordingListener(BatchErrorLogRepository repo) {
        this.repo = repo;
    }

    // 읽기 중 에러
    @Override
    public void onReadError(Exception ex) {
        repo.save(buildLog(null, ex));
        log.error("[repo!!!!!]onReadError", ex);
    }

    // 처리 중 에러
    @Override
    public void onProcessError(Object[] item, Exception ex) {
        repo.save(buildLog(item, ex));
        log.error("[repo!!!!!]onProcessError", ex);
    }

    @Override
    public void onWriteError(Exception exception, Chunk<? extends Object[]> items) {
        repo.save(buildLog(null, exception));
    }

    private BatchErrorLog buildLog(Object[] item, Exception ex) {

        String fullStack = ExceptionUtils.getStackTrace(ex);

        return BatchErrorLog.builder()
            .unitId(UUID.fromString(unitId))    // JobParameters 에서 unitId
            // todo: groupId은 JobParametersExtractor에서 가져와야 함
            .groupId(null)
            .stepName(stepName) // StepExecution 에서 stepName
            .exceptionClass(ex.getClass().getName())
            .exceptionMessage(ex.getMessage())
            .exceptionStackTrace(fullStack)
            .build();
    }
}
