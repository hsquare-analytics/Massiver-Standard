package planit.massiverstandard.batch.job.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import planit.massiverstandard.columntransform.entity.ColumnTransform;
import planit.massiverstandard.unit.entity.Unit;

import java.util.stream.Collectors;

@Slf4j
public class TempTableInitializer implements StepExecutionListener {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Unit unit;

    public TempTableInitializer(NamedParameterJdbcTemplate jdbcTemplate, Unit unit) {
        this.jdbcTemplate = jdbcTemplate;
        this.unit = unit;
    }

    private boolean isOverwrite() {
        return unit.getColumnTransforms().stream().anyMatch(ColumnTransform::isOverWrite);
    }


    private String getTempTableName() {
        //todo : 한 unit이 동시에 여러개 실행될 수 있는 경우에 대한 처리 필요
        return "tmp_pk_" + unit.getTargetTable() + "_" + unit.getId().toString().replace("-", "_");
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

    private void truncateTempTable() {
        jdbcTemplate.getJdbcTemplate().execute("TRUNCATE TABLE " + getTempTableName());

        log.info("임시 테이블 {} 비움", getTempTableName());
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        if (isOverwrite()) {
            createTempTableIfNeeded();
            truncateTempTable();
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }
}
