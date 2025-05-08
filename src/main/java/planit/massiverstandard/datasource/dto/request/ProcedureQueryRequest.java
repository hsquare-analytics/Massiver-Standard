package planit.massiverstandard.datasource.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ProcedureQueryRequest(
    @Schema(description = "파라미터 목록", example = "employeeId, departmentId")
    List<String> arguments
) {
}
