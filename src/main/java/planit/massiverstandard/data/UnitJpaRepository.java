package planit.massiverstandard.data;

import org.springframework.data.jpa.repository.JpaRepository;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.repository.UnitRepository;

import java.util.UUID;

public interface UnitJpaRepository extends UnitRepository, JpaRepository<Unit, UUID> {
}
