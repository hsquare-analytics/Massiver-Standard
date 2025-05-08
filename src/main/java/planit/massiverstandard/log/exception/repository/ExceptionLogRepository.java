package planit.massiverstandard.log.exception.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import planit.massiverstandard.log.exception.entity.ExceptionLog;

public interface ExceptionLogRepository {

    void save(ExceptionLog exceptionLog);

    /**
     * 예외 로그를 페이징 해 조회합니다.
     * @param pageable
     * @return
     */
    Page<ExceptionLog> findAllBLatest(Pageable pageable);
}
