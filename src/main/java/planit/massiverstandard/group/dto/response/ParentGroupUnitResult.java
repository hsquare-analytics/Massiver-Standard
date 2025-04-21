package planit.massiverstandard.group.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import planit.massiverstandard.group.entity.GroupUnitType;

import java.util.UUID;

/**
 * DTO for ParentGroupUnit
 * @param id executable unit ID
 * @param executableId 실행 단위 ID
 * @param groupUnitType 그룹 유닛 유형
 */
public record ParentGroupUnitResult(
    UUID id,
    UUID executableId,
    GroupUnitType groupUnitType
) {
}
