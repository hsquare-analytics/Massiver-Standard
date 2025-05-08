package planit.massiverstandard.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import planit.massiverstandard.log.exception.entity.ExceptionLog;
import planit.massiverstandard.log.exception.entity.ExceptionType;
import planit.massiverstandard.log.exception.service.CommandExceptionLog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ApiControllerAdvice {

    private final CommandExceptionLog commandExceptionLog;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors()
            .forEach(c -> errors.put(((FieldError) c).getField(), c.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        ApiErrorResponse error = new ApiErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.getMessage()
        );

        // 예외 로그 저장
        saveException(ex, ExceptionType.DOMAIN);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(BatchException.class)
    public void handleBatchException(Exception ex) {

        // 예외 로그 저장
        saveException(ex, ExceptionType.BATCH);
    }

    private void saveException(Exception ex, ExceptionType exceptionType) {
        try {
            ExceptionLog log = ExceptionLog.builder()
                .exceptionClass(ex.getClass().getName())
                .message(ex.getMessage())
                .stacktrace(getStackTraceAsString(ex))
                .type(exceptionType)
                .build();
            commandExceptionLog.save(log);
        } catch (Exception e) {
            // 예외 핸들러 내 저장 실패 시 무한루프 방지
            log.error("ExceptionLog 저장 중 오류", e);
        }
    }

    private String getStackTraceAsString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
