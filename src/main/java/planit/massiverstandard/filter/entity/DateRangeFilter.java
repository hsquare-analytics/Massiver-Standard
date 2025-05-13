package planit.massiverstandard.filter.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import planit.massiverstandard.unit.entity.Unit;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Entity
@DiscriminatorValue(value = "DATE_RANGE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "massiver_st_date_range_filter")
public class DateRangeFilter extends Filter implements WhereFilter{


    private String columnName; // 날짜/시간 컬럼 이름

    // ✅ 고정 날짜 값 필드
    private LocalDate fixedStartDate;
    private LocalDate fixedEndDate;

    // ✅ 동적 날짜 표현식 필드 (String, SpEL 등)
    private String dynamicStartDateExpression;
    private String dynamicEndDateExpression;

    // 생성자
    @Builder
    public DateRangeFilter(Unit unit, String name, int order, String columnName, LocalDate fixedStartDate, LocalDate fixedEndDate, String dynamicStartDateExpression, String dynamicEndDateExpression) {
        super(unit, name, order);
        this.columnName = columnName;
        this.fixedStartDate = fixedStartDate;
        this.fixedEndDate = fixedEndDate;
        this.dynamicStartDateExpression = dynamicStartDateExpression;
        this.dynamicEndDateExpression = dynamicEndDateExpression;
    }

    @Override
    public Object[] process(Object[] item) {
        return item;
    }

    @Override
    public void addCondition(StringBuilder sql) {
        if (fixedStartDate != null && fixedEndDate != null) {
            sql.append(" AND ").append(columnName).append(" BETWEEN '")
                .append(fixedStartDate).append("' AND '").append(fixedEndDate).append("'");
        } else if (dynamicStartDateExpression != null && dynamicEndDateExpression != null) {
            sql.append(" AND ").append(columnName).append(" BETWEEN ")
                .append(dynamicStartDateExpression).append(" AND ").append(dynamicEndDateExpression);
        }
    }

}
