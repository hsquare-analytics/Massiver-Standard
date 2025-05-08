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
            "SELECT * FROM v_catalog.columns " +
                "WHERE table_schema = '%s' AND table_name = '%s' " +
                "ORDER BY ordinal_position;",
            schema, table); //  [oai_citation:3‡Vertica Documentation](https://docs.vertica.com/23.3.x/en/sql-reference/system-tables/v-catalog-schema/columns/)
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
