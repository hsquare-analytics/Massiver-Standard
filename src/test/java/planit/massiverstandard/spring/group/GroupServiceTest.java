package planit.massiverstandard.spring.group;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import planit.massiverstandard.DataInitializer;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.datasource.service.DataSourceService;
import planit.massiverstandard.group.service.GroupService;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.service.UnitService;

import java.util.List;

@SpringBootTest
class GroupServiceTest {

    @Autowired
    DataSourceService dataSourceService;

    DataSource sourceDataSource;
    DataSource targetDataSource;

    @Autowired
    UnitService unitService;

    Unit unit;

    @Autowired
    GroupService groupService;

    @BeforeEach
    void setUp() {
        DataSource dataSourceRequest = DataInitializer.createDataBase();
        DataSource dataSource = dataSourceService.createDataBase(dataSourceRequest);
        this.sourceDataSource = dataSource;
        this.targetDataSource = dataSource;

        this.unit = unitService.createUnit(DataInitializer.createUnitDto(
            sourceDataSource.getId(),
            targetDataSource.getId(),
            "public",
            "source_table",
            "public",
            "target_table",
            List.of(),
            List.of()
        ));
    }

}
