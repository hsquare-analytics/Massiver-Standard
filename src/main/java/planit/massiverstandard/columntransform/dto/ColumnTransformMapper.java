package planit.massiverstandard.columntransform.dto;

import org.mapstruct.Mapper;
import planit.massiverstandard.config.DefaultMapStructConfig;

@Mapper(config = DefaultMapStructConfig.class)
public interface ColumnTransformMapper {

    ColumnTransformDto toDto(ColumnTransformRequestDto columnTransformRequestDto);
}
