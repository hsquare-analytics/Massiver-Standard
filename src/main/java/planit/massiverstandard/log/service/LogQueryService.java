package planit.massiverstandard.log.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import planit.massiverstandard.log.dto.BatchJobInfo;
import planit.massiverstandard.log.dto.request.BatchJobLogSearchDto;
import planit.massiverstandard.log.repository.BatchJobInfoRepository;

@Service
@RequiredArgsConstructor
public class LogQueryService implements FindLog {

    private final BatchJobInfoRepository batchJobInfoRepository;

    @Override
    public Page<BatchJobInfo> findLog(Pageable pageable, BatchJobLogSearchDto searchDto) {
        return batchJobInfoRepository.findAll(pageable, searchDto);
    }

}
