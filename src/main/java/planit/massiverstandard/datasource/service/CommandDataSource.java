package planit.massiverstandard.datasource.service;

import planit.massiverstandard.datasource.dto.request.DataSourceTestConnectionDto;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.datasource.dto.request.DataSourceCreateDto;

import java.util.UUID;

/**
 * 설명: 데이터베이스 생성 인터페이스
 * 작성일: 2025. 04. 22.
 * 작성자: Mason
 */
public interface CommandDataSource {

    /**
     * 데이터베이스 생성
     * @param dto 데이터베이스 생성 DTO
     * @return 생성된 데이터베이스 객체
     */
    DataSource create(DataSourceCreateDto dto);

    void update(UUID id, DataSourceCreateDto dataSource);

    void testConnection(DataSourceTestConnectionDto dataSource);
}
