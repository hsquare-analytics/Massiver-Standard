package planit.massiverstandard.batch.usecase;

import java.util.UUID;

/**
 * 설명: 스케줄러 등록 인터페이스
 * 작성일: 2025. 04. 03.
 * 작성자: Mason
 */
public interface ExecuteSchedule {

    /**
     * 스케줄러 등록
     *
     * @param groupID 그룹 ID
     */
    void active(UUID groupID);

    /**
     * 스케줄러 삭제
     *
     * @param groupId 그룹 ID
     * @return 삭제 성공 여부
     */
    boolean deActive(UUID groupId);
}
