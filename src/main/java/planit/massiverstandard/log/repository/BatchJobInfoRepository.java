package planit.massiverstandard.log.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import planit.massiverstandard.log.dto.BatchJobInfo;
import planit.massiverstandard.log.dto.request.BatchJobLogSearchDto;

/**
 * 설명: JobExecutionRepository 인터페이스
 * 작성일: 2025. 04. 21.
 * 작성자: Mason
 */
public interface BatchJobInfoRepository {
    Page<BatchJobInfo> findAll(Pageable pageable, BatchJobLogSearchDto searchDto);
}
