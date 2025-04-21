package planit.massiverstandard.filter.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import planit.massiverstandard.filter.FilterUtil;
import planit.massiverstandard.unit.entity.Unit;

import java.util.Map;

@Getter
@Entity
@DiscriminatorValue(value = "SQL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SqlFilter extends Filter {

    private String sql;

    public SqlFilter(Unit unit, String name, int order, String sql) {
        super(unit, name, order);
        this.sql = sql;
    }

    @Override
    public Map<String, Object> process(Map<String, Object> item) {
        // WHERE ID < 5

        // where 제거
        String where = sql.replace("WHERE", "").trim();

        String key = null;
        String operator = null;
        String value = null;

        // 연산자가 포함된 부분 찾기
        for (String op : FilterUtil.operators) {
            if (where.contains(op)) {
                operator = op;
                String[] split = where.split(operator);
                key = split[0].trim();
                value = split[1].trim();
                break;
            }
        }

        if (key == null || operator == null || value == null) {
            return null;
        }

        // 필터링
        Object rowValue = item.get(key);

        // todo: implement date groupUnitType filter

        if (FilterUtil.compareValues(rowValue, operator, value)) {
            return item;
        }

        return null;
    }

}
