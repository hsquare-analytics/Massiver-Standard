package planit.massiverstandard.group.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import planit.massiverstandard.group.entity.GroupUnitType;

import java.util.List;
import java.util.UUID;
/**
 * DTO for GroupUnit
 *
 * @param id        그룹 유닛 ID
 * @param groupUnitType      그룹 유닛 유형
 * @param parentIds 선행 ID 목록
 */
public record GroupUnitRequestDto(

    @NotNull
    @Schema(description = "ID", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,

    @NotNull
    @Schema(description = "유형", example = "UNIT")
    GroupUnitType groupUnitType,

    @Schema(description = "선행 ID 목록", example = "[\"123e4567-e89b-12d3-a456-426614174000\"]")
    List<ParentGroupUnitRequest> parentIds

) {
}
