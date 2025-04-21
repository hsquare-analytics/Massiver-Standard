package planit.massiverstandard.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import planit.massiverstandard.group.entity.Group;

import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {
}
