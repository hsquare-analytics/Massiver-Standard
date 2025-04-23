package planit.massiverstandard.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import planit.massiverstandard.database.DataBase;
import planit.massiverstandard.filter.entity.DateRangeFilter;
import planit.massiverstandard.filter.entity.Filter;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.repository.UnitJpaRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UnitInitializer {

    private final UnitJpaRepository unitRepository;

    public List<Unit> init(DataBase sourceDb, DataBase targetDb) {

        List<Unit> unitList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            String unitName = "UNIT-" + (char) ('A' + i); // UNIT-A, UNIT-B, UNIT-C ... 생성
            String sourceTableName = "source_table_" + (char) ('a' + i); // source_table_a, source_table_b ... 생성
            String targetTableName = "target_table_" + (char) ('a' + i); // target_table_a, target_table_b ... 생성
            Unit unit = createUnit(unitName, sourceDb, "public", sourceTableName, targetDb, "public", targetTableName,
                Map.of(
                    "id", "id",
                    "col1", "col1",
                    "col2", "col2"
                ),
                List.of());

            unitList.add(unitRepository.save(unit));
        }

        // 필터 추가한 유닛 생성
        String unitName = "UNIT-DATA_FILETER";
        String sourceTableName = "source_table_date";
        String targetTableName = "target_table_date";
        Unit unit = createUnit(unitName, sourceDb, "public", sourceTableName, targetDb, "public", targetTableName,
            Map.of(
                "id", "id",
                "col1", "col1",
                "col2", "col2",
                "date_col", "date_col"
            ),
            List.of(createDateFilter()));
        Unit save = unitRepository.save(unit);

        return unitList;
    }

    public Unit createUnit(String name, DataBase sourceDb, String sourceSchema, String sourceTable, DataBase targetDb, String targetSchema, String targetTable, Map<String, String> columnMap, List<Filter> filters) {
        Unit build = Unit.builder()
            .name(name)
            .sourceDb(sourceDb)
            .sourceSchema(sourceSchema)
            .sourceTable(sourceTable)
            .targetDb(targetDb)
            .targetSchema(targetSchema)
            .targetTable(targetTable)
            .build();

        columnMap.forEach(build::addColumnTransform);
        filters.forEach(build::addFilter);

        return unitRepository.save(build);
    }

    private Filter createDateFilter(){
        DateRangeFilter build = DateRangeFilter.builder()
            .name("date_filter")
            .order(1)
            .columnName("date_col")
            .fixedStartDate(LocalDate.of(2023, 1, 1))
            .fixedEndDate(LocalDate.of(2023, 12, 31))
            .build();
        return build;
    }
}
