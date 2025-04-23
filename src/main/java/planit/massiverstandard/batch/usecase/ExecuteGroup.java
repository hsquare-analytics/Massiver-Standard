package planit.massiverstandard.batch.usecase;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 설명: 배치 실행 그룹
 * 작성일: 2025. 04. 03.
 * 작성자: Mason
 */
public interface ExecuteGroup {

    /**
     * 배치 실행
     *
     * @param groupId 그룹 ID
     */
    void executeGroup(UUID groupId);

    /**
     * 비동기 배치 실행
     *
     * @param groupId 그룹 ID
     */
    CompletableFuture<Void> asyncGroup(UUID groupId);
}
