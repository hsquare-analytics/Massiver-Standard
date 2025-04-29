package planit.massiverstandard.datasource.util;

import planit.massiverstandard.datasource.entity.DataSource;

public class DataSourceResolver {

    public static javax.sql.DataSource createDataSource(DataSource dataSource) {
        String url;
        String driverClassName;

        switch (dataSource.getType()) {
            case H2_TCP -> {
                // 예: jdbc:h2:tcp://localhost:9092/~/massiver
                url = String.format("jdbc:h2:tcp://%s:%s/%s", dataSource.getHost(), dataSource.getPort(), dataSource.getDatabase());
                driverClassName = "org.h2.Driver";
            }
            case H2_MEM -> {
                // 예: jdbc:h2:mem:sourceDb;DB_CLOSE_DELAY=-1
                url = String.format("jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1", dataSource.getDatabase());
                driverClassName = "org.h2.Driver";
            }
            case MYSQL -> {
                // 예: jdbc:mysql://localhost:3306/mydb
                url = String.format("jdbc:mysql://%s:%s/%s", dataSource.getHost(), dataSource.getPort(), dataSource.getDatabase());
                driverClassName = "com.mysql.cj.jdbc.Driver";
            }
            case POSTGRESQL -> {
                // 예: jdbc:postgresql://localhost:5432/mydb
                url = String.format("jdbc:postgresql://%s:%s/%s", dataSource.getHost(), dataSource.getPort(), dataSource.getDatabase());
                driverClassName = "org.postgresql.Driver";
            }
            // 필요에 따라 다른 DB 타입 추가…
            default -> throw new IllegalArgumentException("Unsupported DataSourceType: " + dataSource.getType());
        }

        return org.springframework.boot.jdbc.DataSourceBuilder.create()
            .driverClassName(driverClassName)
            .url(url)
            .username(dataSource.getUsername())
            .password(dataSource.getPassword())
            .build();
    }
}
