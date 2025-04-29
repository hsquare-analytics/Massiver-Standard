package planit.massiverstandard.datasource.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import planit.massiverstandard.datasource.dto.response.ColumnInfoResult;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.datasource.repository.DataSourceCacheRepository;
import planit.massiverstandard.datasource.util.DataSourceQueryResolver;
import planit.massiverstandard.datasource.util.DataSourceResolver;
import planit.massiverstandard.datasource.util.sql.SelectSqlFactory;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SqlExecuteService implements ExecuteSqlScript {

    private final DataSourceCacheRepository dataSourceCacheRepository;

    private List<String> executeList(DataSource dataSource, String sqlScript) {
        try {
            javax.sql.DataSource ds = dataSourceCacheRepository.getOrCreateDataSource(dataSource);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
            return jdbcTemplate.queryForList(sqlScript, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Error executing SQL script", e);
        }
    }

    @Override
    public List<String> getPkList(DataSource dataSource, String schema, String table) {
        SelectSqlFactory selectSqlFactory = DataSourceQueryResolver.getSelectSqlFactory(dataSource.getType());
        String sqlScript = selectSqlFactory.getPkList(schema, table);
        return executeList(dataSource, sqlScript);
    }

    @Override
    public List<String> getSchemas(DataSource dataSource) {
        SelectSqlFactory selectSqlFactory = DataSourceQueryResolver.getSelectSqlFactory(dataSource.getType());
        String sqlScript = selectSqlFactory.getSchemaList();
        return executeList(dataSource, sqlScript);
    }

    public List<String> getTables(DataSource dataSource, String schema) {
        SelectSqlFactory selectSqlFactory = DataSourceQueryResolver.getSelectSqlFactory(dataSource.getType());
        String sqlScript = selectSqlFactory.getTableList(schema);
        return executeList(dataSource, sqlScript);
    }

    public List<ColumnInfoResult> getColumns(DataSource dataSource, String schema, String table) {
        String sql = DataSourceQueryResolver
            .getSelectSqlFactory(dataSource.getType())
            .getColumnList(schema, table);

        JdbcTemplate jdbcTemplate =
            new JdbcTemplate(dataSourceCacheRepository.getOrCreateDataSource(dataSource));

        RowMapper<ColumnInfoResult> mapper = (rs, rowNum) -> new ColumnInfoResult(
            rs.getString("column_name"),
            rs.getInt("ordinal_position"),
            rs.getString("data_type"),
            rs.getInt("character_maximum_length"),
            rs.getInt("numeric_precision"),
            rs.getInt("numeric_scale"),
            rs.getString("is_nullable"),
            rs.getString("column_default"),
            rs.getBoolean("is_primary_key")
        );

        return jdbcTemplate.query(sql, mapper);
    }
}
