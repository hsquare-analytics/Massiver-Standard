package planit.massiverstandard.unit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import planit.massiverstandard.unit.entity.Unit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UnitJpaRepository extends UnitRepository, JpaRepository<Unit, UUID> {

    @Query("""
            SELECT u
            FROM Unit u
            LEFT JOIN FETCH u.filters
            WHERE u.id = :id
            """)
    Optional<Unit> findWithFiltersById(@Param("id") UUID id);

    @Query("""
            SELECT u
            FROM Unit u
            LEFT JOIN FETCH u.columnTransforms
            WHERE u.id = :id
            """)
    Optional<Unit> findWithColumnTransformById(@Param("id") UUID id);

    @Query("""
            SELECT u
            FROM Unit u
            LEFT JOIN FETCH u.procedureParameters
            WHERE u.id = :id
            """)
    Optional<Unit> findWithProcedureParameterById(UUID id);

    @Query("""
        SELECT u
        FROM Unit u
        WHERE u.sourceDb.id = :datasourceId
           OR u.targetDb.id = :datasourceId
        """)
    List<Unit> findUnitBySourceOrTargetDb(UUID datasourceId);
}
