package planit.massiverstandard.unit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "massiver_st_unit_procedure_param")
public class ProcedureParameter {

    @Id
    @Column(name = "procedure_param_id")
    private UUID id;

    @Column(nullable = false)
    private String name;        // 파라미터 이름

    @Column(nullable = false)
    private String dataType;    // VARCHAR, INT 등

    @Column(nullable = false)
    private Integer ordinal;    // 순서(1부터 시작)

    /** LITERAL(문자열·숫자·날짜 리터럴) or EXPRESSION(런타임에 평가할 코드 식) */
    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private ProcedureParameterMode mode;

    /**
     * defaultMode=LITERAL 이면
     *   "2025-05-08" 또는 "100" 또는 "hello"
     * defaultMode=EXPRESSION 이면
     *   "T(java.time.LocalDate).now().minusDays(1)" 같은 SpEL 식
     */
    @Column(name = "\"value\"", columnDefinition = "TEXT")
    private String value;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @Builder
    public ProcedureParameter(String name, String dataType, Integer ordinal,
                              ProcedureParameterMode mode, String value) {
        id = UUID.randomUUID();
        this.name    = Objects.requireNonNull(name,    "param name is required");
        this.dataType= Objects.requireNonNull(dataType,"param dataType is required");
        this.ordinal = Objects.requireNonNull(ordinal, "param ordinal is required");
        this.mode = Objects.requireNonNull(mode, "defaultMode is required");
        this.value   = value;
    }

    public void assignUnit(Unit unit) {
        this.unit = unit;
    }
}
