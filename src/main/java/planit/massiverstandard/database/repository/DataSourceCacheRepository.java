package planit.massiverstandard.database.repository;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Repository;
import planit.massiverstandard.database.DataBase;

import javax.sql.DataSource;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class DataSourceCacheRepository {

    private final Map<UUID, HikariDataSource> dataSourceCache = new ConcurrentHashMap<>();

    public DataSource getOrCreateDataSource(DataBase db) {
        return dataSourceCache.computeIfAbsent(db.getId(), id -> {
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl("jdbc:" + db.getType() + "://" + db.getHost() + "/" + db.getName());
            ds.setUsername(db.getUsername());
            ds.setPassword(db.getPassword());
            ds.setMaximumPoolSize(5); // 적절히 조절
            return ds;
        });
    }

}

