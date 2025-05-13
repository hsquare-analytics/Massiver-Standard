package planit.massiverstandard.log.exception.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import planit.massiverstandard.log.exception.entity.ExceptionLog;
import planit.massiverstandard.log.exception.repository.ExceptionLogRepository;


@Service
@RequiredArgsConstructor
@Transactional("transactionManager")
public class CommandExceptionLogService implements CommandExceptionLog{

    private final ExceptionLogRepository findExceptionLog;

    @Override
    public void save(ExceptionLog exceptionLog) {
        findExceptionLog.save(exceptionLog);
    }
}
