package planit.massiverstandard.batch.usecase;

import org.quartz.Trigger;

import java.util.List;
import java.util.UUID;

/**
 * 설명: 스케줄러 작동 모니터링 인터페이스
 * 작성일: 2025. 04. 03.
 * 작성자: Mason
 */
public interface CheckSchedule {

    /**
     * 스케줄러 작동 모니터링
     *
     * @param groupId 그룹 ID
     * @return 작동 중인 트리거 목록
     */
    List<? extends Trigger> check(UUID groupId);
}
