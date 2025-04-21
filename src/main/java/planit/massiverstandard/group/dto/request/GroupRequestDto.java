package planit.massiverstandard.group.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record GroupRequestDto(
    @NotBlank
    @Schema(description = "그룹명", example = "그룹1")
    String name,

    @Schema(description = "UNIT 목록")
    List<GroupUnitRequestDto> groupUnits
) {

}
