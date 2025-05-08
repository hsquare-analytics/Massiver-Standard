package planit.massiverstandard.columntransform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ColumnTransformRequest(

    @Schema(description = "덮어쓰기 여부", example = "true")
    boolean isOverWrite,

    @Schema(description = "소스 컬럼명", example = "ID")
    @NotBlank
    String sourceColumn,

    @Schema(description = "타겟 컬럼명", example = "NEW_ID")
    @NotBlank
    String targetColumn,

    @Schema(description = "타겟 컬럼 타입", example = "VARCHAR")
    String targetColumnType
) {
}
