package planit.massiverstandard.batch.job.config.reader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import planit.massiverstandard.columntransform.entity.ColumnTransform;
import planit.massiverstandard.datasource.service.FindRealDataSource;
import planit.massiverstandard.filter.entity.DateRangeFilter;
import planit.massiverstandard.filter.entity.SqlFilter;
import planit.massiverstandard.filter.entity.WhereFilter;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.service.UnitGetService;

import javax.sql.DataSource;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.UUID;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchReaderConfig {

    private final FindRealDataSource findRealDataSource;

    @Bean
    @StepScope
    public JdbcCursorItemReader<Object[]> reader(
        @Value("#{jobParameters['unitId']}") String unitId,
        UnitGetService unitGetService,
        List<ColumnTransform> columnTransforms
    ) {

        Unit unit = unitGetService.byId(UUID.fromString(unitId));
        DataSource dataSource = findRealDataSource.getOrCreateDataSource(unit.getSourceDb());

        String sqlString = buildSelectSql(unit, columnTransforms);

        JdbcCursorItemReader<Object[]> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql(sqlString);
        reader.setFetchSize(1000);
        reader.setRowMapper((rs, rowNum) -> {
            ResultSetMetaData md = rs.getMetaData();
            int cnt = md.getColumnCount();
            Object[] arr = new Object[cnt];
            for (int i = 1; i <= cnt; i++) {
                arr[i - 1] = rs.getObject(i);
            }
            return arr;
        });

        log.info("[Reader-{}] SQL = {}", unitId, sqlString);
        return reader;
    }


    private String buildSelectSql(Unit unit, List<ColumnTransform> columnTransforms) {

        List<String> columnNames = columnTransforms.stream()
            .map(ColumnTransform::getSourceColumn)
            .toList();

        StringBuilder sql = new StringBuilder("SELECT " + String.join(", ", columnNames) +
            " FROM " + unit.getSourceSchema() + "." + unit.getSourceTable() +
            " WHERE 1=1");


        unit.getFilters().stream()
            .filter(filter -> filter instanceof DateRangeFilter | filter instanceof SqlFilter)
            .forEach(filter -> {
                WhereFilter whereFilter = (WhereFilter) filter;
                whereFilter.addCondition(sql);
            });

        return sql.toString();
    }

}
