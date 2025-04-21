package planit.massiverstandard.group.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import planit.massiverstandard.group.entity.GroupUnitType;
import planit.massiverstandard.unit.dto.response.UnitResponseDto;

import java.util.List;
import java.util.UUID;

@Getter
@Data
public class GroupUnitResponseDto {

    private UUID id;
    private GroupResponseDto childGroup;
    private UnitResponseDto childUnit;
    private GroupUnitType groupUnitType;
    private List<ParentGroupUnitResponse> parentUnits;

    @Builder
    public GroupUnitResponseDto(UUID id, GroupResponseDto childGroup, UnitResponseDto childUnit, GroupUnitType groupUnitType, List<ParentGroupUnitResponse> parentGroupUnits) {
        this.id = id;
        this.childUnit = childUnit;
        this.childGroup = childGroup;
        this.groupUnitType = groupUnitType;
        this.parentUnits = parentGroupUnits;
    }
}
