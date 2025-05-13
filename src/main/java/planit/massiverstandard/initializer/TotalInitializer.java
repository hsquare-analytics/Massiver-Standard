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
import planit.massiverstandard.datasource.service.FindDataSource;
import planit.massiverstandard.exception.common.UnsupportedProfileException;
import planit.massiverstandard.initializer.dto.DataSourceProperties;
import planit.massiverstandard.unit.entity.Unit;

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
    private final FindDataSource findDataSource;

    //프로파일 가져오기
    private final Environment environment;

    private DataSourceType resolveDataSourceType(String profile) {
        return switch (profile) {
            case "local-h2" -> DataSourceType.H2_TCP;
            case "local-postgre" -> DataSourceType.POSTGRESQL;
            default -> throw new UnsupportedProfileException("지원하지 않는 프로파일: " + profile);
        };
    }

    private String resolveSqlScript(String profile, String type) {
        return switch (profile) {
            case "local-h2" -> "sql/%s.sql".formatted(type);
            case "local-postgre" -> "sql/%s_postgre.sql".formatted(type);
            default -> throw new UnsupportedProfileException("지원하지 않는 프로파일: " + profile);
        };
    }

    @PostConstruct
    @Transactional("transactionManager")
    public void init() {

        // application startup 또는 @PostConstruct 에서
        String[] activeProfiles = environment.getActiveProfiles();

        if (props.getEnabled().isDomain()) {

            DataSource sourceDb = databaseInitializer.init(
                "massiver-source",
                resolveDataSourceType(activeProfiles[0]),
                props.getSource().getDatabase(),
                props.getSource().getHost(),
                props.getSource().getPort(),
                props.getSource().getUsername(),
                props.getSource().getPassword()
            );
            DataSource targetDb = databaseInitializer.init(
                "massiver-target",
                resolveDataSourceType(activeProfiles[0]),
                props.getTarget().getDatabase(),
                props.getTarget().getHost(),
                props.getTarget().getPort(),
                props.getTarget().getUsername(),
                props.getTarget().getPassword()
            );

            List<Unit> units = unitInitializer.init(sourceDb, targetDb);
            log.info("[❗️데이터 초기화] UNIT 초기화 완료");

            groupInitializer.init(units);
            log.info("[❗️데이터 초기화] GROUP 초기화 완료");

        }

        if (props.getEnabled().isSql()) {

            DataSource sourceDb = findDataSource.byName("massiver-source");
            DataSource targetDb = findDataSource.byName("massiver-target");

            String sourceSql = resolveSqlScript(activeProfiles[0], "source");
            String targetSql = resolveSqlScript(activeProfiles[0], "target");

            sqlDataInitializer.init(sourceDb, sourceSql);
            sqlDataInitializer.init(targetDb, targetSql);
            log.info("[❗️데이터 초기화] SQL 초기화 완료");

        }


    }
}
