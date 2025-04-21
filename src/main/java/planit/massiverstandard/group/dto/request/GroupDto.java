package planit.massiverstandard.group.dto.request;

import java.util.List;

/**
 * DTO for Group
 * @param name 그룹 이름
 * @param groupUnits 그룹 유닛 목록
 */
public record GroupDto (
    String name,
    List<GroupUnitDto> groupUnits
){
}
