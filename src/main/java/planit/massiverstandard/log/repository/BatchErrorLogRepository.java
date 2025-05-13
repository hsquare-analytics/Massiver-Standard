package planit.massiverstandard.log.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import planit.massiverstandard.log.entity.BatchErrorLog;

import java.util.UUID;

public interface BatchErrorLogRepository extends JpaRepository<BatchErrorLog, UUID> {
}
