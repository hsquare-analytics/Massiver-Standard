package planit.massiverstandard.unit.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import planit.massiverstandard.BaseEntity;
import planit.massiverstandard.Executable;
import planit.massiverstandard.columntransform.ColumnTransform;
import planit.massiverstandard.database.DataBase;
import planit.massiverstandard.filter.entity.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Unit extends BaseEntity implements Executable {

    @Id
    private UUID id;

    private String name; // ETL 단위명

    @ManyToOne
    @JoinColumn(name = "source_db_id")
    private DataBase sourceDb;

    @Column(nullable = false)
    private String sourceSchema;

    @Column(nullable = false)
    private String sourceTable;

    @ManyToOne
    @JoinColumn(name = "target_db_id")
    private DataBase targetDb;

    @Column(nullable = false)
    private String targetSchema;

    @Column(nullable = false)
    private String targetTable;

    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ColumnTransform> columnTransforms = new ArrayList<>();

    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Filter> filters = new ArrayList<>();

    // 📌 Aggregate Root이므로 생성자에서 유효성 검사 수행
    @Builder
    public Unit(
        String name,
        DataBase sourceDb,
        String sourceSchema,
        String sourceTable,
        DataBase targetDb,
        String targetSchema,
        String targetTable
    ) {
        if (name == null || sourceDb == null || sourceTable == null || targetDb == null || targetTable == null) {
            throw new IllegalArgumentException("ETL Unit의 필수 필드를 입력해야 합니다.");
        }
        this.id = UUID.randomUUID();
        this.name = name;
        this.sourceDb = sourceDb;
        this.sourceSchema = sourceSchema;
        this.sourceTable = sourceTable;
        this.targetDb = targetDb;
        this.targetSchema = targetSchema;
        this.targetTable = targetTable;
    }

    public void addColumnTransform(String sourceColumn, String targetColumn) {
        ColumnTransform columnTransform = new ColumnTransform(this, sourceColumn, targetColumn);
        columnTransforms.add(columnTransform);
    }

    public void addFilter(Filter filter) {
        filters.add(filter);
    }

}
