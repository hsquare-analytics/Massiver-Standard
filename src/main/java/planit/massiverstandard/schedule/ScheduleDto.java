package planit.massiverstandard.schedule;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 설명:
 * 작성일: 2025. 04. 01.
 * 작성자: Mason
 */
public record ScheduleDto(
    @ArraySchema(
        schema = @Schema(description = "crons 표현식", example = "0 0/1 * 1/1 * ? *")
    )
    List<String> crons
) {
}
