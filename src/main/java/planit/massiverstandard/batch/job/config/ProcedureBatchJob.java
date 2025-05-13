package planit.massiverstandard.batch.job.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import planit.massiverstandard.datasource.service.FindRealDataSource;
import planit.massiverstandard.unit.entity.DateRangeType;
import planit.massiverstandard.unit.entity.ProcedureParameter;
import planit.massiverstandard.unit.entity.ProcedureParameterMode;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.service.FindUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ProcedureBatchJob {

    private final FindUnit findUnit;
    private final JobRepository jobRepository;
    private final Step procedureStep;
    private final FindRealDataSource findRealDataSource;

    public Job createJob(String jobName) {
        log.info("'{}' 이름의 프로시저 호출 Job을 생성합니다", jobName);
        return new JobBuilder(jobName, jobRepository)
            .start(procedureStep)
            .incrementer(new RunIdIncrementer())
            .build();
    }

    @Bean(name = "procedureStep")
    @JobScope
    public Step procedureStep(JobRepository jobRepository,
                              @Value("#{jobParameters['unitId']}") String unitId
    ) {
        Unit unit = findUnit.byIdWithProcedureParameter(UUID.fromString(unitId));
        String targetSchema = unit.getTargetSchema();
        String procedureName = unit.getTargetTable();

        log.info("{}.{} 프로시저 호출 Step을 생성합니다", targetSchema, procedureName);

        javax.sql.DataSource dataSource = findRealDataSource.getOrCreateDataSource(unit.getTargetDb());
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
        dataSourceTransactionManager.setGlobalRollbackOnParticipationFailure(false);

        String stepName = "procedure : " + targetSchema + "." + procedureName;
        return new StepBuilder(stepName, jobRepository)
            .tasklet((contribution, chunkContext) -> {

                callStoredProcedure(jdbcTemplate, unit);

                return RepeatStatus.FINISHED;
            }, dataSourceTransactionManager)
            .build();
    }

    private void callStoredProcedure(JdbcTemplate jdbcTemplate, Unit unit) {

        String sql;

        String targetSchema = unit.getTargetSchema();
        String targetTable = unit.getTargetTable();
        List<ProcedureParameter> procedureParameters = unit.getProcedureParameters();

        if (procedureParameters.isEmpty()) {
            sql = "CALL " + targetSchema + "." + targetTable + "()";
        } else {
//            startDate = SelectSqlUtil.calculateDateByRangeType(DateRangeType.valueOf(startDateRangeType), startDate);
//            endDate = SelectSqlUtil.calculateDateByRangeType(DateRangeType.valueOf(endDateRangeType), endDate);
//            sql = "CALL " + targetSchema + "." + procedureName + "('" + startDate + "', '" + endDate + " 23:59:59')";
//            ;
            String parameterString = buildParameterString(procedureParameters);
            sql = "CALL " + targetSchema + "." + targetTable + "(" + parameterString + ")";
        }

        log.info("Stored procedure called: " + sql);

        String searchSchemaSetting = "SET SEARCH_PATH = " + targetSchema + "; ";
        jdbcTemplate.execute(searchSchemaSetting + sql);
    }

    private String buildParameterString(List<ProcedureParameter> procedureParameters) {

        List<String> parameterValues = new ArrayList<>();
        for (ProcedureParameter parameter : procedureParameters) {
            if (parameter.getMode() == ProcedureParameterMode.LITERAL) {
                parameterValues.add("'" + parameter.getValue() + "'");
            } else if (parameter.getMode() == ProcedureParameterMode.EXPRESSION) {
                String paramString = processExpressionParameter(parameter);
                parameterValues.add(paramString);
            } else {
                throw new IllegalArgumentException("Unknown parameter mode: " + parameter.getMode());
            }
        }

        return String.join(", ", parameterValues);
    }

    private String processExpressionParameter(ProcedureParameter parameter) {
        if ("date".equals(parameter.getDataType())) {
            DateRangeType dateRangeType = DateRangeType.of(parameter.getValue());
            return "'%s'".formatted(dateRangeType.getDate());
        }
        return "";
        // todo: expression 처리
    }
}
