package planit.massiverstandard.log.exception.service;

import planit.massiverstandard.log.exception.entity.ExceptionLog;

public interface CommandExceptionLog {

    /**
     * 예외 로그를 저장합니다.
     * @param exceptionLog 저장할 예외 로그
     */
    void save(ExceptionLog exceptionLog);
}
