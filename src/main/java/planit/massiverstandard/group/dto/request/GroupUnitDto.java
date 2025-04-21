package planit.massiverstandard.group.dto.request;

import planit.massiverstandard.group.entity.GroupUnitType;

import java.util.List;
import java.util.UUID;

/**
 * DTO for GroupUnit
 *
 * @param id        실행 단위 ID
 * @param groupUnitType      그룹 유닛 유형
 * @param parentIds 선행 ID 목록
 */
public record GroupUnitDto(
    UUID id,
    GroupUnitType groupUnitType,
    List<ParentGroupUnitDto> parentIds
) {
}
