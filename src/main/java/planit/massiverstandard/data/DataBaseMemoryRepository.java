package planit.massiverstandard.data;

import org.springframework.context.annotation.Profile;
import planit.massiverstandard.database.DataBase;
import planit.massiverstandard.database.repository.DataBaseRepository;

import java.util.*;

@Profile("test")
public class DataBaseMemoryRepository implements DataBaseRepository {

    private static final Map<UUID, DataBase> databaseMap = new HashMap<>();


    @Override
    public DataBase save(DataBase dataBase) {
        databaseMap.put(dataBase.getId(), dataBase);
        return dataBase;
    }

    @Override
    public Optional<DataBase> findById(UUID id) {
        return Optional.ofNullable(databaseMap.get(id));
    }

    @Override
    public List<DataBase> findAll() {
        return new ArrayList<>(databaseMap.values());
    }

    @Override
    public void deleteAll() {
        databaseMap.clear();
    }
}
