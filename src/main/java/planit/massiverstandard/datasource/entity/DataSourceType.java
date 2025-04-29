package planit.massiverstandard.datasource.entity;

public enum DataSourceType {
    H2_TCP("h2:tcp"),
    H2_MEM("mem"),
    MYSQL("mysql"),
    ORACLE("oracle"),
    POSTGRESQL("postgresql"),
    SQLSERVER("sqlserver"),
    VERTICA("vertica"),
    SNOWFLAKE("snowflake");

    private final String value;

    DataSourceType(String value) {
        this.value = value;
    }
}
