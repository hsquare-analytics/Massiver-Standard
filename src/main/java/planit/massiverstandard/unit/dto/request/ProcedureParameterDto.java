package planit.massiverstandard.unit.dto.request;

import planit.massiverstandard.unit.entity.ProcedureParameterMode;

public record ProcedureParameterDto(
    String name,
    String dataType,
    int ordinal,
    ProcedureParameterMode mode,
    String value
) {
}
