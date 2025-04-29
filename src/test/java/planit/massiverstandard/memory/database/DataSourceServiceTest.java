package planit.massiverstandard.memory.database;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import planit.massiverstandard.data.DataSourceMemoryRepository;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.datasource.service.DataSourceService;
import planit.massiverstandard.datasource.repository.DataSourceRepository;

class DataSourceServiceTest {

    DataSourceService dataSourceService;

    DataSourceRepository dataSourceRepository;

    @BeforeEach
    public void setUp() {
        dataSourceRepository = new DataSourceMemoryRepository();
        dataSourceService = new DataSourceService(dataSourceRepository);
    }

    @AfterEach
    public void tearDown() {
        dataSourceRepository.deleteAll();
    }

    @Test
    void createDataBase() {
        // given
        DataSource h2Database = new DataSource(
                "massiver-target",        // name: 데이터베이스 이름
                "h2:mem",                 // groupUnitType: 메모리 기반 H2
                "",                       // host: 필요 없음
                "",                       // port: 필요 없음
                "sa",                     // username: 기본 사용자명
                "sa",                     // password: 기본은 빈 값
                "H2 in-memory DB"         // description: 설명 추가
        );

        // when
        DataSource dataSource = dataSourceService.createDataBase(h2Database);

        // then
        Assertions.assertThat(dataSource).isNotNull();
        Assertions.assertThat(dataSource.getId()).isNotNull();
        Assertions.assertThat(dataSourceRepository.findAll()).hasSize(1);
    }

    @Test
    void findAll() {
        // given
        DataSource h2Database = new DataSource(
                "massiver-target",        // name: 데이터베이스 이름
                "h2:mem",                 // groupUnitType: 메모리 기반 H2
                "",                       // host: 필요 없음
                "",                       // port: 필요 없음
                "sa",                     // username: 기본 사용자명
                "sa",                     // password: 기본은 빈 값
                "H2 in-memory DB"         // description: 설명 추가
        );

        DataSource h2Database2 = new DataSource(
                "massiver-source",        // name: 데이터베이스 이름
                "h2:mem",                 // groupUnitType: 메모리 기반 H2
                "",                       // host: 필요 없음
                "",                       // port: 필요 없음
                "sa",                     // username: 기본 사용자명
                "sa",                     // password: 기본은 빈 값
                "H2 in-memory DB"         // description: 설명 추가
        );

        // when
        dataSourceService.createDataBase(h2Database);
        dataSourceService.createDataBase(h2Database2);

        // then
        Assertions.assertThat(dataSourceService.findAll()).hasSize(2);
    }

    @Test
    void findById() {
        // given
        DataSource h2Database = new DataSource(
                "massiver-target",        // name: 데이터베이스 이름
                "h2:mem",                 // groupUnitType: 메모리 기반 H2
                "",                       // host: 필요 없음
                "",                       // port: 필요 없음
                "sa",                     // username: 기본 사용자명
                "sa",                     // password: 기본은 빈 값
                "H2 in-memory DB"         // description: 설명 추가
        );

        // when
        DataSource dataSource = dataSourceService.createDataBase(h2Database);

        // then
        Assertions.assertThat(dataSourceService.findById(dataSource.getId())).isNotNull();
    }
}
