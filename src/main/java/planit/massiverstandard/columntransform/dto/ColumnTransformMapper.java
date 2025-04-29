package planit.massiverstandard.columntransform.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import planit.massiverstandard.config.DefaultMapStructConfig;

@Mapper(config = DefaultMapStructConfig.class)
public interface ColumnTransformMapper {

    @Mapping(source = "isOverWrite", target = "overWrite")
    ColumnTransformDto toDto(ColumnTransformRequest columnTransformRequest);
}
