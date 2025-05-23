package planit.massiverstandard.group.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import planit.massiverstandard.config.Mapper;
import planit.massiverstandard.exception.group.GroupNotFoundException;
import planit.massiverstandard.group.dto.response.GroupResultDto;
import planit.massiverstandard.group.entity.Group;
import planit.massiverstandard.group.repository.GroupRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupGetService implements FindGroup{

    private final GroupRepository groupRepository;
    private final Mapper mapper;

    @Override
    @Transactional(value = "transactionManager", readOnly = true)
    public Group byId(UUID id) {
        Group group = groupRepository.findById(id).orElseThrow(() -> new GroupNotFoundException("Group을 찾을 수 없습니다"));

        // Lazy Loading을 위한 호출
        group.getGroupUnits().size();
        group.getSchedules().size();

        return group;
    }

    @Override
    @Transactional(value = "transactionManager" , readOnly = true)
    public GroupResultDto resultDtoById(UUID id) {
        Group group = byId(id);
        return mapper.groupTooResultDto(group);
    }
}
