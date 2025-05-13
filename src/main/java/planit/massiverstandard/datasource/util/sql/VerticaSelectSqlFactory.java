package planit.massiverstandard.datasource.util.sql;

import java.util.List;

public class VerticaSelectSqlFactory implements SelectSqlFactory {

    @Override
    public String getPkList(String schema, String table) {
        return String.format(
            "SELECT column_name FROM v_catalog.primary_keys " +
                "WHERE table_schema = '%s' AND table_name = '%s' AND constraint_type = 'p' " +
                "ORDER BY ordinal_position;",
            schema, table); //  [oai_citation:0‡Vertica Documentation](https://docs.vertica.com/23.3.x/en/sql-reference/system-tables/v-catalog-schema/primary-keys/)
    }

    @Override
    public String getSchemaList() {
        return "SELECT schema_name FROM v_catalog.schemata WHERE is_system_schema = false ORDER BY schema_name;"; //  [oai_citation:1‡Vertica Documentation](https://docs.vertica.com/23.3.x/en/sql-reference/system-tables/v-catalog-schema/schemata/)
    }

    @Override
    public String getTableList(String schema) {
        return String.format(
            "SELECT table_name FROM v_catalog.tables " +
                "WHERE table_schema = '%s' AND is_system_table = false " +
                "ORDER BY table_name;",
            schema); //  [oai_citation:2‡Vertica Documentation](https://docs.vertica.com/23.3.x/en/sql-reference/system-tables/v-catalog-schema/tables/)
    }

    @Override
    public String getColumnList(String schema, String table) {
        return String.format(
            "SELECT " +
                "  c.column_name, " +
                "  c.ordinal_position, " +
                "  c.data_type, " +
                "  c.character_maximum_length, " +
                "  c.numeric_precision, " +
                "  c.numeric_scale, " +
                "  c.is_nullable, " +
                "  c.column_default, " +
                "  COALESCE(pk.is_primary_key, FALSE) AS is_primary_key " +
                "FROM v_catalog.columns AS c " +
                "LEFT JOIN ( " +
                "  SELECT table_schema, table_name, column_name, TRUE AS is_primary_key " +
                "  FROM v_catalog.primary_keys " +
                ") AS pk " +
                "  ON c.table_schema = pk.table_schema " +
                " AND c.table_name   = pk.table_name " +
                " AND c.column_name  = pk.column_name " +
                "WHERE c.table_schema = '%s' " +
                "  AND c.table_name   = '%s' " +
                "ORDER BY c.ordinal_position;",
            schema, table
        );
    }

    @Override
    public String getProcedureList(String schema) {
        return String.format(
            "SELECT procedure_name, procedure_arguments FROM v_catalog.user_procedures " +
                "WHERE schema_name = '%s' " +
                "ORDER BY procedure_name;",
            schema); //  [oai_citation:4‡Vertica Documentation](https://docs.vertica.com/23.3.x/en/sql-reference/system-tables/v-catalog-schema/user-procedures/)
    }

    @Override
    public String getProcedureQuery(String schema, String procedureName, List<String> params) {
        return """
            select export_objects('','%s.%s(%s)')
            """.formatted(schema, procedureName, String.join(", ", params));
    }
}
