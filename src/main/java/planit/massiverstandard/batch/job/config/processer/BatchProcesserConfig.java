package planit.massiverstandard.batch.job.config.processer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import planit.massiverstandard.columntransform.entity.ColumnTransform;
import planit.massiverstandard.filter.entity.Filter;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.service.FindUnit;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchProcesserConfig {

    private final FindUnit findUnit;

    @Bean
    @StepScope
    public ItemProcessor<Object[], Object[]> processor(
        @Value("#{jobParameters['unitId']}") String unitId,
        List<ColumnTransform> columnTransforms
    ) {
        Unit unit = findUnit.byId(UUID.fromString(unitId));
        List<Filter> filters = unit.getFilters();

        List<String> headerList = columnTransforms.stream()
            .map(ColumnTransform::getTargetColumn)
            .toList();

        Map<String,Integer> columnIndexMap = new HashMap<>();
        for (int i = 0; i < headerList.size(); i++) {
            columnIndexMap.put(headerList.get(i), i);
        }

        return item -> {

            for (Filter filter : filters) {
                item = filter.process(item);

                if (item == null) {
                    return null;
                }
            }

            // 2) 컬럼 변환 적용
            for (ColumnTransform transform : columnTransforms) {
                int tgtIdx = columnIndexMap.get(transform.getTargetColumn());

                Object rawValue = item[tgtIdx];
                if (rawValue == null) continue;

                String str = rawValue.toString();

                switch (transform.getTransformType()) {
                    case NONE:
                        break;
                    case TO_DATE, TO_TIMESTAMP:
                        // 예: 포맷 패턴이 yyyyMMdd 라면
                        DateTimeFormatter inFmt  = DateTimeFormatter.ofPattern(transform.getFormatPattern());
                        item[tgtIdx] = LocalDate.parse(str, inFmt);
                        break;
//                    case SUBSTRING:
//                        int start = transform.getSubstrStart();
//                        int len   = transform.getSubstrLength();
//                        result = str.substring(start, Math.min(start + len, str.length()));
//                        break;
//                    case REGEX:
//                        result = str.replaceAll(transform.getRegexPattern(), transform.getReplacement());
//                        break;
//                    case CUSTOM:
//                        // customExpression 에 담긴 SQL 스니펫 등을 처리할 로직 필요
//                        result = applyCustom(str, transform.getCustomExpression());
//                        break;
                    default:
                        throw new IllegalArgumentException("Unknown transform: " + transform.getTransformType());
                }

            }

            return item;
        };

    }

}
