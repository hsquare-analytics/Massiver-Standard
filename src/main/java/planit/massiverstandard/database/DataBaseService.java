package planit.massiverstandard.database;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import planit.massiverstandard.database.dto.DataBaseCreateDto;
import planit.massiverstandard.database.repository.DataBaseRepository;
import planit.massiverstandard.database.service.CreateDb;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

@Service
public class DataBaseService implements CreateDb {

    private final DataBaseRepository dataBaseRepository;

    public DataBaseService(DataBaseRepository dataBaseRepository) {
        this.dataBaseRepository = dataBaseRepository;
    }


    @Override
    @Transactional
    public DataBase create(DataBaseCreateDto dto) {
        DataBase entity = new DataBase(
            dto.name(),
            dto.type(),
            dto.host(),
            dto.port(),
            dto.username(),
            dto.password(),
            dto.description()
        );

        DataSource dataSource = entity.createDataSource();
        testConnection(dataSource); // 데이터베이스 연결 테스트

        return dataBaseRepository.save(entity);
    }

    public void testConnection(DataSource dataSource) {
        try (var connection = dataSource.getConnection()) {
            connection.isValid(2); // 2초 내에 유효성 검사
        } catch (Exception e) {
            throw new RuntimeException("데이터베이스 연결에 실패했습니다.", e);
        }
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
