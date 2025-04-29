package planit.massiverstandard.memory.unit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import planit.massiverstandard.DataInitializer;
import planit.massiverstandard.data.DataSourceMemoryRepository;
import planit.massiverstandard.unit.repository.UnitMemoryRepository;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.datasource.service.DataSourceService;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.repository.UnitRepository;

class UnitTest {


    private UnitRepository unitRepository;
    private DataSourceService dataSourceService;
    private DataSourceMemoryRepository dataBaseRepository;

    @BeforeEach
    void setUp() {
        unitRepository = new UnitMemoryRepository(); // UnitRepository의 구현체를 사용하여 초기화

        dataBaseRepository = new DataSourceMemoryRepository();
        dataSourceService = new DataSourceService(dataBaseRepository);

        dataDataBaseSetUp();
    }

    void dataDataBaseSetUp() {
        DataSource sourceDataSource = DataInitializer.createDataBase();
        DataSource targetDataSource = DataInitializer.createDataBase();

        sourceDataSource = dataSourceService.createDataBase(sourceDataSource);
        targetDataSource = dataSourceService.createDataBase(targetDataSource);
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
