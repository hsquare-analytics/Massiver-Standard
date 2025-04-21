package planit.massiverstandard.group.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import planit.massiverstandard.group.entity.GroupUnitType;

import java.util.UUID;

/**
 * DTO for ParentGroupUnit
 * @param id executable unit ID
 * @param groupUnitType 그룹 유닛 유형
 */
public record ParentGroupUnitRequest(
    @Schema(description = "ID", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,
    @Schema(description = "유형", example = "UNIT")
    GroupUnitType groupUnitType
) {
}
