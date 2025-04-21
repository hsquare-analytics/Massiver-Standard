package planit.massiverstandard.schedule.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ScheduleResponseDto (
    @ArraySchema(
        schema = @Schema(description = "crons 표현식", example = "0 0/1 * 1/1 * ? *")
    )
    List<String> crons
) {
}
