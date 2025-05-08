package planit.massiverstandard.log.exception.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import planit.massiverstandard.log.exception.entity.ExceptionLog;

import java.util.UUID;

public interface ExceptionLogJpaRepository extends JpaRepository<ExceptionLog, UUID> {

    Page<ExceptionLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
