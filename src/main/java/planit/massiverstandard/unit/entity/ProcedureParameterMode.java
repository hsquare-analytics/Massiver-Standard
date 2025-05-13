package planit.massiverstandard.unit.entity;

public enum ProcedureParameterMode {
    LITERAL,     // 고정값
    EXPRESSION   // SpEL, MVEL 같은 실행 시 평가되는 식
}
