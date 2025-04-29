package planit.massiverstandard.initializer;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.datasource.entity.DataSourceType;
import planit.massiverstandard.initializer.dto.DataSourceProperties;
import planit.massiverstandard.unit.entity.Unit;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@Profile({"local-h2", "dev", "local-postgre"})
@RequiredArgsConstructor
public class TotalInitializer {

    private final DatabaseInitializer databaseInitializer;
    private final SqlDataInitializer sqlDataInitializer;
    private final UnitInitializer unitInitializer;
    private final GroupInitializer groupInitializer;
    private final DataSourceProperties props;

    //프로파일 가져오기
    private final Environment environment;

    private DataSourceType resolveDataSourceType(String profile) {
        return switch (profile) {
            case "local-h2" -> DataSourceType.H2_TCP;
            case "local-postgre" -> DataSourceType.POSTGRESQL;
            default -> throw new IllegalArgumentException("지원하지 않는 프로파일: " + profile);
        };
    }

    private String resolveSqlScript(String profile) {
        return switch (profile) {
            case "local-h2" -> "sql/source.sql";
            case "local-postgre" -> "sql/source_postgre.sql";
            default -> throw new IllegalArgumentException("지원하지 않는 프로파일: " + profile);
        };
    }

    @PostConstruct
    @Transactional
    public void init() {

        if (!props.isEnabled()) {
            log.info("[❗️데이터 초기화] 데이터 초기화가 비활성화되었습니다.");
            return;
        }

        DataSource sourceDb;
        DataSource targetDb;

        // application startup 또는 @PostConstruct 에서
        String[] activeProfiles = environment.getActiveProfiles();

        sourceDb = databaseInitializer.init(
            "massiver-source",
            resolveDataSourceType(activeProfiles[0]),
            props.getSource().getDatabase(),
            props.getSource().getHost(),
            props.getSource().getPort(),
            props.getSource().getUsername(),
            props.getSource().getPassword()
        );
        targetDb = databaseInitializer.init(
            "massiver-target",
            resolveDataSourceType(activeProfiles[0]),
            props.getTarget().getDatabase(),
            props.getTarget().getHost(),
            props.getTarget().getPort(),
            props.getTarget().getUsername(),
            props.getTarget().getPassword()
        );

        String sourceSql = resolveSqlScript(activeProfiles[0]);
        String targetSql = resolveSqlScript(activeProfiles[0]);

        sqlDataInitializer.init(sourceDb, sourceSql);
        sqlDataInitializer.init(targetDb, targetSql);
        log.info("[❗️데이터 초기화] SQL 초기화 완료");

        List<Unit> units = unitInitializer.init(sourceDb, targetDb);
        log.info("[❗️데이터 초기화] UNIT 초기화 완료");

        groupInitializer.init(units);
        log.info("[❗️데이터 초기화] GROUP 초기화 완료");

    }
}
