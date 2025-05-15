package planit.massiverstandard.columntransform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import planit.massiverstandard.columntransform.entity.TransformType;

@Getter
@Data
@AllArgsConstructor
public class ColumnTransformDto {
    private boolean isOverWrite;
    private String sourceColumn;
    private String targetColumn;
    private String targetColumnType;

    private TransformType transformType;
    private String formatPattern;
    private String customExpression;
    private Integer substrStart;
    private Integer substrLength;
    private String regexPattern;
    private String replacement;


}
