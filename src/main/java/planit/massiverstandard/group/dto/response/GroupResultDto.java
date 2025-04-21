package planit.massiverstandard.group.dto.response;

import planit.massiverstandard.schedule.dto.response.ScheduleResultDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GroupResultDto(
    UUID id,
    String name,
    List<GroupUnitResultDto> groupUnits,
    List<ScheduleResultDto> schedules,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    boolean isActive
) {

}
