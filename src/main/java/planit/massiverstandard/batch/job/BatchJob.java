package planit.massiverstandard.batch.job;

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
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import planit.massiverstandard.batch.job.listener.TempTableInitializer;
import planit.massiverstandard.columntransform.ColumnTransform;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.datasource.service.FindRealDataSource;
import planit.massiverstandard.datasource.util.DataSourceResolver;
import planit.massiverstandard.filter.entity.DateRangeFilter;
import planit.massiverstandard.filter.entity.Filter;
import planit.massiverstandard.filter.entity.SqlFilter;
import planit.massiverstandard.filter.entity.WhereFilter;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.service.FindUnit;
import planit.massiverstandard.unit.service.UnitGetService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchJob {

    private final FindUnit findUnit;
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;
    private final Step etlStep;
    private final Step mergeStep;
    private final FindRealDataSource findRealDataSource;

    private final ItemReader<Map<String, Object>> reader;
    private final ItemProcessor<Map<String, Object>, Map<String, Object>> processor;
    private final ItemWriter<Map<String, Object>> writer;

    public Job createJob(String jobName) {
        return new JobBuilder(jobName, jobRepository)
            .start(etlStep)
            .next(mergeStep)
            .incrementer(new RunIdIncrementer())
            .build();
    }

    @Bean
    @JobScope
    public Step etlStep(
        @Value("#{jobParameters['unitId']}") String unitId,
        JobRepository jobRepository
    ) {
        Unit unit = findUnit.byIdWithColumnTransform(UUID.fromString(unitId));
        javax.sql.DataSource dataSource = findRealDataSource.getOrCreateDataSource(unit.getSourceDb());
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String tmp = "tmp_pk_" + unit.getTargetTable() + "_" + unit.getId()
            .toString().replace("-", "_");

        boolean isOverWrite = unit.getColumnTransforms().stream()
            .anyMatch(ColumnTransform::isOverWrite);

        TempTableInitializer initializer =
            new TempTableInitializer(jdbcTemplate, unit);

        return new StepBuilder("STEP_" + unitId, jobRepository)
            .<Map<String, Object>, Map<String, Object>>chunk(50000, transactionManager)
            .listener(initializer)
            .reader(reader)  // ✅ @Bean으로 주입된 reader 사용
            .processor(processor)  // ✅ @Bean으로 주입된 processor 사용
            .writer(writer)  // ✅ @Bean으로 주입된 writer 사용
            .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<Map<String, Object>> reader(@Value("#{jobParameters['unitId']}") String unitId, UnitGetService unitGetService) {

        Unit unit = unitGetService.byId(UUID.fromString(unitId));
        javax.sql.DataSource dataSource = findRealDataSource.getOrCreateDataSource(unit.getSourceDb());

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

        javax.sql.DataSource targetDataSource = findRealDataSource.getOrCreateDataSource(unit.getTargetDb());

        if (isOverWrite) {
            return new InsertTempTableWriter(
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


    @Bean
    @JobScope
    public Step mergeStep(
        @Value("#{jobParameters['unitId']}") String unitId,
        JobRepository jr
    ) {
        Unit unit = findUnit.byIdWithColumnTransform(UUID.fromString(unitId));
        List<ColumnTransform> columnTransforms = unit.getColumnTransforms();
        boolean isOverWrite = columnTransforms.stream()
            .anyMatch(ColumnTransform::isOverWrite);
        if (isOverWrite) {
            return new StepBuilder("MERGE_" + unitId, jr)
                .tasklet((contribution, chunkContext) -> {
                    // 1) Unit·TempTable 정보 조회

                    NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(
                        findRealDataSource.getOrCreateDataSource(unit.getTargetDb())
                    );
                    // 2) Upsert SQL 생성 & 실행
                    String sql = buildPgUpsertSql2(unit);
                    log.info("Upsert SQL : {}", sql);

                    jdbc.getJdbcTemplate().execute(sql);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
        } else {
            // 넘기기
            return new StepBuilder("MERGE_" + unitId, jr).tasklet((contribution, chunkContext) -> {
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
        }
    }

    private String buildPgUpsertSql2(Unit unit) {
        List<String> allCols = unit.getColumnTransforms().stream()
            .map(ColumnTransform::getTargetColumn)
            .toList();
        List<String> keyCols = unit.getColumnTransforms().stream()
            .filter(ColumnTransform::isOverWrite)
            .map(ColumnTransform::getTargetColumn)
            .toList();

        String tmp = "tmp_pk_" + unit.getTargetTable() + "_"
            + unit.getId().toString().replace("-", "_");

        String allColumnList = String.join(", ", allCols);
        String allSelectList = allCols.stream()
            .map(c -> "s.\"" + c + "\"")
            .collect(Collectors.joining(", "));

        String joinCond = keyCols.stream()
            .map(k -> "t.\"" + k + "\" = s.\"" + k + "\"")
            .collect(Collectors.joining(" AND "));

        String setClause = allCols.stream()
            .filter(c -> !keyCols.contains(c))
            .map(c -> "\"" + c + "\" = s.\"" + c + "\"")
            .collect(Collectors.joining(", "));

        // ★ 변경: RETURNING에 t. 별칭 추가
        String returningCols = keyCols.stream()
            .map(c -> "t.\"" + c + "\"")
            .collect(Collectors.joining(", "));

        String notExistsCond = keyCols.stream()
            .map(k -> "u.\"" + k + "\" = s.\"" + k + "\"")
            .collect(Collectors.joining(" AND "));

        return String.format("""
        WITH updated AS (
          UPDATE %1$s.%2$s AS t
          SET    %3$s
          FROM   %4$s AS s
          WHERE  %5$s
         RETURNING %6$s
        )
        INSERT INTO public.%2$s (%7$s)
        SELECT %8$s
        FROM   %4$s AS s
        WHERE  NOT EXISTS (
          SELECT 1
          FROM   updated u
          WHERE  %9$s
        );
        """,
            unit.getTargetSchema(),   // %1$s
            unit.getTargetTable(),    // %2$s
            setClause,                // %3$s
            tmp,                      // %4$s
            joinCond,                 // %5$s
            returningCols,            // %6$s ★ t."id", t."id2", t."id3"
            allColumnList,            // %7$s
            allSelectList,            // %8$s
            notExistsCond             // %9$s
        );
    }

    private String buildMergeSql(Unit unit) {
        List<String> targetColumns = unit.getColumnTransforms().stream()
            .map(ColumnTransform::getTargetColumn)
            .toList();
        String columns = String.join(", ", targetColumns);
        String values = targetColumns.stream()
            .map(col -> "source." + col)
            .collect(Collectors.joining(", "));

        List<ColumnTransform> overWriteKeyColumns = unit.getColumnTransforms().stream()
            .filter(ColumnTransform::isOverWrite)
            .toList();

        String joinCondition = overWriteKeyColumns.stream()
            .map(col -> "target." + col.getTargetColumn() + " = source." + col.getTargetColumn())
            .collect(Collectors.joining(" AND "));

        String updateSetClause = targetColumns.stream()
            .filter(col -> !overWriteKeyColumns.stream().map(ColumnTransform::getTargetColumn).toList().contains(col))
            .map(col -> "target." + col + " = source." + col)
            .collect(Collectors.joining(", "));


        String insertColumns = String.join(", ", targetColumns);
        String insertValues = targetColumns.stream()
            .map(col -> "source." + col)
            .collect(Collectors.joining(", "));

        String getTempTableName = "tmp_pk_" + unit.getTargetTable() + "_" + unit.getId().toString().replace("-", "_");

        return String.format("""
            MERGE INTO %s.%s AS target
            USING %s AS source
            ON %s
            WHEN MATCHED THEN
                UPDATE SET %s
            WHEN NOT MATCHED THEN
                INSERT (%s) VALUES (%s)
            """, unit.getTargetSchema(), unit.getTargetTable(), getTempTableName, joinCondition, updateSetClause, insertColumns, insertValues);
    }

    private String buildPgUpsertSql(Unit unit) {
        List<String> cols = unit.getColumnTransforms().stream()
            .map(ColumnTransform::getTargetColumn)
            .toList();
        List<String> keyCols = unit.getColumnTransforms().stream()
            .filter(ColumnTransform::isOverWrite)
            .map(ColumnTransform::getTargetColumn)
            .toList();

        String columnList = String.join(", ", cols);
        String selectCols = cols.stream().map(c -> "source." + c).collect(Collectors.joining(", "));
        String conflictCols = String.join(", ", keyCols);
        String updateSet = cols.stream()
            .filter(c -> !keyCols.contains(c))
            .map(c -> c + " = EXCLUDED." + c)
            .collect(Collectors.joining(", "));

        String tmp = "tmp_pk_" + unit.getTargetTable() + "_" + unit.getId()
            .toString().replace("-", "_");

        return String.format("""
                INSERT INTO %s.%s (%s)
                SELECT %s
                FROM %s AS source
                ON CONFLICT (%s)
                DO UPDATE SET %s
                """,
            unit.getTargetSchema(),
            unit.getTargetTable(),
            columnList,
            selectCols,
            tmp,
            conflictCols,
            updateSet
        );
    }

//    private String buildUpsertSql(Unit unit) {
//        // 1) 컬럼 리스트: (id, id2, id3, col1, col2, date_col)
//        List<String> targetColumns = unit.getColumnTransforms().stream().map(ColumnTransform::getTargetColumn).toList();
//        String columns = String.join(", ", targetColumns);
//
//        // 2) VALUES 절의 소스 컬럼들: (source.id, source.id2, …)
//        String sourceValues = targetColumns.stream()
//            .map(col -> "source." + col)
//            .collect(Collectors.joining(", "));
//
//        // 3) 충돌 키(복합 PK) 조건: (id, id2, id3)
//        String conflictKeys = overWriteKeyColumns.stream()
//            .map(ColumnTransform::getTargetColumn)
//            .collect(Collectors.joining(", "));
//
//        // 4) UPDATE SET 절: 변경 대상 컬럼만
//        List<String> pkCols = overWriteKeyColumns.stream()
//            .map(ColumnTransform::getTargetColumn)
//            .toList();
//        String updateSet = targetColumns.stream()
//            .filter(col -> !pkCols.contains(col))            // PK 컬럼 제외
//            .map(col -> col + " = EXCLUDED." + col)           // EXCLUDED.<col> 사용
//            .collect(Collectors.joining(", "));
//
//        // 최종 UPSERT SQL 생성
//        return String.format("""
//                        INSERT INTO %s.%s (%s)
//                        SELECT %s
//                        FROM %s AS source
//                        ON CONFLICT (%s)
//                        DO UPDATE SET %s
//                        """,
//            targetSchema,
//            targetTable,
//            columns,
//            sourceValues,
//            getTempTableName(),
//            conflictKeys,
//            updateSet
//        );
//    }


}
