package planit.massiverstandard.unit.dto;

import org.mapstruct.Mapper;
import planit.massiverstandard.config.DefaultMapStructConfig;
import planit.massiverstandard.unit.dto.request.UnitRequestDto;
import planit.massiverstandard.unit.dto.response.UnitResponseDto;
import planit.massiverstandard.unit.dto.response.UnitResultDto;
import planit.massiverstandard.unit.entity.Unit;

@Mapper(config = DefaultMapStructConfig.class)
public interface UnitMapper {

    UnitDto toDto(UnitRequestDto unitRequestDto);

    UnitResultDto toResultDto(Unit unit);

    UnitResponseDto toResponseDto(UnitResultDto unit);
}
