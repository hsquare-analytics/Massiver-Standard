package planit.massiverstandard.log.exception.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import planit.massiverstandard.BaseEntity;

import java.util.UUID;

@Entity
@Table(name = "massiver_st_exception_log")
@Getter
@NoArgsConstructor
public class ExceptionLog extends BaseEntity {

    @Id
    @Column(name = "exception_log_id")
    private UUID id;

    /** 예외 클래스 이름 (예: NullPointerException) */
    @Column(length = 255, nullable = false)
    private String exceptionClass;

    /** 예외 메시지 */
    @Column(length = 1000)
    private String message;

    /** 전체 스택트레이스 (길어질 수 있어 Lob 처리) */
    @Lob
    @Column(nullable = false)
    private String stacktrace;

    @Enumerated(EnumType.STRING)
    private ExceptionType type;

    @Builder
    public ExceptionLog(String stacktrace, String message, String exceptionClass, ExceptionType type) {
        this.id = UUID.randomUUID();
        this.stacktrace = stacktrace;
        this.message = message;
        this.exceptionClass = exceptionClass;
        this.type = type;
    }
}
