package planit.massiverstandard.datasource.util.sql;

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

    String getSchemaList();

    String getTableList(String schema);

    String getColumnList(String schema, String table);

}
