package planit.massiverstandard.group;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import planit.massiverstandard.group.dto.mapper.Mapper;
import planit.massiverstandard.group.dto.request.GroupDto;
import planit.massiverstandard.group.dto.request.GroupRequestDto;
import planit.massiverstandard.group.dto.response.GroupResponseDto;
import planit.massiverstandard.group.dto.response.GroupResultDto;
import planit.massiverstandard.group.service.FindGroup;
import planit.massiverstandard.group.service.GroupService;
import planit.massiverstandard.group.service.CommandGroup;

import java.util.List;
import java.util.UUID;

@Tag(name = "Group", description = "그룹 API")
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final FindGroup findGroupfindGroup;
    private final CommandGroup commandGroup;

    private final Mapper mapper;


    @Operation(summary = "그룹 생성", description = "그룹을 생성합니다.")
    @PostMapping
    public GroupResponseDto createGroup(@RequestBody GroupRequestDto groupRequestDto) {
        GroupDto dto = mapper.toDto(groupRequestDto);
        GroupResultDto group = groupService.create(dto);
        return mapper.toResponseDto(group);
    }

    @Operation(summary = "그룹 조회", description = "모든 그룹을 조회합니다.")
    @GetMapping
    public List<GroupResponseDto> getAllGroups() {
        List<GroupResultDto> groupList = groupService.getAllGroups();
        return groupList.stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Operation(summary = "그룹 ID로 조회", description = "그룹 ID로 그룹을 조회합니다.")
    @GetMapping("/{id}")
    public GroupResponseDto getGroup(@PathVariable UUID id) {
        GroupResultDto group = findGroupfindGroup.resultDtoById(id);
        return mapper.toResponseDto(group);
    }

    @Operation(summary = "그룹 수정", description = "그룹을 수정합니다.")
    @PatchMapping("/{id}")
    public GroupResponseDto updateGroup(@PathVariable UUID id,
                                        @RequestBody GroupRequestDto groupRequestDto) {
        GroupDto dto = mapper.toDto(groupRequestDto);
        GroupResultDto group = commandGroup.update(id, dto);
        return mapper.toResponseDto(group);
    }

    @Operation(summary = "그룹 삭제", description = "그룹을 삭제합니다.")
    @DeleteMapping("/{id}")
    public void deleteGroup(@PathVariable UUID id) {
        groupService.deleteGroup(id);
    }
}
