package planit.massiverstandard.memory.unit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import planit.massiverstandard.DataInitializer;
import planit.massiverstandard.data.DataBaseMemoryRepository;
import planit.massiverstandard.data.UnitMemoryRepository;
import planit.massiverstandard.database.DataBase;
import planit.massiverstandard.database.DataBaseService;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.repository.UnitRepository;

class UnitTest {


    private UnitRepository unitRepository;
    private DataBaseService dataBaseService;
    private DataBaseMemoryRepository dataBaseRepository;

    @BeforeEach
    void setUp() {
        unitRepository = new UnitMemoryRepository(); // UnitRepository의 구현체를 사용하여 초기화

        dataBaseRepository = new DataBaseMemoryRepository();
        dataBaseService = new DataBaseService(dataBaseRepository);

        dataDataBaseSetUp();
    }

    void dataDataBaseSetUp() {
        DataBase sourceDataBase = DataInitializer.createDataBase();
        DataBase targetDataBase = DataInitializer.createDataBase();

        sourceDataBase = dataBaseService.createDataBase(sourceDataBase);
        targetDataBase = dataBaseService.createDataBase(targetDataBase);
    }

    @AfterEach
    void tearDown() {
        unitRepository.deleteAll(); // 테스트 종료 후 저장된 데이터를 모두 삭제
        dataBaseRepository.deleteAll();
    }

    @Test
    @DisplayName("Unit 생성 테스트")
    void createUnit() {
        // Given: 새로운 Unit을 생성
        Unit unit = DataInitializer.createUnit();

        // when
        // then
        Assertions.assertThat(unit).isNotNull();
        Assertions.assertThat(unit.getName()).isEqualTo("test-unit");
    }

}
