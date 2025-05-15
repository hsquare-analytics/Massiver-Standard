package planit.massiverstandard.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.datasource.entity.DataSourceType;
import planit.massiverstandard.datasource.repository.DataSourceRepository;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer {

    private final DataSourceRepository dataSourceRepository;

    /**
     * DataSource 엔티티 생성 후 저장
     *
     * @param name        식별용 이름
     * @param type        데이터소스 종류 (H2_TCP, H2_MEM 등)
     * @param database    DB 이름 또는 URL 접미부 (예: "~/massiver", "mem:sourceDb")
     * @param host        접속 호스트 (in-mem 이면 null)
     * @param port        접속 포트 (in-mem 이면 null)
     * @param username    계정
     * @param password    비밀번호
     */
    public DataSource init(
        String name,
        DataSourceType type,
        String database,
        String properties,
        String host,
        String port,
        String username,
        String password
    ) {
        DataSource ds = DataSource.builder()
            .name(name)
            .type(type)
            .database(database)    // ← 여기(database) 로 바뀌었습니다
            .properties(properties)
            .host(host)
            .port(port)
            .username(username)
            .password(password)
            .build();

        return dataSourceRepository.save(ds);
    }
}
