package planit.massiverstandard.log.exception.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import planit.massiverstandard.log.exception.entity.ExceptionLog;
import planit.massiverstandard.log.exception.repository.ExceptionLogRepository;


@Service
@RequiredArgsConstructor
public class QueryExceptionLogService implements FindExceptionLog{
    private final ExceptionLogRepository exceptionLogRepository;

    @Override
    public Page<ExceptionLog> findAllBLatest(Pageable pageable) {
        return exceptionLogRepository.findAllBLatest(pageable);
    }
}
