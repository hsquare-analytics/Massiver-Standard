package planit.massiverstandard.log.dto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Batch Job 실행 정보를 담는 레코드 (DTO).
 *
 * @param jobExecutionId jobExecutionId
 * @param version        version
 * @param jobInstanceId  jobInstanceId
 * @param jobName        job 이름
 * @param jobKey         job 키
 * @param createTime     생성 시간
 * @param startTime      시작 시간
 * @param endTime        종료 시간
 * @param status         실행 상태
 * @param exitCode       종료 코드
 * @param exitMessage    종료 메시지
 * @param lastUpdated    마지막 업데이트 시간
 */
public record BatchJobInfo(
    Long jobExecutionId,
    Long version,
    Long jobInstanceId,
    String jobName,
    String jobKey,
    LocalDateTime createTime,
    LocalDateTime startTime,
    LocalDateTime endTime,
    String elapsedTime,
    String status,
    String exitCode,
    String exitMessage,
    LocalDateTime lastUpdated
) {

    public BatchJobInfo(
        Long jobExecutionId,
        Long version,
        Long jobInstanceId,
        String jobName,
        String jobKey,
        LocalDateTime createTime,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status,
        String exitCode,
        String exitMessage,
        LocalDateTime lastUpdated
    ) {
        this(
            jobExecutionId,
            version,
            jobInstanceId,
            jobName,
            jobKey,
            createTime,
            startTime,
            endTime,
            formatElapsedTime(startTime, endTime), // ✅ 문자열로 계산
            status,
            exitCode,
            exitMessage,
            lastUpdated
        );
    }

    private static String formatElapsedTime(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return "-";

        long seconds = java.time.Duration.between(start, end).getSeconds();
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format("%02d:%02d", minutes, secs);
        }
    }
}
