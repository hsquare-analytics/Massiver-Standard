package planit.massiverstandard.batch;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import planit.massiverstandard.DataInitializer;
import planit.massiverstandard.columntransform.dto.ColumnTransformDto;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.datasource.service.DataSourceService;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.service.UnitService;
import planit.massiverstandard.unit.dto.UnitDto;

import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringBatchTest
@ExtendWith(SpringExtension.class)
class BatchJobLauncherTest {

    @Autowired
    UnitService unitService;

    @Autowired
    BatchJobLauncher batchJobLauncher;

    @Autowired
    DataSourceService dataSourceService;

    private DataSource sourceDataSource;
    private DataSource targetDataSource;

    private JdbcTemplate sourceJdbcTemplate;
    private JdbcTemplate targetJdbcTemplate;

    private final String sourceTable = "TEST_SOURCE_TABLE2";
    private final String targetTable = "TEST_TARGET_TABLE2";

    @BeforeEach
    void setUp() {
        dataDataBaseSetUp();

        // ✅ Source Table 동적 생성
        String createSourceTableSql = """
            CREATE TABLE IF NOT EXISTS %s (
                id UUID PRIMARY KEY,
                name VARCHAR(255),
                name2 VARCHAR(255),
                amount DECIMAL(10,2)
            )
            """.formatted(sourceTable);

        sourceJdbcTemplate.execute(createSourceTableSql);

        // ✅ Target Table 동적 생성
        String createTargetTableSql = """
            CREATE TABLE IF NOT EXISTS %s (
                id UUID,
                name VARCHAR(255),
                 name2 VARCHAR(255),
                amount DECIMAL(10,2)
            )
            """.formatted(targetTable);

        targetJdbcTemplate.execute(createTargetTableSql);

        // ✅ Source Table에 테스트 데이터 삽입
        String insertDataSql =
            "INSERT INTO %s (id, name, name2, amount) VALUES ('%s', 'name1','철수', 100.0), ('%s', 'name2','영희', 200.0)"
                .formatted(sourceTable, UUID.randomUUID(), UUID.randomUUID());

        sourceJdbcTemplate.execute(insertDataSql);
    }

    void dataDataBaseSetUp() {
        DataSource sourceDataSource = new DataSource(
            "massiver-target",        // name: 데이터베이스 이름
            "h2:mem",                 // groupUnitType: 메모리 기반 H2
            "",                       // host: 필요 없음
            "",                       // port: 필요 없음
            "sa",                     // username: 기본 사용자명
            "sa",                     // password: 기본은 빈 값
            "H2 in-memory DB"         // description: 설명 추가
        );

        DataSource targetDataSource = new DataSource(
            "massiver-source",        // name: 데이터베이스 이름
            "h2:mem",                 // groupUnitType: 메모리 기반 H2
            "",                       // host: 필요 없음
            "",                       // port: 필요 없음
            "sa",                     // username: 기본 사용자명
            "sa",                     // password: 기본은 빈 값
            "H2 in-memory DB"         // description: 설명 추가
        );

        this.sourceDataSource = dataSourceService.createDataBase(sourceDataSource);
        this.targetDataSource = dataSourceService.createDataBase(targetDataSource);

        this.sourceJdbcTemplate = new JdbcTemplate(sourceDataSource.createDataSource());
        this.targetJdbcTemplate = new JdbcTemplate(targetDataSource.createDataSource());
    }

    @AfterEach
    void tearDown() {
        // ✅ Source Table 삭제
        sourceJdbcTemplate.execute("DROP TABLE IF EXISTS " + sourceTable);
        // ✅ Target Table 삭제
        targetJdbcTemplate.execute("DROP TABLE IF EXISTS " + targetTable);
    }

    @DisplayName("ETL Job 실행 테스트")
    @Test
    void testCreateJob() throws Exception {
        // 1️⃣ 테스트용 ETL Unit 생성
        UnitDto unitDto = DataInitializer.createUnitDto(
            sourceDataSource.getId(), targetDataSource.getId(),
            "public", sourceTable, "public", targetTable,
            List.of(
                new ColumnTransformDto("id", "id"),
                new ColumnTransformDto("name", "name"),
                new ColumnTransformDto("name2", "name2"),
                new ColumnTransformDto("amount", "amount")
            ),
            List.of()
        );
        Unit unit = unitService.createUnit(unitDto);

        UUID unitId = unit.getId();

        batchJobLauncher.runBatchJob(unitId);

        // 4️⃣ 실행 결과 검증
        // ✅ Target Table에 데이터가 정상적으로 삽입되었는지 확인
        String countSql = "SELECT COUNT(*) FROM %s".formatted(targetTable);
        int count = targetJdbcTemplate.queryForObject(countSql, Integer.class);

        Assertions.assertThat(count).isEqualTo(2);
    }

    @DisplayName("ETL 컬럼 매핑 테스트 - 다른 컬럼명")
    @Test
    void testColumnMapping() throws Exception {
        // 1️⃣ 테스트용 ETL Unit 생성
        UnitDto unitDto = DataInitializer.createUnitDto(
            sourceDataSource.getId(), targetDataSource.getId(),
            "public", sourceTable, "public", targetTable,
            List.of(
                new ColumnTransformDto("id", "id"),
                new ColumnTransformDto("name", "name2"),
                new ColumnTransformDto("name2", "name"),
                new ColumnTransformDto("amount", "amount")
            ),
            List.of()
        );
        Unit unit = unitService.createUnit(unitDto);

        UUID unitId = unit.getId();

        batchJobLauncher.runBatchJob(unitId);

        // 4️⃣ 실행 결과 검증
        // 다른 컬럼명으로 매핑한 경우 amount 컬럼에 name 컬럼 값이 삽입되었는지 확인
        String selectSql = "SELECT name FROM %s".formatted(targetTable);
        List<String> amounts = targetJdbcTemplate.queryForList(selectSql, String.class);

        Assertions.assertThat(amounts).containsExactly("철수", "영희");
    }

}
