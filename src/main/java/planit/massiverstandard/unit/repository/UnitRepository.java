package planit.massiverstandard.unit.repository;

import planit.massiverstandard.unit.entity.Unit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UnitRepository {

    List<Unit> findAll();

    Optional<Unit> findById(UUID id);

    Unit save(Unit unit);

    void deleteById(UUID id);

    void deleteAll();

}
