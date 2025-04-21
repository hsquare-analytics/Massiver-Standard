package planit.massiverstandard.database;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import planit.massiverstandard.database.repository.DataSourceCacheRepository;

import javax.sql.DataSource;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataBaseQueryService {

    private final DataSourceCacheRepository dataSourceCacheRepository;

    public List<String> getSchemas(DataBase dataBase) {
        DataSource dataSource = dataSourceCacheRepository.getOrCreateDataSource(dataBase);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.queryForList("SELECT schema_name FROM information_schema.schemata", String.class);
    }

    public List<String> getTables(DataBase dataBase, String schema) {
        DataSource dataSource = dataSourceCacheRepository.getOrCreateDataSource(dataBase);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.queryForList("SELECT table_name FROM information_schema.tables WHERE table_schema = ?", String.class, schema);
    }

    public List<String> getColumns(DataBase dataBase, String schema, String table) {
        DataSource dataSource = dataSourceCacheRepository.getOrCreateDataSource(dataBase);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.queryForList("SELECT column_name FROM information_schema.columns WHERE table_schema = ? AND table_name = ?", String.class, schema, table);
    }
}
