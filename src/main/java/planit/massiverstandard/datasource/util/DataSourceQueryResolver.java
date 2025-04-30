package planit.massiverstandard.datasource.util;

import planit.massiverstandard.datasource.entity.DataSourceType;
import planit.massiverstandard.datasource.util.sql.H2SelectSqlFactory;
import planit.massiverstandard.datasource.util.sql.PostgreSelectSqlFactory;
import planit.massiverstandard.datasource.util.sql.SelectSqlFactory;
import planit.massiverstandard.exception.datasource.UnsupportedDataSourceTypeException;

public class DataSourceQueryResolver {

    public static SelectSqlFactory getSelectSqlFactory(DataSourceType dbType) {
        return switch (dbType) {
            case H2_TCP -> new H2SelectSqlFactory();
//            case MYSQL -> new MySqlSelectSqlFactory();
//            case ORACLE -> new OracleSelectSqlFactory();
            case POSTGRESQL -> new PostgreSelectSqlFactory();
//            case SQLSERVER -> new SqlServerSelectSqlFactory();
//            case VERTICA -> new VerticaSelectSqlFactory();
//            case SNOWFLAKE -> new SnowflakeSelectSqlFactory();
            default -> throw new UnsupportedDataSourceTypeException("지원하지 않는 데이터베이스 타입입니다: " + dbType);
        };
    }
}
