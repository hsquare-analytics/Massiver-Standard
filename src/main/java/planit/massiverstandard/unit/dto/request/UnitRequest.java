package planit.massiverstandard.unit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.Getter;
import planit.massiverstandard.columntransform.dto.ColumnTransformRequest;
import planit.massiverstandard.filter.dto.FilterRequestDto;

import java.util.List;
import java.util.UUID;

@Getter
@Data
public class UnitRequest {

    @Schema(description = "단위 ETL 작업명", example = "ETL 작업")
    private String name;

    @Schema(description = "소스 DB ID", example = "509f84d9-ae0a-4685-b8fb-31c2dcf95ef9")
    private UUID sourceDb;

    @Schema(description = "소스 스키마명", example = "PUBLIC")
    private String sourceSchema;

    @Schema(description = "소스 테이블명", example = "SAMPLE_TABLE")
    private String sourceTable;

    @Schema(description = "타겟 DB ID", example = "3d09ec88-a2f6-421a-8d2d-1ff5db5fc6f6")
    private UUID targetDb;

    @Schema(description = "타겟 스키마명", example = "PUBLIC")
    private String targetSchema;

    @Schema(description = "타겟 테이블명", example = "SAMPLE_TABLE")
    private String targetTable;

    @Valid
    @Schema(description = "컬럼 변환 정보", example = "[{\"sourceColumn\":\"ID\", \"targetColumn\":\"NEW_ID\"}]")
    private List<ColumnTransformRequest> columnTransforms;

    @Schema(
        description = "적용할 필터 정보 목록",
        example = "[{\"name\":\"필터1\", \"order\":1, \"groupUnitType\":\"SQL\", \"sql\":\"WHERE ID < 5\"}, "
            + "{\"name\":\"필터2\", \"order\":2, \"groupUnitType\":\"API\"}]"
    )
    private List<FilterRequestDto> filters;

}
