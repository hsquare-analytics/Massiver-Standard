package planit.massiverstandard.log.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import planit.massiverstandard.log.dto.BatchJobInfo;
import planit.massiverstandard.log.dto.request.BatchJobLogSearchDto;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BatchJobInfoRepositoryImpl implements BatchJobInfoRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Page<BatchJobInfo> findAll(Pageable pageable, BatchJobLogSearchDto searchDto) {
        int pageSize = pageable.getPageSize(); // size
        int offset = (int) pageable.getOffset(); // page * size

        // ✅ 1. 실제 페이징된 결과 조회
        StringBuilder sql = new StringBuilder(
            """
                SELECT
                    e.JOB_EXECUTION_ID,
                    e.VERSION,
                    e.JOB_INSTANCE_ID,
                    i.JOB_NAME,
                    i.JOB_KEY,
                    e.CREATE_TIME,
                    e.START_TIME,
                    e.END_TIME,
                    e.STATUS,
                    e.EXIT_CODE,
                    e.EXIT_MESSAGE,
                    e.LAST_UPDATED
                FROM BATCH_JOB_EXECUTION e
                JOIN BATCH_JOB_INSTANCE i ON e.JOB_INSTANCE_ID = i.JOB_INSTANCE_ID
                WHERE 1=1
                """);

        // 🔹 검색 조건 추가
        sql = addCondition(sql, searchDto);

        sql.append("""
            ORDER BY CREATE_TIME DESC
                LIMIT %d OFFSET %d
            """.formatted(pageSize, offset));

        log.info("SQL: {}", sql);

        List<BatchJobInfo> content = jdbcTemplate.query(sql.toString(),
            (rs, rowNum) -> new BatchJobInfo(
                rs.getLong("job_execution_id"),
                rs.getLong("version"),
                rs.getLong("job_instance_id"),
                rs.getString("job_name"),  // 🔹 추가
                rs.getString("job_key"),   // 🔹 추가
                rs.getTimestamp("create_time").toLocalDateTime(),
                rs.getTimestamp("start_time") != null ? rs.getTimestamp("start_time").toLocalDateTime() : null,
                rs.getTimestamp("end_time") != null ? rs.getTimestamp("end_time").toLocalDateTime() : null,
                rs.getString("status"),
                rs.getString("exit_code"),
                rs.getString("exit_message"),
                rs.getTimestamp("last_updated") != null ? rs.getTimestamp("last_updated").toLocalDateTime() : null
            )
        );

        // ✅ 2. 전체 카운트 조회
        StringBuilder countSql = new StringBuilder(
            """
                SELECT COUNT(*)
                FROM BATCH_JOB_EXECUTION e
                JOIN BATCH_JOB_INSTANCE i ON e.JOB_INSTANCE_ID = i.JOB_INSTANCE_ID
                WHERE 1=1
                """);

        // 🔹 검색 조건 추가
        countSql = addCondition(countSql, searchDto);

        Integer total = jdbcTemplate.queryForObject(countSql.toString(), Integer.class);

        return new PageImpl<>(content, pageable, total);
    }

    private StringBuilder addCondition(StringBuilder sql, BatchJobLogSearchDto searchDto) {
        // 🔹 검색 조건 추가
        if (!searchDto.status().isEmpty()) {
            sql.append("AND e.STATUS IN (");
            for (int i = 0; i < searchDto.status().size(); i++) {
                sql.append("'").append(searchDto.status().get(i)).append("'");
                if (i < searchDto.status().size() - 1) {
                    sql.append(", ");
                }
            }
            sql.append(")\n");
        }

        if (searchDto.from() != null) {
            sql.append("AND e.CREATE_TIME >= '").append(searchDto.from()).append("'\n");
        }

        if (searchDto.to() != null) {
            sql.append("AND e.CREATE_TIME <= '").append(searchDto.to()).append("'\n");
        }

        return sql;
    }

}
