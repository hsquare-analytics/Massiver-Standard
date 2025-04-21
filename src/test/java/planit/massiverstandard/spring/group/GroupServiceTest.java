package planit.massiverstandard.spring.group;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import planit.massiverstandard.DataInitializer;
import planit.massiverstandard.database.DataBase;
import planit.massiverstandard.database.DataBaseService;
import planit.massiverstandard.group.service.GroupService;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.service.UnitService;

import java.util.List;

@SpringBootTest
class GroupServiceTest {

    @Autowired
    DataBaseService dataBaseService;

    DataBase sourceDataBase;
    DataBase targetDataBase;

    @Autowired
    UnitService unitService;

    Unit unit;

    @Autowired
    GroupService groupService;

    @BeforeEach
    void setUp() {
        DataBase dataBaseRequest = DataInitializer.createDataBase();
        DataBase dataBase = dataBaseService.createDataBase(dataBaseRequest);
        this.sourceDataBase = dataBase;
        this.targetDataBase = dataBase;

        this.unit = unitService.createUnit(DataInitializer.createUnitDto(
            sourceDataBase.getId(),
            targetDataBase.getId(),
            "public",
            "source_table",
            "public",
            "target_table",
            List.of(),
            List.of()
        ));
    }

}
