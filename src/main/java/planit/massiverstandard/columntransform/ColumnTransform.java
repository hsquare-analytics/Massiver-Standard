package planit.massiverstandard.columntransform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import planit.massiverstandard.BaseEntity;
import planit.massiverstandard.unit.entity.Unit;

import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "massiver_st_column_transform")
public class ColumnTransform extends BaseEntity {

    @Id
    @Column(name = "column_transform_id", updatable = false, nullable = false)
    private UUID id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @Comment("덮어쓰기 여부")
    private boolean isOverWrite;

    @Comment("소스 컬럼명")
    private String sourceColumn;

    @Comment("타겟 컬럼명")
    private String targetColumn;

    @Comment("타겟 컬럼의 타입")
    private String targetColumnType;

    @Builder
    public ColumnTransform(Unit unit, String sourceColumn, String targetColumn, boolean isOverWrite, String targetColumnType) {
        this.id = UUID.randomUUID();
        this.unit = unit;
        this.sourceColumn = sourceColumn;
        this.targetColumn = targetColumn;
        this.isOverWrite = isOverWrite;
        this.targetColumnType = targetColumnType;
    }

    public static ColumnTransform withOutUnitOf(String sourceColumn, String targetColumn, boolean isOverWrite, String targetColumnType) {
        return ColumnTransform.builder()
            .sourceColumn(sourceColumn)
            .targetColumn(targetColumn)
            .isOverWrite(isOverWrite)
            .targetColumnType(targetColumnType)
            .build();
    }

    public void assignUnit(Unit unit) {
        this.unit = unit;
    }
}
