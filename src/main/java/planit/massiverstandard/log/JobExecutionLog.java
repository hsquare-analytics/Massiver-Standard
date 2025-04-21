package planit.massiverstandard.log;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Batch Job 실행 결과 정보를 담는 레코드 (DTO).
 * UI 표시 또는 API 응답에 사용됩니다.
 *
 * @param jobExecutionId Spring Batch Job 실행 ID (BATCH_JOB_EXECUTION 테이블의 PK).
 * @param jobName        실행된 Job의 이름.
 * @param unitId         Massiver 실행 단위 ID (Job Parameter에서 추출). 특정 실행을 식별하는 주요 키.
 * @param jobParameters  실행 시 사용된 Job 파라미터 (보통 JSON 형태의 문자열).
 * @param startTime      Job 실행 시작 시간.
 * @param endTime        Job 실행 종료 시간 (실행 중일 경우 null).
 * @param duration       Job 실행 소요 시간 (endTime - startTime), 사람이 읽기 좋은 형태의 문자열 (예: "1m 30s"). 실행 중이면 null 또는 계산 중 표시.
 * @param status         Job의 최종 상태 문자열 (예: "COMPLETED", "FAILED", "STARTING"). Spring Batch의 BatchStatus 참고.
 * @param exitCode       Job의 종료 코드 문자열 (예: "COMPLETED", "FAILED", 사용자 정의 코드). Spring Batch의 ExitStatus 참고.
 * @param readCount      Job에 포함된 모든 Step에서 읽은 총 아이템 수.
 * @param writeCount     Job에 포함된 모든 Step에서 쓴 (성공한) 총 아이템 수.
 * @param skipCount      Job에 포함된 모든 Step에서 스킵된 총 아이템 수.
 * @param errorMessage   Job 실패 시 주요 에러 메시지 요약 (성공 시 null 또는 비어있음).
 */
public record JobExecutionLog(
    long jobExecutionId,
    String jobName,
    String unitId,
    String jobParameters,
    LocalDateTime startTime,
    LocalDateTime endTime,
    String duration, // 계산된 값 또는 null
    String status,
    String exitCode,
    long readCount,
    long writeCount,
    long skipCount,
    String errorMessage
) {
    /**
     * Job 실행 시간을 계산하여 사람이 읽기 좋은 형태의 문자열로 반환하는 정적 팩토리 메소드 또는 변환 로직 추가 가능.
     * 예시: Duration 클래스를 활용하여 포맷팅.
     *
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 포맷팅된 실행 시간 문자열 (예: "1h 2m 3s", "45s") 또는 null
     */
    public static String formatDuration(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null || endTime.isBefore(startTime)) {
            return null; // 또는 "Calculating..." 등 상태 표시
        }
        Duration duration = Duration.between(startTime, endTime);
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
            "%dh %dm %ds",
            absSeconds / 3600,
            (absSeconds % 3600) / 60,
            absSeconds % 60);
        // 간단하게 초 단위로만 표시할 수도 있음: return absSeconds + "s";
        return positive;
    }
}
