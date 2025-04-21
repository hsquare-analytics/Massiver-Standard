package planit.massiverstandard.initializer;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;
import planit.massiverstandard.database.DataBase;
import planit.massiverstandard.group.dto.response.GroupResultDto;
import planit.massiverstandard.unit.entity.Unit;

import java.util.List;

@Slf4j
@Configuration
@Profile({"local-h2", "dev"})
@RequiredArgsConstructor
public class TotalInitializer {

    private final DatabaseInitializer databaseInitializer;
    private final SqlDataInitializer sqlDataInitializer;
    private final UnitInitializer unitInitializer;
    private final GroupInitializer groupInitializer;

    //프로파일 가져오기
    private final Environment environment;

    @PostConstruct
    @Transactional
    public void init() {

        log.info("[❗️데이터 초기화] DATABASE 초기화 완료");
        // 현재 활성화된 profile 배열
        String[] activeProfiles = environment.getActiveProfiles();

        DataBase sourceDb;
        DataBase targetDb;

        if (List.of(activeProfiles).contains("local-h2")) {
            sourceDb = databaseInitializer.init("~/massiver-source", "h2:tcp", "localhost", "9092", "sa", "sa", "sourceDb");
            targetDb = databaseInitializer.init("~/massiver-target", "h2:tcp", "localhost", "9092", "sa", "sa", "targetDb");
            log.info("[❗️데이터 초기화] 현재 프로파일 : local-h2");
        } else if (List.of(activeProfiles).contains("dev")) {
            sourceDb = databaseInitializer.init("mem:sourceDb", "h2:mem", "", "", "sa", "", "sourceDb");
            targetDb = databaseInitializer.init("mem:targetDb", "h2:mem", "", "", "sa", "", "targetDb");
            log.info("[❗️데이터 초기화] 현재 프로파일 : dev");
        } else {
            throw new IllegalArgumentException("지원하지 않는 프로파일입니다.");
        }

        sqlDataInitializer.init(sourceDb, "sql/source.sql");
        sqlDataInitializer.init(targetDb, "sql/target.sql");
        log.info("[❗️데이터 초기화] SQL 초기화 완료");

        List<Unit> units = unitInitializer.init(sourceDb, targetDb);
        log.info("[❗️데이터 초기화] UNIT 초기화 완료");

        groupInitializer.init(units);
        log.info("[❗️데이터 초기화] GROUP 초기화 완료");

    }
}
