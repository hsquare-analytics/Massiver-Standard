package planit.massiverstandard.unit.service;

import planit.massiverstandard.unit.dto.request.UnitUpdateDto;
import planit.massiverstandard.unit.dto.request.UnitUpdateRequestDto;

import java.util.UUID;

/**
 * 설명: CommandUnit 인터페이스
 * 작성일: 2025. 04. 25.
 * 작성자: Mason
 */
public interface CommandUnit {

    void update(UUID unitId, UnitUpdateDto unitUpdateDto);
}
