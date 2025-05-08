package planit.massiverstandard.datasource.util.sql;

import java.util.List;

/**
 * 설명: SelectSqlFactory 인터페이스
 * 작성일: 2025. 04. 28.
 * 작성자: Mason
 */
public interface SelectSqlFactory {

    /**
     * 테이블의 Primary Key 목록을 조회하는 SQL을 생성한다.
     * @param schema 스키마 이름
     * @param table 테이블 이름
     * @return Primary Key 목록을 조회하는 SQL
     */
    String getPkList(String schema, String table);

    /**
     * 스키마 목록을 조회하는 SQL을 생성한다.
     * @return 스키마 목록을 조회하는 SQL
     */
    String getSchemaList();

    /**
     * 테이블 목록을 조회하는 SQL을 생성한다.
     * @param schema 스키마 이름
     * @return 테이블 목록을 조회하는 SQL
     */
    String getTableList(String schema);

    /**
     * 테이블의 컬럼 목록을 조회하는 SQL을 생성한다.
     * @param schema 스키마 이름
     * @param table 테이블 이름
     * @return 컬럼 목록을 조회하는 SQL
     */
    String getColumnList(String schema, String table);

    /**
     * 프로시저 목록을 조회하는 SQL을 생성한다.
     * @param schema 스키마 이름
     * @return 프로시저 목록을 조회하는 SQL
     */
    String getProcedureList(String schema);

    /**
     * 프로시저의 SQL을 생성한다.
     * @param schema 스키마 이름
     * @param procedureName 프로시저 이름
     * @param params 프로시저의 파라미터 목록
     * @return 프로시저의 SQL
     */
    String getProcedureQuery(String schema, String procedureName, List<String> params);
}
