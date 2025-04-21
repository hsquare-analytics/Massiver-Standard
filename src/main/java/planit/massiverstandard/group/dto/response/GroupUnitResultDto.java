package planit.massiverstandard.group.dto.response;

import planit.massiverstandard.group.entity.GroupUnitType;
import planit.massiverstandard.unit.dto.response.UnitResultDto;

import java.util.List;
import java.util.UUID;

public record GroupUnitResultDto (
    UUID id,
    UnitResultDto childUnit,
    GroupResultDto childGroup,
    GroupUnitType groupUnitType,
    List<ParentGroupUnitResult> parentGroupUnits
){

}
