package planit.massiverstandard;

import planit.massiverstandard.columntransform.ColumnTransform;
import planit.massiverstandard.columntransform.dto.ColumnTransformDto;
import planit.massiverstandard.database.DataBase;
import planit.massiverstandard.filter.dto.FilterDto;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.dto.UnitDto;

import java.util.List;
import java.util.UUID;

public class DataInitializer {

    public static DataBase createDataBase() {
        return new DataBase(
            "massiver-target",        // name: 데이터베이스 이름
            "h2:mem",                 // groupUnitType: 메모리 기반 H2
            "",                       // host: 필요 없음
            "",                       // port: 필요 없음
            "sa",                     // username: 기본 사용자명
            "sa",                     // password: 기본은 빈 값
            "H2 in-memory DB"         // description: 설명 추가
        );
    }

    public static Unit createUnit() {
        return new Unit(
            "test-unit", // name: Unit 이름
            createDataBase(), // sourceDb: sourceDataBase
            "public", // sourceSchema: public
            "source_table", // sourceTable: source_table
            createDataBase(), // targetDb: targetDataBase
            "public", // targetSchema: public
            "target_table" // targetTable: target_table
        );
    }

    public static UnitDto createUnitDto(
        UUID sourceDbId,
        UUID targetDbId,
        String sourceSchema,
        String sourceTable,
        String targetSchema,
        String targetTable,
        List<ColumnTransformDto> columnTransformDto,
        List<FilterDto> filterDto
    ) {
        return new UnitDto(
            "test-unit", // name: Unit 이름
            sourceDbId, // sourceDbId: sourceDataBase.getId()

            sourceSchema, // sourceSchema: public
            sourceTable, // sourceTable: source_table

            targetDbId, // targetDbId: targetDataBase.getId()
            targetSchema, // targetSchema: public
            targetTable, // targetTable: target_table

            columnTransformDto, // columnTransformDto: columnTransformDto
            filterDto // filterDto: filterDto
        );
    }

    public static ColumnTransform createColumnTransform(Unit unit) {
        return new ColumnTransform(
            unit, // unit: unit
            "source_column", // sourceColumn: source_column
            "target_column" // targetColumn: target_column
        );
    }


}
