package planit.massiverstandard.memory.database;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import planit.massiverstandard.data.DataBaseMemoryRepository;
import planit.massiverstandard.database.DataBase;
import planit.massiverstandard.database.DataBaseService;
import planit.massiverstandard.database.repository.DataBaseRepository;

class DataBaseServiceTest {

    DataBaseService dataBaseService;

    DataBaseRepository dataBaseRepository;

    @BeforeEach
    public void setUp() {
        dataBaseRepository = new DataBaseMemoryRepository();
        dataBaseService = new DataBaseService(dataBaseRepository);
    }

    @AfterEach
    public void tearDown() {
        dataBaseRepository.deleteAll();
    }

    @Test
    void createDataBase() {
        // given
        DataBase h2Database = new DataBase(
                "massiver-target",        // name: 데이터베이스 이름
                "h2:mem",                 // groupUnitType: 메모리 기반 H2
                "",                       // host: 필요 없음
                "",                       // port: 필요 없음
                "sa",                     // username: 기본 사용자명
                "sa",                     // password: 기본은 빈 값
                "H2 in-memory DB"         // description: 설명 추가
        );

        // when
        DataBase dataBase = dataBaseService.createDataBase(h2Database);

        // then
        Assertions.assertThat(dataBase).isNotNull();
        Assertions.assertThat(dataBase.getId()).isNotNull();
        Assertions.assertThat(dataBaseRepository.findAll()).hasSize(1);
    }

    @Test
    void findAll() {
        // given
        DataBase h2Database = new DataBase(
                "massiver-target",        // name: 데이터베이스 이름
                "h2:mem",                 // groupUnitType: 메모리 기반 H2
                "",                       // host: 필요 없음
                "",                       // port: 필요 없음
                "sa",                     // username: 기본 사용자명
                "sa",                     // password: 기본은 빈 값
                "H2 in-memory DB"         // description: 설명 추가
        );

        DataBase h2Database2 = new DataBase(
                "massiver-source",        // name: 데이터베이스 이름
                "h2:mem",                 // groupUnitType: 메모리 기반 H2
                "",                       // host: 필요 없음
                "",                       // port: 필요 없음
                "sa",                     // username: 기본 사용자명
                "sa",                     // password: 기본은 빈 값
                "H2 in-memory DB"         // description: 설명 추가
        );

        // when
        dataBaseService.createDataBase(h2Database);
        dataBaseService.createDataBase(h2Database2);

        // then
        Assertions.assertThat(dataBaseService.findAll()).hasSize(2);
    }

    @Test
    void findById() {
        // given
        DataBase h2Database = new DataBase(
                "massiver-target",        // name: 데이터베이스 이름
                "h2:mem",                 // groupUnitType: 메모리 기반 H2
                "",                       // host: 필요 없음
                "",                       // port: 필요 없음
                "sa",                     // username: 기본 사용자명
                "sa",                     // password: 기본은 빈 값
                "H2 in-memory DB"         // description: 설명 추가
        );

        // when
        DataBase dataBase = dataBaseService.createDataBase(h2Database);

        // then
        Assertions.assertThat(dataBaseService.findById(dataBase.getId())).isNotNull();
    }
}
