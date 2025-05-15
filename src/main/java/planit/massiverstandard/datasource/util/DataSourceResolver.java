package planit.massiverstandard.datasource.util;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.DataSourceBuilder;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.exception.datasource.UnsupportedDataSourceTypeException;

@Slf4j
public class DataSourceResolver {

    public static javax.sql.DataSource createDataSource(DataSource dataSource) {
        StringBuilder url = new StringBuilder();
        String driverClassName;

        switch (dataSource.getType()) {
            case H2_TCP -> {
                // 예: jdbc:h2:tcp://localhost:9092/~/massiver
                url.append(String.format("jdbc:h2:tcp://%s:%s/%s", dataSource.getHost(), dataSource.getPort(), dataSource.getDatabase()));
                driverClassName = "org.h2.Driver";
            }
            case H2_MEM -> {
                // 예: jdbc:h2:mem:sourceDb;DB_CLOSE_DELAY=-1
                url.append(String.format("jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1", dataSource.getDatabase()));
                driverClassName = "org.h2.Driver";
            }
            case MYSQL -> {
                // 예: jdbc:mysql://localhost:3306/mydb
                url.append(String.format("jdbc:mysql://%s:%s/%s", dataSource.getHost(), dataSource.getPort(), dataSource.getDatabase()));
                driverClassName = "com.mysql.cj.jdbc.Driver";
            }
            case POSTGRESQL -> {
                // 예: jdbc:postgresql://localhost:5432/mydb
                url.append(String.format("jdbc:postgresql://%s:%s/%s", dataSource.getHost(), dataSource.getPort(), dataSource.getDatabase()));
                driverClassName = "org.postgresql.Driver";
            }
            case VERTICA -> {
                // 예: jdbc:vertica://localhost:5433/mydb
                url.append(String.format("jdbc:vertica://%s:%s/%s", dataSource.getHost(), dataSource.getPort(), dataSource.getDatabase()));
                driverClassName = "com.vertica.jdbc.Driver";
            }
            // 필요에 따라 다른 DB 타입 추가…
            default -> throw new UnsupportedDataSourceTypeException("지원하지 않는 데이터베이스 타입입니다: " + dataSource.getType());
        }

        // 설정
        if (dataSource.getProperties() != null) {
            url.append(";").append(dataSource.getProperties());
        }

        // HikariDataSource 로 빌드
        HikariDataSource ds = DataSourceBuilder.create()
            .type(HikariDataSource.class)      // HikariCP 명시
            .driverClassName(driverClassName)
            .url(url.toString())
            .username(dataSource.getUsername())
            .password(dataSource.getPassword())
            .build();

        // auto-commit 꺼서 커서 스트리밍 활성화
        ds.setAutoCommit(false);

        // (디버그용) 꼭 로그로 한 번 찍어 보세요
        log.info(">> Created DataSource for {}://{} (autoCommit={})",
            dataSource.getType(), url, ds.isAutoCommit());

        return ds;
    }
}
