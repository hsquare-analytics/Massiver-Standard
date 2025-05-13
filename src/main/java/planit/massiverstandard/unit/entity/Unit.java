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

    @Enumerated(EnumType.STRING)
    private UnitType type; // ETL 단위 타입

    @Enumerated(EnumType.STRING)
    @Column(name = "load_strategy")
    private LoadStrategy loadStrategy;

    @ManyToOne
    @JoinColumn(name = "source_db_id")
    private DataSource sourceDb;

    @Column
    private String sourceSchema;

    @Column
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

    @OneToMany(
        mappedBy = "unit",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY)
    private List<ProcedureParameter> procedureParameters = new ArrayList<>();

    // 📌 Aggregate Root이므로 생성자에서 유효성 검사 수행
    @Builder
    public Unit(
        String name,
        UnitType type,
        LoadStrategy loadStrategy,
        DataSource sourceDb,
        String sourceSchema,
        String sourceTable,
        DataSource targetDb,
        String targetSchema,
        String targetTable,
        List<ColumnTransform> columnTransforms,
        List<Filter> filters,
        List<ProcedureParameter> procedureParameters
    ) {
        if (name == null || type == null || targetDb == null || targetSchema == null || targetTable == null) {
            throw new UnitFieldsRequireException("ETL Unit의 필수 필드를 입력해야 합니다.");
        }
        this.id = UUID.randomUUID();
        this.name = name;
        this.type = type;
        this.loadStrategy = loadStrategy;
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
        if (procedureParameters != null) {
            addParameters(procedureParameters);
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

    public void addParameters(List<ProcedureParameter> procedureParameters) {
        this.procedureParameters.clear();

        for (ProcedureParameter parameter : procedureParameters) {
            parameter.assignUnit(this);
            this.procedureParameters.add(parameter);
        }
    }

    public void update(Unit updateUnit) {
        this.name = updateUnit.getName();
        this.type = updateUnit.getType();
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
