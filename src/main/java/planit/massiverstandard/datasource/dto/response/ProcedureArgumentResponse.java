package planit.massiverstandard.datasource.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProcedureArgumentResponse(
    @Schema(description = "프로시저 인자 이름", example = "employeeId")
    String name,
    @Schema(description = "프로시저 인자 타입", example = "int")
    String dataType
) {
}
