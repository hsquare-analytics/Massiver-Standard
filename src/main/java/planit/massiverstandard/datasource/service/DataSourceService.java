package planit.massiverstandard.datasource.service;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import planit.massiverstandard.datasource.dto.request.DataSourceCreateDto;
import planit.massiverstandard.datasource.dto.request.DataSourceTestConnectionDto;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.datasource.repository.DataSourceRepository;
import planit.massiverstandard.datasource.util.DataSourceResolver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class DataSourceService implements CommandDataSource {

    private final DataSourceRepository dataSourceRepository;
    private final FindDataSource findDataSource;

    @Override
    public DataSource create(DataSourceCreateDto dto) {
        DataSource entity = DataSource.builder()
            .name(dto.name())
            .type(dto.type())
            .database(dto.database())
            .host(dto.host())
            .port(dto.port())
            .username(dto.username())
            .password(dto.password())
            .build();

        testConnection(entity); // 데이터베이스 연결 테스트

        return dataSourceRepository.save(entity);
    }

    @Override
    public void update(UUID id, DataSourceCreateDto dto) {
        DataSource entity = DataSource.builder()
            .name(dto.name())
            .type(dto.type())
            .database(dto.database())
            .host(dto.host())
            .port(dto.port())
            .username(dto.username())
            .password(dto.password())
            .build();

        testConnection(entity);

        DataSource dataSource = findDataSource.byId(id);
        dataSource.update(entity);
    }

    @Override
    public void testConnection(DataSourceTestConnectionDto testDto) {
        DataSource dataSourceEntity = DataSource.builder()
            .type(testDto.type())
            .database(testDto.database())
            .host(testDto.host())
            .port(testDto.port())
            .username(testDto.username())
            .password(testDto.password())
            .build();

        testConnection(dataSourceEntity);
    }


    /**
     * * 데이터베이스 연결 테스트
     *
     * @param dataSource datasourceEntity
     */
    public void testConnection(DataSource dataSource) {
        try (

            HikariDataSource hikari = (HikariDataSource) DataSourceResolver.createDataSource(dataSource);
            Connection conn = hikari.getConnection()
        ) {
            if (!conn.isValid(3)) {
                throw new RuntimeException("유효하지 않은 커넥션");
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB 연결/풀 종료 중 오류", e);
        }
    }
    public List<DataSource> findAll() {
        return dataSourceRepository.findAll();
    }

    public DataSource findById(UUID id) {
        return dataSourceRepository.findById(id).orElseThrow();
    }

}
