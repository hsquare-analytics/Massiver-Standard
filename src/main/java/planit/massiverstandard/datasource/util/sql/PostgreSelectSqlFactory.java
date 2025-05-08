package planit.massiverstandard.datasource.util.sql;

import java.util.List;

public class PostgreSelectSqlFactory implements SelectSqlFactory {

    @Override
    public String getPkList(String schema, String table) {

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
            schema,
            table
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

        return String.format("""
            SELECT table_name
            FROM information_schema.tables
            WHERE table_schema = '%s'
              AND table_type = 'BASE TABLE'
            ORDER BY table_name;
            """,
            schema
        );
    }

    @Override
    public String getColumnList(String schema, String table) {
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
            schema,
            table
        );
    }

    @Override
    public String getProcedureList(String schema) {
        return String.format("""
        SELECT
          p.proname           AS procedure_name,
          pg_get_function_arguments(p.oid) AS procedure_arguments
        FROM pg_catalog.pg_proc p
        JOIN pg_catalog.pg_namespace n
          ON n.oid = p.pronamespace
        WHERE n.nspname = '%s'
          AND p.prokind = 'p'       -- 'p' = PROCEDURE, 'f' = FUNCTION
        ORDER BY p.proname;
        """,
            schema
        );
    }

    @Override
    public String getProcedureQuery(String schema, String procedureName, List<String> params) {
        String paramList = String.join(", ", params);

        return String.format("""
            SELECT routine_name
            FROM information_schema.routines
            WHERE routine_schema = '%s'
              AND routine_name = '%s'
              AND routine_type = 'PROCEDURE'
              AND parameter_list = '%s';
            """,
            schema,
            procedureName,
            paramList
        );
    }
}

