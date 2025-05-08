package planit.massiverstandard.datasource.service;

import planit.massiverstandard.datasource.dto.response.ColumnInfoResult;
import planit.massiverstandard.datasource.dto.response.ProcedureResult;
import planit.massiverstandard.datasource.entity.DataSource;

import java.util.List;

public interface ExecuteSqlScript {

    /**
     * pk 조회
     * @param dataSource DataSource
     * @param schema 스키마
     * @param table 테이블
     * @return Primary Key 목록
     */
    List<String> getPkList(DataSource dataSource, String schema, String table);

    List<String> getSchemas(DataSource dataSource);

    List<String> getTables(DataSource dataSource, String schema);

    List<ColumnInfoResult> getColumns(DataSource dataSource, String schema, String table);

    List<ProcedureResult> getProcedures(DataSource dataSource, String schema);

    String getProcedureQuery(DataSource dataSource, String schema, String procedureName, List<String> params);
}
