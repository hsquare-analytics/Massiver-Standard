package planit.massiverstandard.group.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import planit.massiverstandard.config.Mapper;
import planit.massiverstandard.group.dto.request.GroupDto;
import planit.massiverstandard.group.dto.response.GroupResultDto;
import planit.massiverstandard.group.entity.Group;
import planit.massiverstandard.group.entity.GroupUnit;
import planit.massiverstandard.group.repository.GroupRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupService implements CommandGroup {

    private final GroupRepository groupRepository;

    private final Mapper mapper;
    private final GroupUnitService groupUnitService;
    private final GroupGetService groupGetService;

    @Override
    @Transactional("transactionManager")
    public GroupResultDto create(GroupDto groupDto) {

        List<GroupUnit> groupUnitList = groupUnitService.createGroupUnitList(groupDto.groupUnits());

        Group group = mapper.toGroup(groupDto, groupUnitList);
        group.addGroupUnits(groupUnitList);
        Group savedGroup = groupRepository.save(group);

        return mapper.groupTooResultDto(savedGroup);
    }

    public List<GroupResultDto> getAllGroups() {
        List<Group> all = groupRepository.findAll();
        return all.stream().map(mapper::groupTooResultDto).toList();
    }

    @Override
    @Transactional("transactionManager")
    public GroupResultDto update(UUID groupId, GroupDto groupDto) {
        Group group = groupGetService.byId(groupId);
        List<GroupUnit> groupUnitList = groupUnitService.createGroupUnitList(groupDto.groupUnits());
        Group updatedGroup = group.updateGroup(groupDto, groupUnitList);
        return mapper.groupTooResultDto(updatedGroup);
    }

    public void deleteGroup(UUID id) {
        Group group = groupGetService.byId(id);
        groupRepository.delete(group);
    }
}
