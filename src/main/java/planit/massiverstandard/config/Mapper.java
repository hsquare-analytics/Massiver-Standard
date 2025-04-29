package planit.massiverstandard.config;

import org.mapstruct.Mapping;
import planit.massiverstandard.datasource.dto.request.DataSourceCreateDto;
import planit.massiverstandard.datasource.dto.request.DataSourceRequest;
import planit.massiverstandard.datasource.dto.request.DataSourceTestConnectionDto;
import planit.massiverstandard.datasource.dto.request.DataSourceTestConnectionRequest;
import planit.massiverstandard.group.dto.request.*;
import planit.massiverstandard.group.dto.response.*;
import planit.massiverstandard.group.entity.Group;
import planit.massiverstandard.group.entity.GroupUnit;
import planit.massiverstandard.schedule.Schedule;
import planit.massiverstandard.schedule.dto.response.ScheduleResultDto;
import planit.massiverstandard.unit.entity.Unit;

import java.util.List;

@org.mapstruct.Mapper(config = DefaultMapStructConfig.class)
public interface Mapper {

    // group
    @Mapping(target = "groupUnits", source = "groupUnits")
    Group toGroup(GroupDto groupDto, List<GroupUnit> groupUnits);

    GroupDto toDto(GroupRequestDto groupRequestDto);

    GroupResultDto groupTooResultDto(Group group);

    GroupResponseDto toResponseDto(GroupResultDto group);

    // schedule
    Schedule toGroup(String cron, Group group);

    ScheduleResultDto toScheduleResultDto(Schedule schedules);

    // GroupUnit
    @Mapping(target = "childGroup", ignore = true)
    GroupUnit toUnitEntity(Unit childUnit, List<GroupUnit> parentGroupUnits, GroupUnitDto groupUnitDto);

    @Mapping(target = "childUnit", ignore = true)
    GroupUnit toGroupEntity(Group childGroup, List<GroupUnit> parentGroupUnits, GroupUnitDto groupUnitDto);

    GroupUnitResultDto toResultDto(GroupUnit groupUnit);

    ParentGroupUnitDto toParentGroupUnitDto(ParentGroupUnitRequest request);

    ParentGroupUnitResponse toParentGroupUnitResponse(ParentGroupUnitResult parentGroupUnitResult);

    // datasource
    DataSourceTestConnectionDto toDataSourceTestConnectionDto(DataSourceTestConnectionRequest request);
    DataSourceCreateDto toDataSourceCreateDto(DataSourceRequest request);
}
