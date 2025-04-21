package planit.massiverstandard.group.dto.response.analysis;

import planit.massiverstandard.unit.dto.response.UnitResultDto;

/**
 * 테이블 비율
 *
 * @param unit 유닛
 * @param tableCount 테이블 수
 * @param ratio 비율
 */
public record TableRatio (
    UnitResultDto unit,
    Integer tableCount,
    Double ratio
){
}
