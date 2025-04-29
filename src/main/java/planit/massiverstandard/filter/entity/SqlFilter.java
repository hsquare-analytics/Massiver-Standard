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
    public Map<String, Object> process(Map<String, Object> item) {
//        // WHERE ID < 5
//
//        // where 제거
//        String where = sql.replace("WHERE", "").trim();
//
//        String key = null;
//        String operator = null;
//        String value = null;
//
//        // 연산자가 포함된 부분 찾기
//        for (String op : FilterUtil.operators) {
//            if (where.contains(op)) {
//                operator = op;
//                String[] split = where.split(operator);
//                key = split[0].trim();
//                value = split[1].trim();
//                break;
//            }
//        }
//
//        if (key == null || operator == null || value == null) {
//            return null;
//        }
//
//        // 필터링
//        Object rowValue = item.get(key);
//
//        // todo: implement date groupUnitType filter
//
//        if (FilterUtil.compareValues(rowValue, operator, value)) {
//            return item;
//        }
//
//        return null;

        return item;
    }

}
