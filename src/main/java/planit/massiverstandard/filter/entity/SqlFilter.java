package planit.massiverstandard.filter.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import planit.massiverstandard.filter.FilterUtil;
import planit.massiverstandard.unit.entity.Unit;

import java.util.Map;

@Getter
@Entity
@DiscriminatorValue(value = "SQL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "massiver_st_sql_filter")
public class SqlFilter extends Filter implements WhereFilter {

    private String sql;

    @Builder
    public SqlFilter(Unit unit, String name, int order, String sql) {
        super(unit, name, order);
        this.sql = sql;
    }

    @Override
    public void addCondition(StringBuilder sql) {
        // SQL WHERE 절을 추가합니다.
        sql.append(" AND ").append(this.sql);
    }

    @Override
    public Object[] process(Object[] item) {

        return item;
    }

}
