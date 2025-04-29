package planit.massiverstandard.datasource.dto.response;

public record ColumnInfoResult(
    String columnName,
    int ordinalPosition,
    String dataType,
    int characterMaximumLength,
    int numericPrecision,
    int numericScale,
    String isNullable,
    String columnDefault,
    boolean isPrimaryKey
) {
}
