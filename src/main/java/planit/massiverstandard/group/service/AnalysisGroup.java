package planit.massiverstandard.group.service;

import java.util.UUID;

/**
 * 설명: 그룹 분석
 * 작성일: 2025. 04. 10.
 * 작성자: Mason
 */
public interface AnalysisGroup {
    /**
     * 그룹 분석
     *
     * @param groupId 그룹 id
     * @return 분석 결과
     */
    void byId(UUID groupId);
}
