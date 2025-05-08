package planit.massiverstandard.unit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import planit.massiverstandard.columntransform.dto.ColumnTransformDto;
import planit.massiverstandard.filter.dto.FilterDto;
import planit.massiverstandard.unit.entity.UnitType;

import java.util.List;
import java.util.UUID;

@Getter
@Data
@AllArgsConstructor
public class UnitDto {

    private String name;
    private UnitType type;
    private UUID sourceDb;
    private String sourceSchema;
    private String sourceTable;
    private UUID targetDb;
    private String targetSchema;
    private String targetTable;
    private List<ColumnTransformDto> columnTransforms;
    private List<FilterDto> filters;

}
