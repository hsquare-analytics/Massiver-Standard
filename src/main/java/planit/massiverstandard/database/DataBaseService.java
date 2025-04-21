package planit.massiverstandard.database;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import planit.massiverstandard.database.repository.DataBaseRepository;

import java.util.List;
import java.util.UUID;

@Service
public class DataBaseService {

    private final DataBaseRepository dataBaseRepository;

    public DataBaseService(DataBaseRepository dataBaseRepository) {
        this.dataBaseRepository = dataBaseRepository;
    }

    @Transactional
    public DataBase createDataBase(DataBase dataBase) {
        DataBase entity = new DataBase(
            dataBase.getName(),
            dataBase.getType(),
            dataBase.getHost(),
            dataBase.getPort(),
            dataBase.getUsername(),
            dataBase.getPassword(),
            dataBase.getDescription()
        );
        return dataBaseRepository.save(entity);
    }

    public List<DataBase> findAll() {
        return dataBaseRepository.findAll();
    }

    public DataBase findById(UUID id) {
        return dataBaseRepository.findById(id).orElseThrow();
    }

}
