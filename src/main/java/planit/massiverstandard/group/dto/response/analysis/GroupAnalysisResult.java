package planit.massiverstandard.group.dto.response.analysis;

import java.util.List;

public record GroupAnalysisResult(
    // 테이블 비중
    List<TableRatio> tableRatios
    // 유닛 / 테이블 별 실행 시간
    // 데이터 흐름량
    // 유닛 리스트
    // 최근 실행 결과
    // 최근 실행 시간
) {
}
