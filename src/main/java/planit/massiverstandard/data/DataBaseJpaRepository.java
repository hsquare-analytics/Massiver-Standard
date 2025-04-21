package planit.massiverstandard.data;

import org.springframework.data.jpa.repository.JpaRepository;
import planit.massiverstandard.database.DataBase;
import planit.massiverstandard.database.repository.DataBaseRepository;

import java.util.UUID;

public interface DataBaseJpaRepository extends DataBaseRepository, JpaRepository<DataBase, UUID> {
}
