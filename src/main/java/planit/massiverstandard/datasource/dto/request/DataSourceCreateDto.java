package planit.massiverstandard.datasource.dto.request;

import planit.massiverstandard.datasource.entity.DataSourceType;

public record DataSourceCreateDto(
        String name,
        DataSourceType type,
        String database,
        String host,
        String port,
        String username,
        String password
){
}
