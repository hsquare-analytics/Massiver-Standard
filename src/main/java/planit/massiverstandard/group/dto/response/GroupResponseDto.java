package planit.massiverstandard.group.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.scheduling.support.CronExpression;
import planit.massiverstandard.schedule.dto.response.ScheduleResultDto;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Data
public class GroupResponseDto {

    private UUID id;
    private String name;
    private List<GroupUnitResponseDto> groupUnits;
    private List<String> crons;
    private LocalDateTime systemTime;
    private LocalDateTime nextExecutionTime;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActive;

    @Builder
    public GroupResponseDto(UUID id, String name, List<GroupUnitResponseDto> groupUnits, LocalDateTime createdAt, LocalDateTime updatedAt, List<ScheduleResultDto> schedules, boolean isActive) {
        this.id = id;
        this.name = name;
        this.groupUnits = groupUnits;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        this.crons = schedules.stream()
                .map(ScheduleResultDto::cron)
                .toList();

        this.systemTime = LocalDateTime.now();
        this.nextExecutionTime = crons.stream()
            .map(CronExpression::parse) // 문자열 → CronExpression
            .map(expr -> expr.next(this.systemTime)) // 다음 실행 시각
            .filter(next -> next != null) // null 제거
            .min(Comparator.naturalOrder()) // 가장 이른 시각 선택
            .orElse(null); // 모두 null일 경우 null 리턴

        this.isActive = isActive;
    }
}
