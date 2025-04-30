package planit.massiverstandard.batch.job;

import lombok.RequiredArgsConstructor;
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
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import planit.massiverstandard.columntransform.ColumnTransform;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.datasource.util.DataSourceResolver;
import planit.massiverstandard.filter.entity.DateRangeFilter;
import planit.massiverstandard.filter.entity.Filter;
import planit.massiverstandard.filter.entity.SqlFilter;
import planit.massiverstandard.filter.entity.WhereFilter;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.service.FindUnit;
import planit.massiverstandard.unit.service.UnitGetService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class BatchJob {

    private final FindUnit findUnit;
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;
    private final Step etlStep;

    private final ItemReader<Map<String, Object>> reader;
    private final ItemProcessor<Map<String, Object>, Map<String, Object>> processor;
    private final ItemWriter<Map<String, Object>> writer;

    public Job createJob(String jobName) {
        return new JobBuilder(jobName, jobRepository)
            .start(etlStep)
            .incrementer(new RunIdIncrementer())
            .build();
    }

    @Bean
    @JobScope
    public Step etlStep(
        @Value("#{jobParameters['unitId']}") String unitId,
        JobRepository jobRepository
    ) {
        return new StepBuilder("STEP_" + unitId, jobRepository)
            .<Map<String, Object>, Map<String, Object>>chunk(50000, transactionManager)
            .reader(reader)  // ✅ @Bean으로 주입된 reader 사용
            .processor(processor)  // ✅ @Bean으로 주입된 processor 사용
            .writer(writer)  // ✅ @Bean으로 주입된 writer 사용
            .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<Map<String, Object>> reader(@Value("#{jobParameters['unitId']}") String unitId, UnitGetService unitGetService) {

        Unit unit = unitGetService.byId(UUID.fromString(unitId));
        javax.sql.DataSource dataSource = DataSourceResolver.createDataSource(unit.getSourceDb());

        JdbcCursorItemReader<Map<String, Object>> reader = new JdbcCursorItemReader<>();

        StringBuilder sql = new StringBuilder("""
            SELECT *
            FROM """ + " " + unit.getSourceSchema() + "." + unit.getSourceTable() + " " + """
            WHERE 1=1
            """);

        unit.getFilters().stream()
            .filter(filter -> filter instanceof DateRangeFilter | filter instanceof SqlFilter)
            .forEach(filter -> {
                WhereFilter whereFilter = (WhereFilter) filter;
                whereFilter.addCondition(sql);
            });

        reader.setDataSource(dataSource);
        reader.setSql(sql.toString());
        reader.setRowMapper(new ColumnMapRowMapper());
        return reader;
    }

    @Bean
    @StepScope
    public ItemProcessor<Map<String, Object>, Map<String, Object>> processor(
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

//    @Bean
//    @StepScope
//    public JdbcBatchItemWriter<Map<String, Object>> writer(@Value("#{jobParameters['unitId']}") String unitId) {
//
//        Unit unit = findUnit.byId(UUID.fromString(unitId));
//        DataSource targetDs = unit.getTargetDb();
//        javax.sql.DataSource targetDataSource = DataSourceResolver.createDataSource(targetDs);
//        String targetSchema = unit.getTargetSchema();
//        String targetTable = unit.getTargetTable();
//
//        Map<String, String> columnMapping = new HashMap<>();
//        // map으로 변환
//        unit.getColumnTransforms().forEach(
//            c -> columnMapping.put(c.getTargetColumn(), c.getSourceColumn())
//        );
//
//        String sql
//            = "INSERT INTO " + targetSchema + "." + targetTable + " ("
//            + String.join(", ", columnMapping.keySet())
//            + ") VALUES ("
//            + String.join(", ", columnMapping.keySet().stream().map(k -> ":" + k).toList())
//            + ")";
//
//        return new JdbcBatchItemWriterBuilder<Map<String, Object>>()
//            .dataSource(targetDataSource)
//            .sql(sql)
//            .itemSqlParameterSourceProvider(
//                (Map<String, Object> item) -> {
//                    MapSqlParameterSource parameterSource = new MapSqlParameterSource();
//                    columnMapping.forEach(
//                        (targetColumn, sourceColumn) -> parameterSource.addValue(targetColumn, item.get(sourceColumn))
//                    );
//                    return parameterSource;
//                }
//            )
//            .build();
//    }

    @Bean
    @StepScope
    public ItemWriter<Map<String, Object>> writer(@Value("#{jobParameters['unitId']}") String unitId) {

        Unit unit = findUnit.byId(UUID.fromString(unitId));
        boolean isOverWrite = unit.getColumnTransforms().stream()
            .anyMatch(ColumnTransform::isOverWrite);

        DataSource targetDs = unit.getTargetDb();
        javax.sql.DataSource targetDataSource = DataSourceResolver.createDataSource(targetDs);

        if (isOverWrite) {
            return new DeleteInsertItemWriter(
                targetDataSource,
                unit
            );
        }


        String targetSchema = unit.getTargetSchema();
        String targetTable = unit.getTargetTable();

        Map<String, String> columnMapping = new HashMap<>();
        // map으로 변환
        unit.getColumnTransforms().forEach(
            c -> columnMapping.put(c.getTargetColumn(), c.getSourceColumn())
        );

        String sql
            = "INSERT INTO " + targetSchema + "." + targetTable + " ("
            + String.join(", ", columnMapping.keySet())
            + ") VALUES ("
            + String.join(", ", columnMapping.keySet().stream().map(k -> ":" + k).toList())
            + ")";

        return new JdbcBatchItemWriterBuilder<Map<String, Object>>()
            .dataSource(targetDataSource)
            .sql(sql)
            .itemSqlParameterSourceProvider(
                (Map<String, Object> item) -> {
                    MapSqlParameterSource parameterSource = new MapSqlParameterSource();
                    columnMapping.forEach(
                        (targetColumn, sourceColumn) -> parameterSource.addValue(targetColumn, item.get(sourceColumn))
                    );
                    return parameterSource;
                }
            )
            .build();
    }

}
