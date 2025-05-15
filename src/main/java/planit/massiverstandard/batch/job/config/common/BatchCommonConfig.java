package planit.massiverstandard.batch.job.config.common;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import planit.massiverstandard.columntransform.entity.ColumnTransform;
import planit.massiverstandard.datasource.service.FindRealDataSource;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.service.FindUnit;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class BatchCommonConfig {

    private final FindUnit findUnit;
    private final FindRealDataSource findRealDataSource;

    @Bean
    @StepScope
    public PlatformTransactionManager etlTransactionManager(
        @Value("#{jobParameters['unitId']}") String unitId
    ) {
        Unit unit = findUnit.byId(UUID.fromString(unitId));
        DataSource etlDs = findRealDataSource.getOrCreateDataSource(unit.getTargetDb());
        return new DataSourceTransactionManager(etlDs);
    }

    // 1) 컬럼 순서만 한 번 계산
//    @Bean
//    @StepScope
//    public List<String> targetColumns(@Value("#{jobParameters['unitId']}") String unitId) {
//        Unit unit = findUnit.byIdWithColumnTransform(UUID.fromString(unitId));
//        // 순서 보장된 LinkedHashMap → key 리스트로
//        LinkedHashMap<String, String> mapping = new LinkedHashMap<>();
//        unit.getColumnTransforms()
//            .forEach(c -> mapping.put(c.getTargetColumn(), c.getSourceColumn()));
//        return new ArrayList<>(mapping.keySet());
//    }

    @Bean
    @StepScope
    public List<ColumnTransform> columnTransforms(@Value("#{jobParameters['unitId']}") String unitId) {
        Unit unit = findUnit.byIdWithColumnTransform(UUID.fromString(unitId));
        return new ArrayList<>(unit.getColumnTransforms());
    }
}
