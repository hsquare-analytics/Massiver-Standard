package planit.massiverstandard.unit.entity;

public enum LoadStrategy {
    /** 조건 없이 누적 적재(Append) */
    APPEND,
    /** 전체 삭제 후 적재(Full) */
    FULL,
    /** 기준 키에 따라 삭제 후 덮어쓰기(Overwrite) */
    OVERWRITE
}
