package planit.massiverstandard.datasource.service;

import planit.massiverstandard.datasource.entity.DataSource;

public interface FindRealDataSource {

    /**
     * 데이터 소스 캐시에서 데이터 소스를 가져오거나 생성합니다.
     * @param dataSource 데이터 소스 정보
     * @return 데이터 소스
     */
    javax.sql.DataSource getOrCreateDataSource(DataSource dataSource);
}
