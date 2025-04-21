package planit.massiverstandard.group.service;

import planit.massiverstandard.group.dto.response.GroupResultDto;
import planit.massiverstandard.group.entity.Group;

import java.util.UUID;

/**
 * 설명: 그룹 조회
 * 작성일: 2025. 04. 01.
 * 작성자: Mason
 */
public interface FindGroup {

    /**
     * id로 그룹 조회
     * @param id 그룹 id
     * @return 조회한 그룹
     */
    Group byId(UUID id);

    GroupResultDto resultDtoById(UUID id);
}
