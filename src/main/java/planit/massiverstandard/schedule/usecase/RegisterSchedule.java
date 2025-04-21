package planit.massiverstandard.schedule.usecase;

import planit.massiverstandard.schedule.ScheduleDto;
import planit.massiverstandard.schedule.dto.response.ScheduleResultDto;

import java.util.List;
import java.util.UUID;

/**
 * 설명: 스케줄 등록
 * 작성일: 2025. 04. 01.
 * 작성자: Mason
 */
public interface RegisterSchedule {

    /**
     * 스케줄 등록
     * @param groupId 그룹 ID
     * @param scheduleDto 스케줄 정보
     * @return 등록된 스케줄 정보
     */
    List<ScheduleResultDto> updateSchedule(UUID groupId, ScheduleDto scheduleDto);
}
