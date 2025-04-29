package planit.massiverstandard.datasource.repository;

import org.springframework.stereotype.Repository;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.datasource.util.DataSourceResolver;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class DataSourceCacheRepository {

    private final Map<UUID, javax.sql.DataSource> dataSourceCache = new ConcurrentHashMap<>();

    public javax.sql.DataSource getOrCreateDataSource(DataSource dataSource) {
        return dataSourceCache.computeIfAbsent(dataSource.getId(), id -> {
            return DataSourceResolver.createDataSource(dataSource);
        });
    }

    public void updateDataSource(DataSource dataSource) {
        //todo: 비밀번호, 접속정보 바뀌었을 때 호출시켜줘야함
        dataSourceCache.put(dataSource.getId(), DataSourceResolver.createDataSource(dataSource));
    }

}

