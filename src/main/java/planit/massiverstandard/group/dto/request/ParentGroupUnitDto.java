package planit.massiverstandard.group.dto.request;

import planit.massiverstandard.group.entity.GroupUnitType;

import java.util.UUID;

/**
 * DTO for ParentGroupUnit
 * @param id executable unit ID
 * @param groupUnitType 그룹 유닛 유형
 */
public record ParentGroupUnitDto(
    UUID id,
    GroupUnitType groupUnitType
) {
}
