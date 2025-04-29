package planit.massiverstandard.datasource.util.sql;

public class PostgreSelectSqlFactory implements SelectSqlFactory {

    @Override
    public String getPkList(String schema, String table) {
        // PostgreSQL은 식별자를 소문자로 변환해서 비교하는 것이 안전합니다.
        String sch = schema.toLowerCase();
        String tbl = table.toLowerCase();

        return String.format("""
            SELECT kcu.column_name
            FROM information_schema.table_constraints AS tc
            JOIN information_schema.key_column_usage AS kcu
              ON tc.constraint_name = kcu.constraint_name
             AND tc.table_schema    = kcu.table_schema
            WHERE tc.constraint_type = 'PRIMARY KEY'
              AND tc.table_schema    = '%s'       -- 스키마 명
              AND tc.table_name      = '%s'       -- 테이블 명
            ORDER BY kcu.ordinal_position;        -- PK 순서 보장
            """,
            sch,
            tbl
        );
    }


    @Override
    public String getSchemaList() {
        return """
            SELECT schema_name
            FROM information_schema.schemata
            ORDER BY schema_name;
            """;
    }

    @Override
    public String getTableList(String schema) {
        String sch = schema.toLowerCase();
        return String.format("""
            SELECT table_name
            FROM information_schema.tables
            WHERE table_schema = '%s'
              AND table_type = 'BASE TABLE'
            ORDER BY table_name;
            """,
            sch
        );
    }

    @Override
    public String getColumnList(String schema, String table) {
        String sch = schema.toLowerCase();
        String tbl = table.toLowerCase();
        return String.format("""
        SELECT
            col.column_name,
            col.ordinal_position,
            col.data_type,
            col.character_maximum_length,
            col.numeric_precision,
            col.numeric_scale,
            col.is_nullable,
            col.column_default,
            COALESCE(pk.is_primary, FALSE)                 AS is_primary_key
        FROM information_schema.columns AS col
        LEFT JOIN (
            SELECT
                kcu.table_schema,
                kcu.table_name,
                kcu.column_name,
                TRUE AS is_primary
            FROM information_schema.table_constraints AS tc
            JOIN information_schema.key_column_usage AS kcu
              ON tc.constraint_name = kcu.constraint_name
             AND tc.table_schema   = kcu.table_schema
             AND tc.table_name     = kcu.table_name
            WHERE tc.constraint_type = 'PRIMARY KEY'
        ) AS pk
          ON col.table_schema  = pk.table_schema
         AND col.table_name    = pk.table_name
         AND col.column_name   = pk.column_name
        WHERE col.table_schema = '%s'
          AND col.table_name   = '%s'
        ORDER BY col.ordinal_position;
            """,
            sch,
            tbl
        );
    }
}

