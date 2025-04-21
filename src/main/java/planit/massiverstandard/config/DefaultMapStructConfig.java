package planit.massiverstandard.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;
import planit.massiverstandard.columntransform.dto.ColumnTransformMapper;
import planit.massiverstandard.filter.dto.FilterMapper;
import planit.massiverstandard.group.dto.mapper.Mapper;
import planit.massiverstandard.unit.dto.UnitMapper;

@MapperConfig(
    componentModel = "spring", // Spring Bean으로 등록
    unmappedTargetPolicy = ReportingPolicy.ERROR, // 매핑되지 않은 필드 → 에러
    typeConversionPolicy = ReportingPolicy.WARN, // 타입 변환 경고
    uses = {
        Mapper.class,
        ColumnTransformMapper.class,
        FilterMapper.class,
        UnitMapper.class,

    }
)
public interface DefaultMapStructConfig {
}
