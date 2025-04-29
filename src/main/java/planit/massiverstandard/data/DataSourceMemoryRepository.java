package planit.massiverstandard.data;

import org.springframework.context.annotation.Profile;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.datasource.repository.DataSourceRepository;

import java.util.*;

@Profile("test")
public class DataSourceMemoryRepository implements DataSourceRepository {

    private static final Map<UUID, DataSource> databaseMap = new HashMap<>();


    @Override
    public DataSource save(DataSource dataSource) {
        databaseMap.put(dataSource.getId(), dataSource);
        return dataSource;
    }

    @Override
    public Optional<DataSource> findById(UUID id) {
        return Optional.ofNullable(databaseMap.get(id));
    }

    @Override
    public List<DataSource> findAll() {
        return new ArrayList<>(databaseMap.values());
    }

    @Override
    public void deleteAll() {
        databaseMap.clear();
    }
}
