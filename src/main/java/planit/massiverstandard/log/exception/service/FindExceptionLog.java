package planit.massiverstandard.log.exception.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import planit.massiverstandard.log.exception.entity.ExceptionLog;


public interface FindExceptionLog {

    /**
     * 예외 로그를 페이징 해 조회합니다.
     * @param pageable 페이징 정보
     * @return 예외 로그 페이지
     */
    Page<ExceptionLog> findAllBLatest(Pageable pageable);
}
