package planit.massiverstandard.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import planit.massiverstandard.group.entity.GroupUnit;

import java.util.UUID;

public interface GroupUnitRepository extends JpaRepository<GroupUnit, UUID> {

    boolean existsByChildUnit_Id(UUID unitId);
}
