package planit.massiverstandard.unit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import planit.massiverstandard.columntransform.ColumnTransform;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.datasource.service.DataSourceService;
import planit.massiverstandard.exception.unit.UnitInGroupException;
import planit.massiverstandard.filter.dto.FilterDto;
import planit.massiverstandard.filter.entity.Filter;
import planit.massiverstandard.filter.serivce.FilterService;
import planit.massiverstandard.group.service.GroupUnitService;
import planit.massiverstandard.unit.dto.UnitDto;
import planit.massiverstandard.unit.dto.request.UnitUpdateDto;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.repository.UnitRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UnitService implements CommandUnit {

    private final UnitRepository unitRepository;
    private final GroupUnitService groupUnitService;
    private final DataSourceService dataSourceService;
    private final UnitGetService unitGetService;

    public List<Unit> getAllUnits() {
        return unitRepository.findAll();
    }

    @Transactional
    public Unit createUnit(UnitDto unitDto) {

        DataSource sourceDataSource = dataSourceService.findById(unitDto.getSourceDb());
        DataSource targetDataSource = dataSourceService.findById(unitDto.getTargetDb());
        Unit entity = Unit.builder()
            .name(unitDto.getName())
            .sourceDb(sourceDataSource)
            .sourceSchema(unitDto.getSourceSchema())
            .sourceTable(unitDto.getSourceTable())
            .targetDb(targetDataSource)
            .targetSchema(unitDto.getTargetSchema())
            .targetTable(unitDto.getTargetTable())
            .build();

        Unit savedUnit = unitRepository.save(entity);

        List<ColumnTransform> entityList = unitDto.getColumnTransforms().stream()
            .map(columnTransformDto -> ColumnTransform.builder()
                .sourceColumn(columnTransformDto.getSourceColumn())
                .targetColumn(columnTransformDto.getTargetColumn())
                .isOverWrite(columnTransformDto.isOverWrite())
                .build()
            ).toList();

        savedUnit.addColumnTransform(entityList);

        for (FilterDto filterDto : unitDto.getFilters()) {
            Filter filter = FilterService.createFilter(savedUnit, filterDto);
            savedUnit.addFilter(filter);
        }

        return savedUnit;
    }

    @Transactional
    public void deleteUnit(UUID id) {
        if (groupUnitService.istExistByUnit(id)) {
            throw new UnitInGroupException("Unit이 그룹에 속해있어 삭제할 수 없습니다");
        }
        Unit unit = unitGetService.byId(id);
        unitRepository.deleteById(unit.getId());
    }

    @Override
    @Transactional
    public void update(UUID unitId, UnitUpdateDto updateDto) {

        Unit unit = unitGetService.byId(unitId);

        DataSource sourceDataSource = dataSourceService.findById(updateDto.sourceDb());
        DataSource targetDataSource = dataSourceService.findById(updateDto.targetDb());

        List<ColumnTransform> columnTransforms = updateDto.columnTransforms()
            .stream()
            .map(columnTransformDto ->
                ColumnTransform.withOutUnitOf(
                    columnTransformDto.getSourceColumn(),
                    columnTransformDto.getTargetColumn(),
                    columnTransformDto.isOverWrite()
                )
            )
            .toList();

        List<Filter> filters = updateDto.filters()
            .stream()
            .map(FilterService::createFilterWithOutUnit)
            .toList();

        Unit entity = Unit.builder()
            .name(updateDto.name())
            .sourceDb(sourceDataSource)
            .sourceSchema(updateDto.sourceSchema())
            .sourceTable(updateDto.sourceTable())
            .targetDb(targetDataSource)
            .targetSchema(updateDto.targetSchema())
            .targetTable(updateDto.targetTable())
            .columnTransforms(columnTransforms)
            .filters(filters)
            .build();

        unit.update(entity);
    }
}
