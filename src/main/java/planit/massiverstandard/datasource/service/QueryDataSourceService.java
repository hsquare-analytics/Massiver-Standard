package planit.massiverstandard.datasource.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.datasource.repository.DataSourceRepository;

import java.util.UUID;

@Service
@Transactional("transactionManager")
@RequiredArgsConstructor
public class QueryDataSourceService implements FindDataSource {

    private final DataSourceRepository dataSourceRepository;

    @Override
    public DataSource byId(UUID id) {
        return dataSourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data source not found"));
    }
}
