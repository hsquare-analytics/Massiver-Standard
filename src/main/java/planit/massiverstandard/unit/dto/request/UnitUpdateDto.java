package planit.massiverstandard.unit.dto.request;

import planit.massiverstandard.columntransform.dto.ColumnTransformDto;
import planit.massiverstandard.filter.dto.FilterDto;
import planit.massiverstandard.unit.entity.LoadStrategy;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.entity.UnitType;

import java.util.List;
import java.util.UUID;

/**
 * 유닛 업데이트 요청 DTO {@link Unit}
 *
 * @param name             Unit 이름
 * @param type             유닛 타입
 * @param loadStrategy     로드 전략
 * @param sourceDb         소스 DB ID
 * @param sourceSchema     소스 스키마
 * @param sourceTable      소스 테이블
 * @param targetDb         타겟 DB ID
 * @param targetSchema     타겟 스키마
 * @param targetTable      타겟 테이블
 * @param columnTransforms 컬럼 변환 정보
 * @param filters          적용할 필터 정보 목록
 */
public record UnitUpdateDto(
    String name,
    UnitType type,
    LoadStrategy loadStrategy,
    UUID sourceDb,
    String sourceSchema,
    String sourceTable,
    UUID targetDb,
    String targetSchema,
    String targetTable,
    List<ColumnTransformDto> columnTransforms,
    List<FilterDto> filters
) {
}
