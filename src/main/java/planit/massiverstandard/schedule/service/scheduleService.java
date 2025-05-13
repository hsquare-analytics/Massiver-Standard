package planit.massiverstandard.schedule.service;

import lombok.RequiredArgsConstructor;
import org.quartz.CronTrigger;
import org.quartz.Trigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import planit.massiverstandard.batch.usecase.CheckSchedule;
import planit.massiverstandard.batch.usecase.ExecuteSchedule;
import planit.massiverstandard.config.Mapper;
import planit.massiverstandard.group.entity.Group;
import planit.massiverstandard.group.service.FindGroup;
import planit.massiverstandard.schedule.Schedule;
import planit.massiverstandard.schedule.ScheduleDto;
import planit.massiverstandard.schedule.dto.response.ScheduleResultDto;
import planit.massiverstandard.schedule.usecase.ActiveSchedule;
import planit.massiverstandard.schedule.usecase.RegisterSchedule;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class scheduleService implements RegisterSchedule, ActiveSchedule {

    private final FindGroup findGroup;
    private final ExecuteSchedule executeSchedule;
    private final CheckSchedule checkSchedule;

    private final Mapper mapper;

    @Override
    @Transactional("transactionManager")
    public List<ScheduleResultDto> updateSchedule(UUID groupId, ScheduleDto scheduleDto) {
        Group group = findGroup.byId(groupId);

        List<Schedule> schedules = scheduleDto.crons().stream()
            .map(cron -> mapper.toGroup(cron, group))
            .toList();

        List<Schedule> updatedSchedules = group.updateSchedule(schedules);

        return updatedSchedules.stream()
            .map(mapper::toScheduleResultDto)
            .toList();
    }

    @Override
    @Transactional("transactionManager")
    public List<String> active(UUID groupID) {
        executeSchedule.active(groupID);
        Group group = findGroup.byId(groupID);
        group.active();

        List<? extends Trigger> triggers = checkSchedule.check(groupID);

        return triggers.stream()
            .filter(trigger -> trigger instanceof CronTrigger)
            .map(trigger -> ((CronTrigger) trigger).getCronExpression())
            .collect(Collectors.toList());
    }

    @Override
    @Transactional("transactionManager")
    public void deActive(UUID groupId) {
        executeSchedule.deActive(groupId);
        Group group = findGroup.byId(groupId);
        group.deActive();
    }

}
