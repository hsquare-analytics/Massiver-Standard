package planit.massiverstandard.datasource.dto.response;

/**
 * 프로시저 결과
 * @param procedureName 프로시저 이름
 * @param procedureArguments 프로시저 인자
 */
public record ProcedureResult(
    String procedureName,
    String procedureArguments
) {
}
