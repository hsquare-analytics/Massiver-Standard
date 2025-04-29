package planit.massiverstandard.data;

import org.springframework.data.jpa.repository.JpaRepository;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.datasource.repository.DataSourceRepository;

import java.util.UUID;

public interface DataSourceJpaRepository extends DataSourceRepository, JpaRepository<DataSource, UUID> {
}
