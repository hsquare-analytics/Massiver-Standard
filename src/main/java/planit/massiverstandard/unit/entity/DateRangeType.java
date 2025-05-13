package planit.massiverstandard.unit.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.function.Supplier;


@Getter
@RequiredArgsConstructor
public enum DateRangeType {
    CURRENT_MONTH_FIRST_DAY("이번 달 첫 날",
        () -> LocalDate.now().withDayOfMonth(1)),
    CURRENT_MONTH_LAST_DAY("이번 달 마지막 날",
        () -> LocalDate.now().with(TemporalAdjusters.lastDayOfMonth())),
    CURRENT_YEAR_FIRST_DAY("올해 첫 날",
        () -> LocalDate.now().with(TemporalAdjusters.firstDayOfYear())),
    CURRENT_YEAR_LAST_DAY("올해 마지막 날",
        () -> LocalDate.now().with(TemporalAdjusters.lastDayOfYear())),
    YESTERDAY("어제",
        () -> LocalDate.now().minusDays(1)),
    TODAY("오늘",
        LocalDate::now),
    LAST_MONTH_FIRST_DAY("지난 달 첫 날",
        () -> LocalDate.now().minusMonths(1).withDayOfMonth(1)),
    LAST_MONTH_LAST_DAY("지난 달 마지막 날",
        () -> LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()));

    private final String description;
    private final Supplier<LocalDate> dateSupplier;

    public static DateRangeType of(String name) {
        for (DateRangeType dateRangeType : DateRangeType.values()) {
            if (dateRangeType.name().equalsIgnoreCase(name)) {
                return dateRangeType;
            }
        }
        throw new IllegalArgumentException("Invalid DateRangeType name: " + name);
    }

    /**
     * enum 이름에 대응하는 날짜(LocalDate)를 계산해 반환합니다.
     */
    public LocalDate getDate() {
        return dateSupplier.get();
    }
}
