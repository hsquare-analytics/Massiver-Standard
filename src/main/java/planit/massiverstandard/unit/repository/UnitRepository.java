package planit.massiverstandard.unit.repository;

import org.springframework.data.repository.query.Param;
import planit.massiverstandard.columntransform.ColumnTransform;
import planit.massiverstandard.filter.entity.Filter;
import planit.massiverstandard.unit.entity.Unit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UnitRepository {

    Unit save(Unit unit);

    List<Unit> findAll();

    void deleteById(UUID id);

    /**
     * {@link Filter} 와 join하여 조회하는 메서드
     * @param id 조회할 {@link Unit} 의 id
     * @return 조회된 {@link Unit} 객체
     */
    Optional<Unit> findWithFiltersById(@Param("id") UUID id);

    /**
     * {@link Unit} 의 {@link ColumnTransform} 과 join하여 조회하는 메서드
     * @param id 조회할 {@link Unit} 의 id
     * @return 조회된 {@link Unit} 객체
     */
    Optional<Unit> findWithColumnTransformById(@Param("id") UUID id);

    /**
     * 데이터 소스가 사용 중인지 확인하는 메서드.
     * @param datasourceId 데이터 소스 ID
     * @return 사용 중인 유닛 목록
     */
    List<Unit> findUnitBySourceOrTargetDb(UUID datasourceId);
}
