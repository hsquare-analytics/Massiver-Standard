package planit.massiverstandard.datasource.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ProcedureResponse(

    @Schema(description = "프로시저 이름", example = "getEmployeeDetails")
    String procedureName,

    @Schema(description = "프로시저 인자", example = "employeeId, departmentId")
    List<String> procedureArguments
) {
}
