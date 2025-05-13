package planit.massiverstandard.batch.job.config.writer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
public class BatchWriterConfig {

    private final FindUnit findUnit;
    private final FindRealDataSource findRealDataSource;

    @Bean
    @StepScope
    public ItemWriter<Object[]> writer(@Value("#{jobParameters['unitId']}") String unitId,
                                               List<String> targetColumns
    ) {

        Unit unit = findUnit.byIdWithColumnTransform(UUID.fromString(unitId));

        Map<String, String> columnMapping = new HashMap<>();
        // map으로 변환
        unit.getColumnTransforms().forEach(
            c -> columnMapping.put(c.getTargetColumn(), c.getSourceColumn())
        );

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

}
