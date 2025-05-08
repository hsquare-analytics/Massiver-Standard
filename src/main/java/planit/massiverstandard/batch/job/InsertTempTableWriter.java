package planit.massiverstandard.batch.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;
import planit.massiverstandard.columntransform.ColumnTransform;
import planit.massiverstandard.unit.entity.Unit;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class InsertTempTableWriter implements ItemWriter<Map<String, Object>>, InitializingBean {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final String targetSchema;
    private final String targetTable;
    private final List<ColumnTransform> overWriteKeyColumns; // 타겟 테이블의 PK 컬럼 이름 목록 (Unit에서 받아옴)
    private final Unit unit;

    @Override
    public void afterPropertiesSet() {
        createTempTableIfNeeded(); // ⬅ 이 시점에 1회 실행
    }

    private void createTempTableIfNeeded() {

        String collect = unit.getColumnTransforms().stream()
            .map(col -> col.getTargetColumn() + " " + col.getTargetColumnType())
            .collect(Collectors.joining(", "));

        String ddl = "CREATE TABLE IF NOT EXISTS " + getTempTableName() + " ("
            + collect
            + ") ;";

        jdbcTemplate.getJdbcTemplate().execute(ddl);

        log.info("임시 테이블 {} 생성 완료", getTempTableName());
    }

    private String getTempTableName() {
        //todo : 한 unit이 동시에 여러개 실행될 수 있는 경우에 대한 처리 필요
        return "tmp_pk_" + targetTable + "_" + unit.getId().toString().replace("-", "_");
    }

    private void truncateTempTable() {
        jdbcTemplate.getJdbcTemplate().execute("TRUNCATE TABLE " + getTempTableName());

        log.info("임시 테이블 {} 비움", getTempTableName());
    }

    // Constructor injection
    public InsertTempTableWriter(javax.sql.DataSource targetDataSource, Unit unit) {
        Assert.notNull(targetDataSource, "targetDataSource must be provided");
        Assert.notNull(unit, "unit must be provided");

        this.unit = unit;
        this.jdbcTemplate = new NamedParameterJdbcTemplate(targetDataSource);
        this.targetSchema = unit.getTargetSchema();
        this.targetTable = unit.getTargetTable();
        this.overWriteKeyColumns = unit.getColumnTransforms().stream()
            .filter(ColumnTransform::isOverWrite)
            .toList();
    }

    private void insertIntoTempTable(List<? extends Map<String, Object>> items) {

        List<String> columnNames = this.unit.getColumnTransforms().stream()
            .map(ColumnTransform::getTargetColumn)
            .toList();

        List<MapSqlParameterSource> batchParams = items.stream().map(item -> {
            MapSqlParameterSource param = new MapSqlParameterSource();
            columnNames.forEach(pkCol -> param.addValue(pkCol, item.get(pkCol)));
            return param;
        }).toList();

        String columns = String.join(", ", columnNames);
        String values = columnNames.stream()
            .map(col -> ":" + col)
            .collect(Collectors.joining(", "));

        String insertSql = "INSERT INTO " + getTempTableName() + " (" + columns + ") VALUES (" + values + ")";
        jdbcTemplate.batchUpdate(insertSql, batchParams.toArray(new MapSqlParameterSource[0]));

        log.info("임시 테이블 {}에 {}건 INSERT 완료", getTempTableName(), items.size());
    }

    private void deleteFromTargetByJoin() {
        String joinCondition = overWriteKeyColumns.stream()
            .map(col -> "t." + col.getTargetColumn() + " = tmp." + col.getTargetColumn())
            .collect(Collectors.joining(" AND "));

        String deleteSql = String.format("""
                DELETE FROM %s.%s t
                USING %s tmp
                WHERE %s
            """, targetSchema, targetTable, getTempTableName(), joinCondition);

        log.info("DELETE SQL: {}", deleteSql);

        jdbcTemplate.update(deleteSql, new MapSqlParameterSource());

        log.info("타겟 테이블 {}에서 {}를 삭제했습니다.", targetTable, getTempTableName());
    }

    @Override
    public void write(Chunk<? extends Map<String, Object>> chunk) throws Exception { // Spring Batch 5: Chunk 사용
        if (chunk == null || chunk.isEmpty()) {
            return;
        }

        List<? extends Map<String, Object>> items = chunk.getItems();

//        // Processor에서 컬럼 변환이 완료되어 Item 맵의 키는 타겟 컬럼명이라고 가정합니다.
//
//        // 1. 청크에 포함된 Item들의 덮어쓰기 값들을 추출
//        List<MapSqlParameterSource> deleteParameters = new ArrayList<>();
//        List<Object> singleKeyValues = (overWriteKeyColumns.size() == 1) ? new ArrayList<>() : null; // 단일키 최적화
//
//        for (Map<String, Object> item : items) {
//            if (overWriteKeyColumns.size() == 1) {
//                // 단일키인 경우: PK 값을 바로 리스트에 추가
//                singleKeyValues.add(item.get(overWriteKeyColumns.getFirst().getTargetColumn()));
//            } else {
//                // 복합키인 경우: 각 item별로 PK 컬럼 값을 MapSqlParameterSource에 담아 리스트에 추가
//                MapSqlParameterSource pkParamSource = new MapSqlParameterSource();
//                List<String> overWriteKeyColumnNames = this.overWriteKeyColumns.stream()
//                    .map(ColumnTransform::getTargetColumn)
//                    .toList();
//                for (String pkCol : overWriteKeyColumnNames) {
//                    // Item 맵에서 타겟 PK 컬럼 이름으로 값 가져옴
//                    pkParamSource.addValue(pkCol, item.get(pkCol));
//                }
//                deleteParameters.add(pkParamSource);
//            }
//        }
//
//        // 2. 타겟 테이블에서 해당 PK의 행들을 삭제 (Bulk DELETE)
//        if (!items.isEmpty()) {
//            String deleteSql;
//            if (overWriteKeyColumns.size() == 1) {
//                // 단일키 DELETE SQL (IN 절 사용)
//                // WHERE pk_column IN (:pkValues)
//                deleteSql = "DELETE FROM " + targetSchema + "." + targetTable +
//                    " WHERE " + overWriteKeyColumns.getFirst().getTargetColumn() + " IN (:pkValues)";
//
//                log.info(deleteSql);
//
//                MapSqlParameterSource deleteParam = new MapSqlParameterSource("pkValues", singleKeyValues); // pkValues 라는 파라미터 이름 사용
//                // IN 절에 너무 많은 값이 들어가지 않도록 분할 필요할 수 있음 (DB 제약 확인)
//                if (!singleKeyValues.isEmpty()) {
//                    jdbcTemplate.update(deleteSql, deleteParam);
//                }
//
//            } else {
//                // 복합키 DELETE SQL (WHERE (pk1 = :pk1_i AND pk2 = :pk2_i) OR (...) ...) 형태 비효율적
//                // 복합키 삭제 시에는 일반적으로 임시 테이블에 PK 값 목록을 넣고 JOIN 또는 EXISTS/IN 서브쿼리로 삭제하는 방식 사용
//                // 여기서는 간단하게 청크별 PK로 삭제 (효율성은 떨어질 수 있음)
//                deleteSql = "DELETE FROM " + targetSchema + "." + targetTable +
//                    " WHERE " +
//                    overWriteKeyColumns.stream() // 각 PK 컬럼에 대해 컬럼명 = :컬럼명 조건 생성
//                        .map(pkCol -> pkCol.getTargetColumn() + " = :" + pkCol.getTargetColumn())
//                        .collect(Collectors.joining(" AND ")); // 복합키 조건 조합 (예: WHERE id = :id AND seq = :seq)
//
//                // batchUpdate를 사용하여 청크 내 각 item의 PK 조건으로 개별 DELETE 실행 (비효율적)
//                // 더 나은 복합키 벌크 삭제 전략 고려 필요
//                // for (MapSqlParameterSource paramSource : deleteParameters) {
//                //    jdbcTemplate.update(deleteSql, paramSource);
//                // }
//                // 복합키 batch update delete는 spring-data JDBC extensions 등 활용 고려
//
//                // 또는: 임시 테이블을 사용한 삭제 전략
//                // 1. 임시 테이블 생성 (청크의 PK 담을 구조)
//                // 2. 청크의 PK 값들을 임시 테이블에 배치 INSERT
//                // 3. 대상 테이블과 임시 테이블을 JOIN 하여 DELETE 실행
//                // 4. 임시 테이블 DROP
//                //todo : 구현해야한다..
//
//                throw new UnsupportedOperationException("Complex PK Delete not implemented with efficient bulk strategy.");
//            }
//        }
//
//
//        // 3. 청크의 Item들을 타겟 테이블에 삽입 (Batch INSERT)
//        // Processor에서 반환된 Item 맵의 키가 타겟 컬럼명이라고 가정합니다.
//        String insertSql = buildInsertSql(items.get(0).keySet()); // Item 맵의 키셋으로 SQL 컬럼 목록 동적 생성
//
//        List<MapSqlParameterSource> insertParameters = new ArrayList<>();
//        for (Map<String, Object> item : items) { // item은 Processor에서 반환된 변환된 맵
//            MapSqlParameterSource parameterSource = new MapSqlParameterSource();
//            // 변환된 맵(item)의 키(타겟컬럼명)와 값을 그대로 사용하여 파라미터 설정
//            item.forEach(parameterSource::addValue);
//            insertParameters.add(parameterSource);
//        }
//
//        if (!insertParameters.isEmpty()) {
//            jdbcTemplate.batchUpdate(insertSql, insertParameters.toArray(new MapSqlParameterSource[0]));
//        }



        //******************************
//        if (!items.isEmpty()) {
//            truncateTempTable(); // 1. temp 테이블 비움
//            insertIntoTempTable(items); // 2. PK만 INSERT
//            deleteFromTargetByJoin(); // 3. DELETE FROM JOIN
//        }
//
//        // 4. INSERT (기존 insert 로직 그대로)
//        String insertSql = buildInsertSql(items.get(0).keySet());
//        List<MapSqlParameterSource> insertParameters = new ArrayList<>();
//        for (Map<String, Object> item : items) {
//            MapSqlParameterSource parameterSource = new MapSqlParameterSource();
//            item.forEach(parameterSource::addValue);
//            insertParameters.add(parameterSource);
//        }
//
//        jdbcTemplate.batchUpdate(insertSql, insertParameters.toArray(new MapSqlParameterSource[0]));



        if (!items.isEmpty()) {
//            truncateTempTable();
            insertIntoTempTable(items); // 2. 모든 데이터를 임시 테이블에 삽입

//            // 3. MERGE 구문 실행
//            String mergeSql = buildUpsertSql(items.getFirst().keySet());
//            jdbcTemplate.update(mergeSql, new MapSqlParameterSource());
//
//            log.info("타겟 테이블 {}에 MERGE 완료", targetTable);
        }
    }

    // INSERT SQL 문 동적 생성 헬퍼 메소드 (첫 번째 Item의 키셋으로 컬럼 목록 파악)
    private String buildInsertSql(Set<String> targetColumns) {
        String columns = String.join(", ", targetColumns);
        String values = targetColumns.stream()
            .map(col -> ":" + col) // Named Parameter 생성
            .collect(Collectors.joining(", "));

        return "INSERT INTO " + targetSchema + "." + targetTable + " (" + columns + ") VALUES (" + values + ")";
    }

    // afterPropertiesSet() 등 필요한 초기화 메소드 구현 가능

//    private String buildMergeSql(java.util.Set<String> targetColumns) {
//        String columns = String.join(", ", targetColumns);
//        String values = targetColumns.stream()
//            .map(col -> "source." + col)
//            .collect(Collectors.joining(", "));
//
//        String joinCondition = overWriteKeyColumns.stream()
//            .map(col -> "target." + col.getTargetColumn() + " = source." + col.getTargetColumn())
//            .collect(Collectors.joining(" AND "));
//
//        String updateSetClause = targetColumns.stream()
//            .filter(col -> !overWriteKeyColumns.stream().map(ColumnTransform::getTargetColumn).toList().contains(col))
//            .map(col -> "target." + col + " = source." + col)
//            .collect(Collectors.joining(", "));
//
//
//        String insertColumns = String.join(", ", targetColumns);
//        String insertValues = targetColumns.stream()
//            .map(col -> "source." + col)
//            .collect(Collectors.joining(", "));
//
//        return String.format("""
//            MERGE INTO %s.%s AS target
//            USING %s AS source
//            ON %s
//            WHEN MATCHED THEN
//                UPDATE SET %s
//            WHEN NOT MATCHED THEN
//                INSERT (%s) VALUES (%s)
//            """, targetSchema, targetTable, getTempTableName(), joinCondition, updateSetClause, insertColumns, insertValues);
//    }

//    /**
//     * @param targetColumns 타겟 테이블의 전체 컬럼명 세트
//     * @return PostgreSQL용 UPSERT(INSERT … ON CONFLICT) SQL
//     */
//    private String buildUpsertSql(Set<String> targetColumns) {
//        // 1) 컬럼 리스트: (id, id2, id3, col1, col2, date_col)
//        String columns = String.join(", ", targetColumns);
//
//        // 2) VALUES 절의 소스 컬럼들: (source.id, source.id2, …)
//        String sourceValues = targetColumns.stream()
//            .map(col -> "source." + col)
//            .collect(Collectors.joining(", "));
//
//        // 3) 충돌 키(복합 PK) 조건: (id, id2, id3)
//        String conflictKeys = overWriteKeyColumns.stream()
//            .map(ColumnTransform::getTargetColumn)
//            .collect(Collectors.joining(", "));
//
//        // 4) UPDATE SET 절: 변경 대상 컬럼만
//        List<String> pkCols = overWriteKeyColumns.stream()
//            .map(ColumnTransform::getTargetColumn)
//            .toList();
//        String updateSet = targetColumns.stream()
//            .filter(col -> !pkCols.contains(col))            // PK 컬럼 제외
//            .map(col -> col + " = EXCLUDED." + col)           // EXCLUDED.<col> 사용
//            .collect(Collectors.joining(", "));
//
//        // 최종 UPSERT SQL 생성
//        return String.format("""
//                        INSERT INTO %s.%s (%s)
//                        SELECT %s
//                        FROM %s AS source
//                        ON CONFLICT (%s)
//                        DO UPDATE SET %s
//                        """,
//            targetSchema,
//            targetTable,
//            columns,
//            sourceValues,
//            getTempTableName(),
//            conflictKeys,
//            updateSet
//        );
//    }
//
//    private void insertIntoTempTable(List<? extends Map<String, Object>> items) {
//        List<MapSqlParameterSource> batchParams = items.stream().map(item -> {
//            MapSqlParameterSource param = new MapSqlParameterSource();
//            item.forEach(param::addValue); // 모든 컬럼을 파라미터에 추가
//            return param;
//        }).toList();
//
//        String columns = String.join(", ", items.get(0).keySet()); // 모든 컬럼 이름을 가져옴
//        String values = items.get(0).keySet().stream()
//            .map(col -> ":" + col)
//            .collect(Collectors.joining(", "));
//
//        String insertSql = "INSERT INTO " + getTempTableName() + " (" + columns + ") VALUES (" + values + ")";
//        jdbcTemplate.batchUpdate(insertSql, batchParams.toArray(new MapSqlParameterSource[0]));
//
//        log.info("임시 테이블 {}에 {}건 INSERT 완료", getTempTableName(), items.size());
//    }

}
