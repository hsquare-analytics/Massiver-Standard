package planit.massiverstandard.columntransform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@AllArgsConstructor
public class ColumnTransformRequestDto {

    @NotBlank
    private String sourceColumn;

    @NotBlank
    private String targetColumn;

}
