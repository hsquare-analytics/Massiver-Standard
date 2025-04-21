package planit.massiverstandard.unit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import planit.massiverstandard.unit.dto.UnitDto;
import planit.massiverstandard.unit.dto.UnitMapper;
import planit.massiverstandard.unit.dto.request.UnitRequestDto;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.service.UnitGetService;
import planit.massiverstandard.unit.service.UnitService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Unit", description = "UNIT을 관리합니다.")
@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;
    private final UnitGetService unitGetService;

    private final UnitMapper unitMapper;

    @Operation(summary = "UNIT 생성", description = "UNIT을 생성합니다.")
    @PostMapping
    public Unit createUnit(@Valid @RequestBody UnitRequestDto unitRequestDto) {
        UnitDto dto = unitMapper.toDto(unitRequestDto);
        return unitService.createUnit(dto);
    }

    @Operation(summary = "UNIT 목록 조회", description = "UNIT 목록을 조회합니다.")
    @GetMapping
    public List<Unit> getAllUnits() {
        return unitService.getAllUnits();
    }

    @Operation(summary = "UNIT ID로 조회", description = "UNIT ID로 UNIT을 조회합니다.")
    @GetMapping("/{id}")
    public Unit getUnit(@PathVariable UUID id) {
        return unitGetService.byId(id);
    }

    @Operation(summary = "UNIT 삭제", description = "UNIT을 삭제합니다.")
    @DeleteMapping("/{id}")
    public void deleteUnit(@PathVariable UUID id) {
        unitService.deleteUnit(id);
    }

}
