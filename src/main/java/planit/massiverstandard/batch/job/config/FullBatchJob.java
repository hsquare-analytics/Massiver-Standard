package planit.massiverstandard.batch.job.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import planit.massiverstandard.batch.job.listener.StepFailureListener;
import planit.massiverstandard.datasource.service.FindRealDataSource;
import planit.massiverstandard.filter.entity.Filter;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.service.FindUnit;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FullBatchJob {

    private final FindUnit findUnit;
    private final FindRealDataSource findRealDataSource;

    private final JobRepository jobRepository;

    private final ItemReader<Object[]> reader;
    private final ItemProcessor<Object[], Object[]> fullLoadProcessor;
    private final ItemWriter<Object[]> writer;

    private final Step etlFullLoadStep;
    private final Step truncateStep;

    public Job createJob(Unit unit) {
        String jobName = "FULL_LOAD_" + unit.getName();

        return new JobBuilder(jobName, jobRepository)
            .start(truncateStep)
            .next(etlFullLoadStep)
            .incrementer(new RunIdIncrementer())
            .build();
    }

    @Bean
    @JobScope
    public Step etlFullLoadStep(
        @Value("#{jobParameters['unitId']}") String unitId,
        JobRepository jobRepository,
        PlatformTransactionManager etlTransactionManager,
        StepFailureListener stepFailureListener
    ) {

        return new StepBuilder("STEP_" + unitId, jobRepository)
            .<Object[], Object[]>chunk(1000, etlTransactionManager)
            .reader(reader)  // ✅ @Bean으로 주입된 reader 사용
            .processor(fullLoadProcessor)  // ✅ @Bean으로 주입된 processor 사용
            .writer(writer)  // ✅ @Bean으로 주입된 writer 사용
            .listener(stepFailureListener)
            .build();
    }

    @Bean
    @JobScope
    public Step truncateStep(
        @Value("#{jobParameters['unitId']}") String unitId,
        PlatformTransactionManager etlTransactionManager,
        StepFailureListener stepFailureListener
    ) {
        // 단위 정보 조회
        Unit unit = findUnit.byId(UUID.fromString(unitId));
        DataSource ds = findRealDataSource.getOrCreateDataSource(unit.getTargetDb());
        JdbcTemplate jdbc = new JdbcTemplate(ds);

        String sql = "TRUNCATE TABLE " + unit.getTargetSchema() + "." + unit.getTargetTable();
        log.info("[TRUNCATE] {}.{} 테이블 비우기 시작", unit.getTargetSchema(), unit.getTargetTable());

        return new StepBuilder("TRUNCATE_" + unit.getName(), jobRepository)
            .tasklet((contribution, chunkContext) -> {
                jdbc.execute(sql);
                return RepeatStatus.FINISHED;
            }, etlTransactionManager)
            .listener(stepFailureListener)
            .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Object[], Object[]> fullLoadProcessor(
        @Value("#{jobParameters['unitId']}") String unitId
    ) {
        Unit unit = findUnit.byId(UUID.fromString(unitId));
        List<Filter> filters = unit.getFilters();

        return item -> {

            for (Filter filter : filters) {
                item = filter.process(item);

                if (item == null) {
                    return null;
                }
            }

            return item;
        };

    }

}
