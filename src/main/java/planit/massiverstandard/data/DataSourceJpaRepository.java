package planit.massiverstandard.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.datasource.repository.DataSourceRepository;
import planit.massiverstandard.unit.entity.Unit;

import java.util.List;
import java.util.UUID;

public interface DataSourceJpaRepository extends DataSourceRepository, JpaRepository<DataSource, UUID> {

    @Query("""
        SELECT u
        FROM Unit u
        WHERE u.sourceDb.id = :datasourceId
           OR u.targetDb.id = :datasourceId
        """)
    List<Unit> findUnitBySourceOrTargetDb(UUID datasourceId);
}
