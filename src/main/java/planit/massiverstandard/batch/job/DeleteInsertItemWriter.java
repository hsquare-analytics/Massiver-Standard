package planit.massiverstandard.batch.job;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;
import planit.massiverstandard.columntransform.ColumnTransform;
import planit.massiverstandard.unit.entity.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeleteInsertItemWriter implements ItemWriter<Map<String, Object>> {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final String targetSchema;
    private final String targetTable;
    private final List<String> overWriteKeyColumns; // 타겟 테이블의 PK 컬럼 이름 목록 (Unit에서 받아옴)

    // Constructor injection
    public DeleteInsertItemWriter(javax.sql.DataSource targetDataSource, Unit unit) {
        Assert.notNull(targetDataSource, "targetDataSource must be provided");
        Assert.notNull(unit, "unit must be provided");

        this.jdbcTemplate = new NamedParameterJdbcTemplate(targetDataSource);
        this.targetSchema = unit.getTargetSchema();
        this.targetTable = unit.getTargetTable();
        this.overWriteKeyColumns = unit.getColumnTransforms().stream()
            .filter(ColumnTransform::isOverWrite)
            .map(ColumnTransform::getTargetColumn)
            .toList();
    }

    @Override
    public void write(Chunk<? extends Map<String, Object>> chunk) throws Exception { // Spring Batch 5: Chunk 사용
        if (chunk == null || chunk.isEmpty()) {
            return;
        }

        List<? extends Map<String, Object>> items = chunk.getItems();

        // Processor에서 컬럼 변환이 완료되어 Item 맵의 키는 타겟 컬럼명이라고 가정합니다.

        // 1. 청크에 포함된 Item들의 덮어쓰기 값들을 추출
        List<MapSqlParameterSource> deleteParameters = new ArrayList<>();
        List<Object> singleKeyValues = (overWriteKeyColumns.size() == 1) ? new ArrayList<>() : null; // 단일키 최적화

        for (Map<String, Object> item : items) {
            if (overWriteKeyColumns.size() == 1) {
                // 단일키인 경우: PK 값을 바로 리스트에 추가
                singleKeyValues.add(item.get(overWriteKeyColumns.getFirst()));
            } else {
                // 복합키인 경우: 각 item별로 PK 컬럼 값을 MapSqlParameterSource에 담아 리스트에 추가
                MapSqlParameterSource pkParamSource = new MapSqlParameterSource();
                for (String pkCol : overWriteKeyColumns) {
                    // Item 맵에서 타겟 PK 컬럼 이름으로 값 가져옴
                    pkParamSource.addValue(pkCol, item.get(pkCol));
                }
                deleteParameters.add(pkParamSource);
            }
        }

        // 2. 타겟 테이블에서 해당 PK의 행들을 삭제 (Bulk DELETE)
        if (!items.isEmpty()) {
            String deleteSql;
            if (overWriteKeyColumns.size() == 1) {
                // 단일키 DELETE SQL (IN 절 사용)
                // WHERE pk_column IN (:pkValues)
                deleteSql = "DELETE FROM " + targetSchema + "." + targetTable +
                    " WHERE " + overWriteKeyColumns.getFirst() + " IN (:pkValues)";
                MapSqlParameterSource deleteParam = new MapSqlParameterSource("pkValues", singleKeyValues); // pkValues 라는 파라미터 이름 사용
                // IN 절에 너무 많은 값이 들어가지 않도록 분할 필요할 수 있음 (DB 제약 확인)
                if (singleKeyValues.size() > 0) {
                    jdbcTemplate.update(deleteSql, deleteParam);
                }

            } else {
                // 복합키 DELETE SQL (WHERE (pk1 = :pk1_i AND pk2 = :pk2_i) OR (...) ...) 형태 비효율적
                // 복합키 삭제 시에는 일반적으로 임시 테이블에 PK 값 목록을 넣고 JOIN 또는 EXISTS/IN 서브쿼리로 삭제하는 방식 사용
                // 여기서는 간단하게 청크별 PK로 삭제 (효율성은 떨어질 수 있음)
                deleteSql = "DELETE FROM " + targetSchema + "." + targetTable +
                    " WHERE " +
                    overWriteKeyColumns.stream() // 각 PK 컬럼에 대해 컬럼명 = :컬럼명 조건 생성
                        .map(pkCol -> pkCol + " = :" + pkCol)
                        .collect(Collectors.joining(" AND ")); // 복합키 조건 조합 (예: WHERE id = :id AND seq = :seq)

                // batchUpdate를 사용하여 청크 내 각 item의 PK 조건으로 개별 DELETE 실행 (비효율적)
                // 더 나은 복합키 벌크 삭제 전략 고려 필요
                // for (MapSqlParameterSource paramSource : deleteParameters) {
                //    jdbcTemplate.update(deleteSql, paramSource);
                // }
                // 복합키 batch update delete는 spring-data JDBC extensions 등 활용 고려

                // 또는: 임시 테이블을 사용한 삭제 전략
                // 1. 임시 테이블 생성 (청크의 PK 담을 구조)
                // 2. 청크의 PK 값들을 임시 테이블에 배치 INSERT
                // 3. 대상 테이블과 임시 테이블을 JOIN 하여 DELETE 실행
                // 4. 임시 테이블 DROP
                //todo : 구현해야한다..

                throw new UnsupportedOperationException("Complex PK Delete not implemented with efficient bulk strategy.");
            }
        }


        // 3. 청크의 Item들을 타겟 테이블에 삽입 (Batch INSERT)
        // Processor에서 반환된 Item 맵의 키가 타겟 컬럼명이라고 가정합니다.
        String insertSql = buildInsertSql(items.get(0).keySet()); // Item 맵의 키셋으로 SQL 컬럼 목록 동적 생성

        List<MapSqlParameterSource> insertParameters = new ArrayList<>();
        for (Map<String, Object> item : items) { // item은 Processor에서 반환된 변환된 맵
            MapSqlParameterSource parameterSource = new MapSqlParameterSource();
            // 변환된 맵(item)의 키(타겟컬럼명)와 값을 그대로 사용하여 파라미터 설정
            item.forEach(parameterSource::addValue);
            insertParameters.add(parameterSource);
        }

        if (!insertParameters.isEmpty()) {
            jdbcTemplate.batchUpdate(insertSql, insertParameters.toArray(new MapSqlParameterSource[0]));
        }
    }

    // INSERT SQL 문 동적 생성 헬퍼 메소드 (첫 번째 Item의 키셋으로 컬럼 목록 파악)
    private String buildInsertSql(java.util.Set<String> targetColumns) {
        String columns = String.join(", ", targetColumns);
        String values = targetColumns.stream()
            .map(col -> ":" + col) // Named Parameter 생성
            .collect(Collectors.joining(", "));

        return "INSERT INTO " + targetSchema + "." + targetTable + " (" + columns + ") VALUES (" + values + ")";
    }

    // afterPropertiesSet() 등 필요한 초기화 메소드 구현 가능
}
