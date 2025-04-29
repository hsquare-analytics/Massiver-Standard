package planit.massiverstandard.datasource.service;

import planit.massiverstandard.datasource.entity.DataSource;

import java.util.UUID;

public interface FindDataSource {

    /**
     * ID로 데이터 소스를 찾습니다.
     * @param id 데이터 소스의 ID
     * @return 데이터 소스
     */
    DataSource byId(UUID id);
}
