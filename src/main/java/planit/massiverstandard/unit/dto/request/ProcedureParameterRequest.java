package planit.massiverstandard.unit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProcedureParameterRequest(
    @Schema(description = "파라미터 이름", example = "employeeId")
    String name,

    @Schema(description = "파라미터 타입", example = "int")
    String dataType,

    @Schema(description = "파라미터 순서", example = "1")
    int ordinal,

    @Schema(description = "파라미터 모드", example = "EXPRESSION")
    String mode,

    @Schema(description = "파라미터 값", example = "T(java.time.LocalDate).now().toString()")
    String value
) {
}
