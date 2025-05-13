package planit.massiverstandard.log.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import planit.massiverstandard.BaseEntity;

import java.util.UUID;

@Entity
@Table(name = "massiver_st_batch_error_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BatchErrorLog extends BaseEntity {

    @Id
    @Column(name = "batch_error_log_id")
    private UUID id;

    @Comment("그룹 ID")
    private UUID groupId;

    @Comment("유닛 ID")
    private UUID unitId;

    @Comment("발생 STEP 명")
    private String stepName;

    @Comment("오류 클래스")
    private String exceptionClass;

    @Comment("오류 메시지")
    @Column(length = 1000)
    private String exceptionMessage;

    @Comment("오류 스택 트레이스")
    @Column(columnDefinition = "TEXT")
    private String exceptionStackTrace;

    @Builder
    public BatchErrorLog(UUID groupId, UUID unitId, String stepName,
                         String exceptionClass, String exceptionMessage,
                         String exceptionStackTrace) {
        this.id = UUID.randomUUID();
        this.groupId = groupId;
        this.unitId = unitId;
        this.stepName = stepName;
        this.exceptionClass = exceptionClass;
        this.exceptionMessage = exceptionMessage;
        this.exceptionStackTrace = exceptionStackTrace;
    }
}
