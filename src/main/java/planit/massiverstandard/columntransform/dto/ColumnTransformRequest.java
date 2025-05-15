package planit.massiverstandard.columntransform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import planit.massiverstandard.columntransform.entity.TransformType;

public record ColumnTransformRequest(

    @Schema(description = "덮어쓰기 여부", example = "true")
    boolean isOverWrite,

    @Schema(description = "소스 컬럼명", example = "ID")
    @NotBlank
    String sourceColumn,

    @Schema(description = "변환 타입", example = "NONE")
    TransformType transformType,

    @Schema(description = "날짜·시간 포맷 패턴(yyyyMMdd 등)", example = "yyyyMMdd")
    String formatPattern,

    @Schema(description = "CUSTOM_SQL 용 스크립트", example = "SELECT * FROM table WHERE id = ?")
    String customExpression,

    @Schema(description = "substring 시작 인덱스", example = "0")
    Integer substrStart,

    @Schema(description = "substring 길이", example = "10")
    Integer substrLength,

    @Schema(description = "정규식 패턴", example = "\\d{3}-\\d{2}-\\d{4}")
    String regexPattern,

    @Schema(description = "정규식 치환 문자열", example = "$1-$2-$3")
    String replacement,

    @Schema(description = "타겟 컬럼명", example = "NEW_ID")
    @NotBlank
    String targetColumn,

    @Schema(description = "타겟 컬럼 타입", example = "VARCHAR")
    String targetColumnType
) {
}
