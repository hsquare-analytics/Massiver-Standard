package planit.massiverstandard.batch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.stereotype.Component;
import planit.massiverstandard.batch.job.config.AppendBatchJob;
import planit.massiverstandard.batch.job.config.FullBatchJob;
import planit.massiverstandard.batch.job.config.OverWriteBatchJob;
import planit.massiverstandard.unit.entity.Unit;

@Component
@RequiredArgsConstructor
public class BatchJobResolver {

    private final FullBatchJob fullBatchJob;
    private final AppendBatchJob appendBatchJob;
    private final OverWriteBatchJob overWriteBatchJob;

    public Job resolveJob(Unit unit) {
        return switch (unit.getLoadStrategy()) {
            case FULL -> fullBatchJob.createJob(unit);
            case APPEND -> appendBatchJob.createJob(unit);
            case OVERWRITE -> overWriteBatchJob.createJob(unit);
            default -> throw new IllegalArgumentException("Unknown load strategy: " + unit.getLoadStrategy());
        };
    }

}
