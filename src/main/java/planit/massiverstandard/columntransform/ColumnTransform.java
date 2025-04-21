package planit.massiverstandard.columntransform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import planit.massiverstandard.BaseEntity;
import planit.massiverstandard.unit.entity.Unit;

import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ColumnTransform extends BaseEntity {

    @Id
    @Column(name = "column_transform_id", updatable = false, nullable = false)
    private UUID id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    private String sourceColumn;
    private String targetColumn;

    public ColumnTransform(Unit unit, String sourceColumn, String targetColumn) {
        this.id = UUID.randomUUID();
        this.unit = unit;
        this.sourceColumn = sourceColumn;
        this.targetColumn = targetColumn;
    }

}
