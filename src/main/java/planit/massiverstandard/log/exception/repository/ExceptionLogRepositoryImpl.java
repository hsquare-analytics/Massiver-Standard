package planit.massiverstandard.log.exception.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import planit.massiverstandard.log.exception.entity.ExceptionLog;

@Repository
@RequiredArgsConstructor
public class ExceptionLogRepositoryImpl implements ExceptionLogRepository{
    private final ExceptionLogJpaRepository exceptionLogJpaRepository;

    @Override
    public void save(ExceptionLog exceptionLog) {
        exceptionLogJpaRepository.save(exceptionLog);
    }

    @Override
    public Page<ExceptionLog> findAllBLatest(Pageable pageable) {
        return exceptionLogJpaRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
}
