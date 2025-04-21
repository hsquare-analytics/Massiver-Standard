package planit.massiverstandard.data;

import org.springframework.context.annotation.Profile;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.repository.UnitRepository;

import java.util.*;

@Profile("test")
public class UnitMemoryRepository implements UnitRepository {

    private static final Map<UUID, Unit> store = new HashMap<>();

    @Override
    public List<Unit> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public Optional<Unit> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Unit save(Unit unit) {
        if (!store.containsKey(unit.getId())) {
            store.put(unit.getId(), unit);
        }
        return unit;
    }

    @Override
    public void deleteById(UUID id) {
        store.remove(id);
    }

    @Override
    public void deleteAll() {
        store.clear();
    }
}
