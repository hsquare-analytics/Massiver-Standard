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
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;
import planit.massiverstandard.batch.job.listener.StepFailureListener;
import planit.massiverstandard.batch.job.listener.TempTableInitializer;
import planit.massiverstandard.columntransform.entity.ColumnTransform;
import planit.massiverstandard.datasource.service.FindRealDataSource;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.service.FindUnit;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OverWriteBatchJob {

    private final FindUnit findUnit;
    private final JobRepository jobRepository;
    private final Step overWriteEtlStep;
    private final Step mergeStep;
    private final FindRealDataSource findRealDataSource;

    private final ItemReader<Object[]> reader;
    private final ItemProcessor<Object[], Object[]> processor;
    private final ItemWriter<Object[]> overWriteWriter;

    public Job createJob(Unit unit) {
        String jobName = "OVERWRITE_LOAD_" + unit.getName();
        return new JobBuilder(jobName, jobRepository)
            .start(overWriteEtlStep)
            .next(mergeStep)
            .incrementer(new RunIdIncrementer())
            .build();
    }

    @Bean
    @JobScope
    public Step overWriteEtlStep(
        @Value("#{jobParameters['unitId']}") String unitId,
        JobRepository jobRepository,
        PlatformTransactionManager etlTransactionManager,
        StepFailureListener stepFailureListener
    ) {

        SimpleStepBuilder<Object[], Object[]> chunk = new StepBuilder("STEP_" + unitId, jobRepository)
            .<Object[], Object[]>chunk(1000, etlTransactionManager)
            .reader(reader)  // ✅ @Bean으로 주입된 reader 사용
            .processor(processor)  // ✅ @Bean으로 주입된 processor 사용
            .writer(overWriteWriter)  // ✅ @Bean으로 주입된 writer 사용
            .listener(stepFailureListener);

        Unit unit = findUnit.byIdWithColumnTransform(UUID.fromString(unitId));

        // 덮어쓰기 적재일 경우 mergeStep 추가
        boolean isOverWrite = unit.getColumnTransforms().stream().anyMatch(ColumnTransform::isOverWrite);
        if (isOverWrite) {
            DataSource dataSource = findRealDataSource.getOrCreateDataSource(unit.getSourceDb());
            NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
            TempTableInitializer initializer = new TempTableInitializer(jdbcTemplate, unit);
            chunk.listener(initializer);
        }

        return chunk.build();
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

    @Bean
    @StepScope
    public ItemWriter<Object[]> overWriteWriter(@Value("#{jobParameters['unitId']}") String unitId,
                                       List<String> targetColumns
    ) {

        Unit unit = findUnit.byIdWithColumnTransform(UUID.fromString(unitId));
        boolean isOverWrite = unit.getColumnTransforms().stream()
            .anyMatch(ColumnTransform::isOverWrite);

        DataSource targetDataSource = findRealDataSource.getOrCreateDataSource(unit.getTargetDb());

//        if (isOverWrite) {
//            return new InsertTempTableWriter(
//                targetDataSource,
//                unit
//            );
//        }

        String targetSchema = unit.getTargetSchema();
        String targetTable = unit.getTargetTable();

        Map<String, String> columnMapping = new HashMap<>();
        // map으로 변환
        unit.getColumnTransforms().forEach(
            c -> columnMapping.put(c.getTargetColumn(), c.getSourceColumn())
        );

//        String sql
//            = "INSERT INTO " + targetSchema + "." + targetTable + " ("
//            + String.join(", ", columnMapping.keySet())
//            + ") VALUES ("
//            + String.join(", ", columnMapping.keySet().stream().map(k -> ":" + k).toList())
//            + ")";


        DataSource ds = findRealDataSource.getOrCreateDataSource(unit.getTargetDb());
        // INSERT SQL 생성
        String cols = String.join(", ", targetColumns);
        String vals = targetColumns.stream().map(c -> "?")
            .collect(Collectors.joining(", "));
        String sql = String.format("INSERT INTO %s.%s (%s) VALUES (%s)",
            unit.getTargetSchema(), unit.getTargetTable(), cols, vals);
        return new JdbcBatchItemWriterBuilder<Object[]>()
            .dataSource(ds)
            .sql(sql)
//            .itemSqlParameterSourceProvider(this::toSqlParameterSource)
            .itemPreparedStatementSetter((item, ps) -> {
                for (int i = 0; i < item.length; i++) {
                    ps.setObject(i + 1, item[i]);
                }
            })
            .build();
    }

    private SqlParameterSource toSqlParameterSource(Map<String, Object> item) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        item.forEach(parameterSource::addValue);
        return parameterSource;
    }


    @Bean
    @JobScope
    public Step mergeStep(
        @Value("#{jobParameters['unitId']}") String unitId,
        JobRepository jr,
        PlatformTransactionManager etlTransactionManager
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
                }, etlTransactionManager)
                .build();
        } else {
            // 넘기기
            return new StepBuilder("MERGE_" + unitId, jr).tasklet((contribution, chunkContext) -> {
                    return RepeatStatus.FINISHED;
                }, etlTransactionManager)
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
