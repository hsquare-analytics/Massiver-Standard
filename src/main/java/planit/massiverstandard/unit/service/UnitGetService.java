package planit.massiverstandard.unit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import planit.massiverstandard.exception.unit.UnitNotFoundException;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.repository.UnitRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UnitGetService implements FindUnit {

    private final UnitRepository unitRepository;

    @Override
    @Transactional(value = "transactionManager", readOnly = true)
    public Unit byId(UUID id) {
        Optional<Unit> withFiltersById = unitRepository.findWithFiltersById(id);
        return withFiltersById.orElseThrow(() -> new UnitNotFoundException("Unit을 찾을 수 없습니다"));
    }

    @Override
    @Transactional(value = "transactionManager", readOnly = true)
    public Unit byIdWithColumnTransform(UUID id) {
        return unitRepository.findWithColumnTransformById(id).orElseThrow(() -> new UnitNotFoundException("Unit을 찾을 수 없습니다"));
    }

    @Override
    @Transactional(value = "transactionManager", readOnly = true)
    public List<Unit> findByDataSource(UUID dataSourceId) {
        return unitRepository.findUnitBySourceOrTargetDb(dataSourceId);
    }

    @Override
    @Transactional(value = "transactionManager", readOnly = true)
    public Unit byIdWithProcedureParameter(UUID id) {
        return unitRepository.findWithProcedureParameterById(id).orElseThrow(() -> new UnitNotFoundException("Unit을 찾을 수 없습니다"));
    }
}
