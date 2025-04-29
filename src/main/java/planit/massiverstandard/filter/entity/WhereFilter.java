package planit.massiverstandard.filter.entity;

public interface WhereFilter {
    void addCondition(StringBuilder sql);
}
