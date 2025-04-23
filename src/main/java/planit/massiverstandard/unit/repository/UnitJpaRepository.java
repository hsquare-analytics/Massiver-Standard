package planit.massiverstandard.unit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import planit.massiverstandard.unit.entity.Unit;

import java.util.Optional;
import java.util.UUID;

public interface UnitJpaRepository extends JpaRepository<Unit, UUID> {

    @Query("""
            SELECT u
            FROM Unit u
            LEFT JOIN FETCH u.filters
            WHERE u.id = :id
            """)
    Optional<Unit> findWithFiltersById(@Param("id") UUID id);
}
