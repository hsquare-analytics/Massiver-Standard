package planit.massiverstandard.filter.dto;

import org.mapstruct.Mapper;
import planit.massiverstandard.config.DefaultMapStructConfig;

@Mapper(config = DefaultMapStructConfig.class)
public interface FilterMapper {

    FilterDto toDto(FilterRequestDto filterRequestDto);
}
