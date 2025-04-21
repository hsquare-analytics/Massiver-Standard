package planit.massiverstandard.filter.serivce;

import planit.massiverstandard.filter.entity.ApiFilter;
import planit.massiverstandard.filter.entity.Filter;
import planit.massiverstandard.filter.entity.SqlFilter;
import planit.massiverstandard.filter.dto.FilterDto;
import planit.massiverstandard.unit.entity.Unit;

public class FilterService {

    public static Filter createFilter(Unit unit, FilterDto filterDto) {
        return switch (filterDto.getType()) {
            case "SQL" ->
                new SqlFilter(unit, filterDto.getName(), filterDto.getOrder(), filterDto.getSql());
            case "API" ->
                new ApiFilter(unit, filterDto.getName(), filterDto.getOrder(), filterDto.getMethod(), filterDto.getUrl());
            default ->
                throw new IllegalArgumentException("지원하지 않는 필터 타입입니다");
        };
    }
}
