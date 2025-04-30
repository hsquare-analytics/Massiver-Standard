package planit.massiverstandard.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import planit.massiverstandard.group.entity.GroupUnit;
import planit.massiverstandard.unit.entity.Unit;

import java.util.List;
import java.util.UUID;

public interface GroupUnitRepository extends JpaRepository<GroupUnit, UUID> {

    boolean existsByChildUnit_Id(UUID unitId);
}
