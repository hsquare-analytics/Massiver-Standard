package planit.massiverstandard.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import planit.massiverstandard.columntransform.ColumnTransform;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.filter.entity.DateRangeFilter;
import planit.massiverstandard.filter.entity.Filter;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.entity.UnitType;
import planit.massiverstandard.unit.repository.UnitRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UnitInitializer {

    private final UnitRepository unitRepository;

    public List<Unit> init(DataSource sourceDb, DataSource targetDb) {

        List<Unit> unitList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            String unitName = "UNIT-" + (char) ('A' + i); // UNIT-A, UNIT-B, …
            String sourceTableName = "SOURCE_TABLE_" + (char) ('A' + i); // SOURCE_TABLE_A, SOURCE_TABLE_B, …
            String targetTableName = "TARGET_TABLE_" + (char) ('A' + i); // TARGET_TABLE_A, TARGET_TABLE_B, …

            Unit unit = createUnit(unitName, sourceDb, "PUBLIC", sourceTableName, targetDb, "PUBLIC", targetTableName,
                List.of(
                    ColumnTransform.builder()
                        .sourceColumn("id")
                        .targetColumn("id")
                        .isOverWrite(false)
                        .targetColumnType("BIGINT")
                        .build(),
                    ColumnTransform.builder()
                        .sourceColumn("col1")
                        .targetColumn("col1")
                        .isOverWrite(false)
                        .targetColumnType("VARCHAR(255)")
                        .build(),
                    ColumnTransform.builder()
                        .sourceColumn("col2")
                        .targetColumn("col2")
                        .isOverWrite(false)
                        .targetColumnType("VARCHAR(255)")
                        .build()
                ),
                List.of());

            unitList.add(unitRepository.save(unit));
        }

        // 필터 추가한 유닛 생성
        String unitName = "UNIT-DATA_FILETER";
        String sourceTableName = "SOURCE_TABLE_date";
        String targetTableName = "TARGET_TABLE_date";
        Unit unit = createUnit(unitName, sourceDb, "PUBLIC", sourceTableName, targetDb, "PUBLIC", targetTableName,
            List.of(
                ColumnTransform.builder()
                    .sourceColumn("id")
                    .targetColumn("id")
                    .isOverWrite(false)
                    .targetColumnType("BIGINT")
                    .build(),
                ColumnTransform.builder()
                    .sourceColumn("col1")
                    .targetColumn("col1")
                    .isOverWrite(false)
                    .targetColumnType("VARCHAR(255)")
                    .build(),
                ColumnTransform.builder()
                    .sourceColumn("col2")
                    .targetColumn("col2")
                    .isOverWrite(false)
                    .targetColumnType("VARCHAR(255)")
                    .build(),
                ColumnTransform.builder()
                    .sourceColumn("date_col")
                    .targetColumn("date_col")
                    .isOverWrite(false)
                    .targetColumnType("DATE")
                    .build()
            ),
            List.of(createDateFilter()));
        Unit save = unitRepository.save(unit);

        return unitList;
    }

    public Unit createUnit(String name, DataSource sourceDb, String sourceSchema, String sourceTable, DataSource targetDb, String targetSchema, String targetTable, List<ColumnTransform> columnTransformList, List<Filter> filters) {
        Unit build = Unit.builder()
            .name(name)
            .type(UnitType.NORMAL)
            .sourceDb(sourceDb)
            .sourceSchema(sourceSchema)
            .sourceTable(sourceTable)
            .targetDb(targetDb)
            .targetSchema(targetSchema)
            .targetTable(targetTable)
            .build();

        build.addColumnTransform(columnTransformList);

        filters.forEach(build::addFilter);

        return unitRepository.save(build);
    }

    private Filter createDateFilter() {
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
