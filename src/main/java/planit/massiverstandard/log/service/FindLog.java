package planit.massiverstandard.log.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import planit.massiverstandard.log.dto.BatchJobInfo;
import planit.massiverstandard.log.dto.request.BatchJobLogSearchDto;

/**
 * 설명: 로그를 찾기 위한 인터페이스
 * 작성일: 2025. 04. 10.
 * 작성자: Mason
 */
public interface FindLog {

    /**
     * 설명: 로그를 찾기 위한 메소드
     * @param pageable 페이지 정보
     * @return 로그 정보
     */
    Page<BatchJobInfo> findLog(Pageable pageable, BatchJobLogSearchDto searchDto);
}
