package planit.massiverstandard.columntransform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@AllArgsConstructor
public class ColumnTransformDto {
    private boolean isOverWrite;
    private String sourceColumn;
    private String targetColumn;
}
