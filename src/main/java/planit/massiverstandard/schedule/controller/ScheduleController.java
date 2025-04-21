package planit.massiverstandard.schedule.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import planit.massiverstandard.schedule.ScheduleDto;
import planit.massiverstandard.schedule.dto.response.ScheduleResponseDto;
import planit.massiverstandard.schedule.dto.response.ScheduleResultDto;
import planit.massiverstandard.schedule.usecase.ActiveSchedule;
import planit.massiverstandard.schedule.usecase.RegisterSchedule;

import java.util.List;
import java.util.UUID;

@Tag(name = "스케줄 API", description = "스케줄 관련 API")
@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ActiveSchedule executeSchedule;
    private final RegisterSchedule registerSchedule;

    @Operation(summary = "스케줄 등록", description = "스케줄을 등록합니다. (실행 X)")
    @PostMapping("/{groupId}")
    public ScheduleResponseDto addSchedule(@PathVariable(name = "groupId") UUID groupId,
                                           @RequestBody ScheduleDto dto) {
        List<ScheduleResultDto> scheduleResultDtos = registerSchedule.updateSchedule(groupId, dto);
        List<String> crons = scheduleResultDtos.stream()
            .map(ScheduleResultDto::cron)
            .toList();
        return new ScheduleResponseDto(crons);
    }

    @Operation(summary = "스케줄 활성화", description = "스케줄을 활성화합니다.")
    @PostMapping("/{groupId}/activate")
    public List<String> activeSchedule(@PathVariable UUID groupId) {
        return executeSchedule.active(groupId);
    }

    @Operation(summary = "스케줄 비활성화", description = "스케줄을 비활성화합니다.")
    @PostMapping("/{groupId}/deactivate")
    public void deActiveSchedule(@PathVariable UUID groupId) {
        executeSchedule.deActive(groupId);
    }

}
