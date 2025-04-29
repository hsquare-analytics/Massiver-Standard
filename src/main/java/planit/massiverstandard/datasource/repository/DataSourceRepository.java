package planit.massiverstandard.datasource.repository;

import planit.massiverstandard.datasource.entity.DataSource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DataSourceRepository {

    DataSource save(DataSource dataSource);

    Optional<DataSource> findById(UUID id);

    List<DataSource> findAll();

    void deleteAll();
}
