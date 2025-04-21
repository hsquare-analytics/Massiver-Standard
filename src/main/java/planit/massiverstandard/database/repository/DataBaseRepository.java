package planit.massiverstandard.database.repository;

import planit.massiverstandard.database.DataBase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DataBaseRepository {

    DataBase save(DataBase dataBase);

    Optional<DataBase> findById(UUID id);

    List<DataBase> findAll();

    void deleteAll();
}
