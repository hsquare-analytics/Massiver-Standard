package planit.massiverstandard.schedule.usecase;

import java.util.List;
import java.util.UUID;

/**
 * 설명: 스케줄러 황성화
 * 작성일: 2025. 04. 03.
 * 작성자: Mason
 */
public interface ActiveSchedule {

    /**
     * 스케줄러 활성화
     *
     * @param groupID 그룹 ID
     * @return 스케줄러 활성화된 crons 표현식
     */
    List<String> active(UUID groupID);

    /**
     * 스케줄러 비황성화
     *
     * @param groupId 그룹 ID
     * @return 삭제 성공 여부
     */
    void deActive(UUID groupId);
}
