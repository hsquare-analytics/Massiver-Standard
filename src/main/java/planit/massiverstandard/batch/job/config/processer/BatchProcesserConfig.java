package planit.massiverstandard.batch.job.config.processer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import planit.massiverstandard.filter.entity.Filter;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.service.FindUnit;

import java.util.List;
import java.util.UUID;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchProcesserConfig {

    private final FindUnit findUnit;

    @Bean
    @StepScope
    public ItemProcessor<Object[], Object[]> processor(
        @Value("#{jobParameters['unitId']}") String unitId
    ) {
        Unit unit = findUnit.byId(UUID.fromString(unitId));
        List<Filter> filters = unit.getFilters();

        return item -> {

            for (Filter filter : filters) {
                item = filter.process(item);

                if (item == null) {
                    return null;
                }
            }

            return item;
        };

    }

}
