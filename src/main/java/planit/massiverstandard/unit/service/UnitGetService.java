package planit.massiverstandard.unit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.repository.UnitRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UnitGetService implements FindUnit {

    private final UnitRepository unitRepository;

    @Override
    @Transactional(readOnly = true)
    public Unit byId(UUID id) {
        return unitRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Unit을 찾을 수 없습니다"));
    }
}
