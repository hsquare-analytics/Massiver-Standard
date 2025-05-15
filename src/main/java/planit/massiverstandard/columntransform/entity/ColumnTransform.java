package planit.massiverstandard.columntransform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import planit.massiverstandard.BaseEntity;
import planit.massiverstandard.unit.entity.Unit;

import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "massiver_st_column_transform")
public class ColumnTransform extends BaseEntity {

    @Id
    @Column(name = "column_transform_id", updatable = false, nullable = false)
    private UUID id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @Comment("덮어쓰기 여부")
    private boolean isOverWrite;

    @Comment("소스 컬럼명")
    private String sourceColumn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'NONE'")
    private TransformType transformType = TransformType.NONE;

    @Comment("날짜·시간 포맷 패턴(yyyyMMdd 등)")
    private String formatPattern;

    @Comment("CUSTOM_SQL 용 스크립트")
    private String customExpression;

    @Comment("substring 시작 인덱스")
    private Integer substrStart;

    @Comment("substring 길이")
    private Integer substrLength;

    @Comment("정규식 패턴")
    private String regexPattern;

    @Comment("정규식 치환 문자열")
    private String replacement;

    @Comment("타겟 컬럼명")
    private String targetColumn;

    @Comment("타겟 컬럼의 타입")
    private String targetColumnType;

    @Builder
    public ColumnTransform(Unit unit, String sourceColumn, String targetColumn, boolean isOverWrite, String targetColumnType,
                           TransformType transformType,
                           String formatPattern, String customExpression,
                           Integer substrStart, Integer substrLength,
                           String regexPattern, String replacement) {
        this.id = UUID.randomUUID();
        this.unit = unit;
        this.sourceColumn = sourceColumn;
        this.targetColumn = targetColumn;
        this.isOverWrite = isOverWrite;
        this.targetColumnType = targetColumnType;

        this.transformType = transformType == null ? TransformType.NONE : transformType;
        this.formatPattern = formatPattern;
        this.customExpression = customExpression;
        this.substrStart = substrStart;
        this.substrLength = substrLength;
        this.regexPattern = regexPattern;
        this.replacement = replacement;

    }

    public static ColumnTransform withOutUnitOf(String sourceColumn, String targetColumn, boolean isOverWrite, String targetColumnType,
                                                TransformType transformType,
                                                String formatPattern, String customExpression,
                                                Integer substrStart, Integer substrLength,
                                                String regexPattern, String replacement
    ) {
        return ColumnTransform.builder()
            .sourceColumn(sourceColumn)
            .targetColumn(targetColumn)
            .isOverWrite(isOverWrite)
            .targetColumnType(targetColumnType)
            .transformType(transformType)
            .formatPattern(formatPattern)
            .customExpression(customExpression)
            .substrStart(substrStart)
            .substrLength(substrLength)
            .regexPattern(regexPattern)
            .replacement(replacement)
            .build();
    }

    public void assignUnit(Unit unit) {
        this.unit = unit;
    }
}
