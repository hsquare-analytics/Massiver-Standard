package planit.massiverstandard.batch.usecase;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 설명: 배치 실행 유닛
 * 작성일: 2025. 04. 03.
 * 작성자: Mason
 */
public interface ExecuteUnit {

    /**
     * 배치 실행
     *
     * @param unitId 유닛 ID
     */
    CompletableFuture<Void> asyncUnit(UUID unitId);
}
