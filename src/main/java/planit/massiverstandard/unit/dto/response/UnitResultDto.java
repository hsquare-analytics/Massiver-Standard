package planit.massiverstandard.unit.dto.response;

import lombok.Data;
import lombok.Getter;
import planit.massiverstandard.columntransform.ColumnTransform;
import planit.massiverstandard.database.DataBase;
import planit.massiverstandard.filter.entity.Filter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Data
public class UnitResultDto {

    private UUID id;
    private String name;
    private DataBase sourceDb;
    private String sourceSchema;
    private String sourceTable;

    private DataBase targetDb;
    private String targetSchema;
    private String targetTable;

    private List<ColumnTransform> columnTransforms;
    private List<Filter> filters;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UnitResultDto(UUID id,
                         String name,
                         DataBase sourceDb,
                         String sourceSchema,
                         String sourceTable,
                         DataBase targetDb,
                         String targetSchema,
                         String targetTable,
                         List<ColumnTransform> columnTransforms,
                         List<Filter> filters,
                         LocalDateTime createdAt,
                         LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.sourceDb = sourceDb;
        this.sourceSchema = sourceSchema;
        this.sourceTable = sourceTable;
        this.targetDb = targetDb;
        this.targetSchema = targetSchema;
        this.targetTable = targetTable;
        this.columnTransforms = columnTransforms;
        this.filters = filters;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
