package planit.massiverstandard.unit.dto.response;

import lombok.Data;
import lombok.Getter;
import planit.massiverstandard.columntransform.entity.ColumnTransform;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.filter.entity.Filter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Data
public class UnitResultDto {

    private UUID id;
    private String name;
    private DataSource sourceDb;
    private String sourceSchema;
    private String sourceTable;

    private DataSource targetDb;
    private String targetSchema;
    private String targetTable;

    private List<ColumnTransform> columnTransforms;
    private List<Filter> filters;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UnitResultDto(UUID id,
                         String name,
                         DataSource sourceDb,
                         String sourceSchema,
                         String sourceTable,
                         DataSource targetDb,
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
