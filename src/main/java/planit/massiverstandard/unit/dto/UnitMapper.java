package planit.massiverstandard.unit.dto;

import org.mapstruct.Mapper;
import planit.massiverstandard.config.DefaultMapStructConfig;
import planit.massiverstandard.unit.dto.request.ProcedureParameterDto;
import planit.massiverstandard.unit.dto.request.UnitRequest;
import planit.massiverstandard.unit.dto.request.UnitUpdateDto;
import planit.massiverstandard.unit.dto.request.UnitUpdateRequestDto;
import planit.massiverstandard.unit.dto.response.UnitResponseDto;
import planit.massiverstandard.unit.dto.response.UnitResultDto;
import planit.massiverstandard.unit.entity.ProcedureParameter;
import planit.massiverstandard.unit.entity.Unit;

@Mapper(config = DefaultMapStructConfig.class)
public interface UnitMapper {

    UnitDto toDto(UnitRequest unitRequest);

    UnitResultDto toResultDto(Unit unit);

    UnitResponseDto toResponseDto(UnitResultDto unit);

    UnitUpdateDto toUpdateDto(UnitUpdateRequestDto unitUpdateRequestDto);

    ProcedureParameter toProcedureParameter(ProcedureParameterDto procedureParameterDto);

}
