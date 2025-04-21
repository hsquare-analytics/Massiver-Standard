package planit.massiverstandard.unit.service;

import planit.massiverstandard.unit.entity.Unit;

import java.util.UUID;

public interface FindUnit {

    /**
     * unitId로 unit을 찾는다.
     *
     * @param id unitId
     * @return unit
     */
    Unit byId(UUID id);

}
