package planit.massiverstandard.unit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import planit.massiverstandard.columntransform.dto.ColumnTransformDto;
import planit.massiverstandard.database.DataBase;
import planit.massiverstandard.database.DataBaseService;
import planit.massiverstandard.filter.dto.FilterDto;
import planit.massiverstandard.filter.entity.Filter;
import planit.massiverstandard.filter.serivce.FilterService;
import planit.massiverstandard.group.service.GroupUnitService;
import planit.massiverstandard.unit.dto.UnitDto;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.repository.UnitJpaRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitJpaRepository unitRepository;
    private final GroupUnitService groupUnitService;
    private final DataBaseService dataBaseService;
    private final UnitGetService unitGetService;

    public List<Unit> getAllUnits() {
        return unitRepository.findAll();
    }

    @Transactional
    public Unit createUnit(UnitDto unitDto) {

        DataBase sourceDataBase = dataBaseService.findById(unitDto.getSourceDb());
        DataBase targetDataBase = dataBaseService.findById(unitDto.getTargetDb());
        Unit entity = new Unit(unitDto.getName(), sourceDataBase, unitDto.getSourceSchema(), unitDto.getSourceTable(), targetDataBase, unitDto.getTargetSchema(), unitDto.getTargetTable());
        Unit savedUnit = unitRepository.save(entity);

        for (ColumnTransformDto columnTransform : unitDto.getColumnTransforms()) {
            savedUnit.addColumnTransform(columnTransform.getSourceColumn(), columnTransform.getTargetColumn());
        }

        for (FilterDto filterDto : unitDto.getFilters()) {
            Filter filter = FilterService.createFilter(savedUnit, filterDto);
            savedUnit.addFilter(filter);
        }

        return savedUnit;
    }

    @Transactional
    public void deleteUnit(UUID id) {
        if (groupUnitService.istExistByUnit(id)) {
            throw new IllegalArgumentException("Unit이 그룹에 속해있어 삭제할 수 없습니다");
        }
        Unit unit = unitGetService.byId(id);
        unitRepository.deleteById(unit.getId());
    }
}
