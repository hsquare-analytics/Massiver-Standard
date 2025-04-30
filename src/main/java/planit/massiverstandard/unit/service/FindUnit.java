package planit.massiverstandard.unit.service;

import planit.massiverstandard.unit.entity.Unit;

import java.util.List;
import java.util.UUID;

public interface FindUnit {

    /**
     * unitId로 unit을 찾는다.
     *
     * @param id unitId
     * @return unit
     */
    Unit byId(UUID id);

    /**
     * unitId로 unit을 찾는다.
     *
     * @param id unitId
     * @return unit
     */
    Unit byIdWithColumnTransform(UUID id);

    /**
     * 데이터 소스가 사용 중인지 확인하는 메서드.
     * @param dataSourceId 데이터 소스 ID
     * @return 사용 중인 유닛 목록
     */
    List<Unit> findByDataSource(UUID dataSourceId);

}
