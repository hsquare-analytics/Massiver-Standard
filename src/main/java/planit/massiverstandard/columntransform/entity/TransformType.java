package planit.massiverstandard.columntransform.entity;

public enum TransformType {
    NONE,            // 변환 없음
    TO_STRING,       // toString()
    TO_INTEGER,      // Integer.parseInt(...)
    TO_LONG,         // Long.parseLong(...)
    TO_DOUBLE,       // Double.parseDouble(...)
    TO_BOOLEAN,      // Boolean.parseBoolean(...)
    TRIM,            // .trim()
    UPPERCASE,       // .toUpperCase()
    LOWERCASE,       // .toLowerCase()
    SUBSTRING,       // substring(start, start+length)
    REGEX_REPLACE,   // .replaceAll(pattern, replacement)
    TO_DATE,         // LocalDate.parse(...)
    TO_TIMESTAMP,    // LocalDateTime.parse(...)
    CUSTOM_SQL       // SQL 함수 등 커스텀
}
