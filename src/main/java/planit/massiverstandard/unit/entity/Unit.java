package planit.massiverstandard.unit.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import planit.massiverstandard.BaseEntity;
import planit.massiverstandard.Executable;
import planit.massiverstandard.columntransform.ColumnTransform;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.exception.unit.UnitFieldsRequireException;
import planit.massiverstandard.filter.entity.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "massiver_st_unit")
public class Unit extends BaseEntity implements Executable {

    @Id
    private UUID id;

    private String name; // ETL 단위명

    @ManyToOne
    @JoinColumn(name = "source_db_id")
    private DataSource sourceDb;

    @Column(nullable = false)
    private String sourceSchema;

    @Column(nullable = false)
    private String sourceTable;

    @ManyToOne
    @JoinColumn(name = "target_db_id")
    private DataSource targetDb;

    @Column(nullable = false)
    private String targetSchema;

    @Column(nullable = false)
    private String targetTable;

    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ColumnTransform> columnTransforms = new ArrayList<>();

    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Filter> filters = new ArrayList<>();

    // 📌 Aggregate Root이므로 생성자에서 유효성 검사 수행
    @Builder
    public Unit(
        String name,
        DataSource sourceDb,
        String sourceSchema,
        String sourceTable,
        DataSource targetDb,
        String targetSchema,
        String targetTable,
        List<ColumnTransform> columnTransforms,
        List<Filter> filters
    ) {
        if (name == null || sourceDb == null || sourceTable == null || targetDb == null || targetTable == null) {
            throw new UnitFieldsRequireException("ETL Unit의 필수 필드를 입력해야 합니다.");
        }
        this.id = UUID.randomUUID();
        this.name = name;
        this.sourceDb = sourceDb;
        this.sourceSchema = sourceSchema;
        this.sourceTable = sourceTable;
        this.targetDb = targetDb;
        this.targetSchema = targetSchema;
        this.targetTable = targetTable;
        if (columnTransforms != null) {
            addColumnTransform(columnTransforms);
        }
        if (filters != null) {
            addFilter(filters);
        }
    }

    public void addColumnTransform(List<ColumnTransform> columnTransforms) {

        this.columnTransforms.clear();

        for (ColumnTransform columnTransform : columnTransforms) {
            columnTransform.assignUnit(this);
            this.columnTransforms.add(columnTransform);
        }
    }

    public void addFilter(List<Filter> filters) {

        this.filters.clear();

        for (Filter filter : filters) {
            filter.assignUnit(this);
            this.filters.add(filter);
        }
    }

    public void addFilter(Filter filter) {
        filter.assignUnit(this);
        filters.add(filter);
    }

    public void update(Unit updateUnit) {
        this.name = updateUnit.getName();
        this.sourceDb = updateUnit.getSourceDb();
        this.sourceSchema = updateUnit.getSourceSchema();
        this.sourceTable = updateUnit.getSourceTable();
        this.targetDb = updateUnit.getTargetDb();
        this.targetSchema = updateUnit.getTargetSchema();
        this.targetTable = updateUnit.getTargetTable();

        addColumnTransform(updateUnit.getColumnTransforms());
        addFilter(updateUnit.getFilters());
    }

}
