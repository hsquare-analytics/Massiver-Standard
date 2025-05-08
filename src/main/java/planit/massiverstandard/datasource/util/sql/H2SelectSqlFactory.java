package planit.massiverstandard.datasource.util.sql;

import java.util.List;

public class H2SelectSqlFactory implements SelectSqlFactory {

    @Override
    public String getPkList(String schema, String table) {
        return String.format("""
                SELECT cc.COLUMN_NAME
                FROM INFORMATION_SCHEMA.CONSTRAINTS AS c
                JOIN INFORMATION_SCHEMA.CONSTRAINT_COLUMNS AS cc
                  ON c.CONSTRAINT_NAME = cc.CONSTRAINT_NAME
                 AND c.TABLE_SCHEMA    = '%s'         -- 스키마
                WHERE c.TABLE_NAME      = '%s'         -- 테이블
                  AND c.CONSTRAINT_TYPE = 'PRIMARY KEY'
                """,
            schema.toUpperCase(),    // H2는 기본 식별자가 대문자라면 toUpperCase 권장
            table.toUpperCase()
        );
    }

    @Override
    public String getSchemaList() {
        return """
            SELECT SCHEMA_NAME
            FROM INFORMATION_SCHEMA.SCHEMATA
            ORDER BY SCHEMA_NAME
            """;
    }

    @Override
    public String getTableList(String schema) {
        return String.format("""
                SELECT TABLE_NAME
                FROM INFORMATION_SCHEMA.TABLES
                WHERE TABLE_SCHEMA = '%s'
                  AND TABLE_TYPE = 'TABLE'
                ORDER BY TABLE_NAME
                """,
            schema.toUpperCase()
        );
    }

    @Override
    public String getColumnList(String schema, String table) {
        return String.format("""
                SELECT
                    col.column_name,\s
                    col.ordinal_position,
                    col.data_type,
                    col.character_maximum_length,
                    col.numeric_precision,
                    col.numeric_scale,
                    col.is_nullable,
                    col.column_default,
                    COALESCE(pk.is_primary, FALSE) AS is_primary_key
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
                WHERE col.table_schema = 'schema_name'
                  AND col.table_name   = 'table_name'
                ORDER BY col.ordinal_position;
                """,
            schema.toUpperCase(),
            table.toUpperCase()
        );
    }

    @Override
    public String getProcedureList(String schema) {
        // H2는 스키마명을 대문자로 관리
        String sch = schema.toUpperCase();
        return String.format("""
        SELECT
          r.ROUTINE_NAME           AS procedure_name,
          COALESCE(
            GROUP_CONCAT(
              p.PARAMETER_MODE || ' ' ||
              p.PARAMETER_NAME || ' ' ||
              p.TYPE_NAME
              ORDER BY p.ORDINAL_POSITION
              SEPARATOR ', '
            ), ''
          ) AS procedure_arguments
        FROM INFORMATION_SCHEMA.ROUTINES r
        LEFT JOIN INFORMATION_SCHEMA.PARAMETERS p
          ON p.SPECIFIC_SCHEMA = r.ROUTINE_SCHEMA
         AND p.SPECIFIC_NAME   = r.SPECIFIC_NAME
        WHERE r.ROUTINE_SCHEMA = '%s'
          AND r.ROUTINE_TYPE   = 'PROCEDURE'
        GROUP BY r.ROUTINE_NAME
        ORDER BY r.ROUTINE_NAME;
        """,
            sch
        );
    }

    @Override
    public String getProcedureQuery(String schema, String procedureName, List<String> params) {
        return String.format("""
                SELECT ROUTINE_NAME
                FROM INFORMATION_SCHEMA.ROUTINES
                WHERE ROUTINE_SCHEMA = '%s'
                  AND ROUTINE_NAME = '%s'
                  AND ROUTINE_TYPE = 'PROCEDURE'
                """,
            schema.toUpperCase(),
            procedureName.toUpperCase()
        );
    }
}


