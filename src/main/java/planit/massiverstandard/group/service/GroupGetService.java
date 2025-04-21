package planit.massiverstandard.group.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import planit.massiverstandard.group.dto.mapper.Mapper;
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
    @Transactional(readOnly = true)
    public Group byId(UUID id) {
        return groupRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Group을 찾을 수 없습니다"));
    }

    @Override
    @Transactional(readOnly = true)
    public GroupResultDto resultDtoById(UUID id) {
        Group group = byId(id);
        return mapper.groupTooResultDto(group);
    }
}
