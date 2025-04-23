package planit.massiverstandard.log.dto.request;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 설명: 배치 잡 로그 검색을 위한 DTO
 * @param from 시작 startTime
 * @param to 종료 startTime
 * @param status 상태
 */
public record BatchJobLogSearchDto (
    LocalDateTime from,
    LocalDateTime to,
    List<String> status
){

}
