package planit.massiverstandard.batch.job.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import planit.massiverstandard.batch.job.listener.StepFailureListener;
import planit.massiverstandard.datasource.service.FindRealDataSource;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.service.FindUnit;

import java.util.UUID;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AppendBatchJob {

    private final FindUnit findUnit;
    private final JobRepository jobRepository;
    private final Step appendEtlStep;
    private final FindRealDataSource findRealDataSource;

    private final ItemReader<Object[]> reader;
    private final ItemProcessor<Object[], Object[]> processor;
    private final ItemWriter<Object[]> writer;

    public Job createJob(Unit unit) {
        String jobName = "APPEND_LOAD_" + unit.getName();

        return new JobBuilder(jobName, jobRepository)
            .start(appendEtlStep)
            .incrementer(new RunIdIncrementer())
            .build();
    }

    @Bean
    @JobScope
    public Step appendEtlStep(
        @Value("#{jobParameters['unitId']}") String unitId,
        JobRepository jobRepository,
        PlatformTransactionManager etlTransactionManager,
        StepFailureListener stepFailureListener
    ) {
        Unit unit = findUnit.byIdWithColumnTransform(UUID.fromString(unitId));

        return new StepBuilder("APPEND_LOAD_STEP_" + unit.getName(), jobRepository)
            .<Object[], Object[]>chunk(1000, etlTransactionManager)
            .reader(reader)  // ✅ @Bean으로 주입된 reader 사용
            .processor(processor)  // ✅ @Bean으로 주입된 processor 사용
            .writer(writer)  // ✅ @Bean으로 주입된 writer 사용
            .listener(stepFailureListener).build();
    }

//    @Bean
//    @StepScope
//    public ItemProcessor<Object[], Object[]> processor(
//        @Value("#{jobParameters['unitId']}") String unitId
//    ) {
//        Unit unit = findUnit.byId(UUID.fromString(unitId));
//        List<Filter> filters = unit.getFilters();
//
//        return item -> {
//
//            for (Filter filter : filters) {
//                item = filter.process(item);
//
//                if (item == null) {
//                    return null;
//                }
//            }
//
//            return item;
//        };
//
//    }

//    @Bean
//    @StepScope
//    public ItemWriter<Object[]> writer(@Value("#{jobParameters['unitId']}") String unitId,
//                                       List<String> targetColumns
//    ) {
//
//        Unit unit = findUnit.byIdWithColumnTransform(UUID.fromString(unitId));
//
//        Map<String, String> columnMapping = new HashMap<>();
//        // map으로 변환
//        unit.getColumnTransforms().forEach(
//            c -> columnMapping.put(c.getTargetColumn(), c.getSourceColumn())
//        );
//
//        DataSource ds = findRealDataSource.getOrCreateDataSource(unit.getTargetDb());
//        // INSERT SQL 생성
//        String cols = String.join(", ", targetColumns);
//        String vals = targetColumns.stream().map(c -> "?")
//            .collect(Collectors.joining(", "));
//        String sql = String.format("INSERT INTO %s.%s (%s) VALUES (%s)",
//            unit.getTargetSchema(), unit.getTargetTable(), cols, vals);
//        return new JdbcBatchItemWriterBuilder<Object[]>()
//            .dataSource(ds)
//            .sql(sql)
////            .itemSqlParameterSourceProvider(this::toSqlParameterSource)
//            .itemPreparedStatementSetter((item, ps) -> {
//                for (int i = 0; i < item.length; i++) {
//                    ps.setObject(i + 1, item[i]);
//                }
//            })
//            .build();
//    }

}
