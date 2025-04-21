package planit.massiverstandard.group.service;

import planit.massiverstandard.group.dto.request.GroupDto;
import planit.massiverstandard.group.dto.response.GroupResultDto;

import java.util.UUID;

/**
 * 설명: 그룹 커맨드
 * 작성일: 2025. 04. 07.
 * 작성자: Mason
 */
public interface CommandGroup {

    /**
     * 그룹 생성
     * @param groupDto 그룹 DTO
     * @return 생성된 그룹 DTO
     */
    GroupResultDto create(GroupDto groupDto);

    /**
     * 그룹 업데이트
     * @param groupId 그룹 ID
     * @param groupDto 그룹 DTO
     * @return
     */
    GroupResultDto update(UUID groupId, GroupDto groupDto);

}
