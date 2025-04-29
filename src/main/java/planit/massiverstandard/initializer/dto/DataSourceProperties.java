package planit.massiverstandard.initializer.dto;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "init.datasource")
@Data
public class DataSourceProperties {

    private boolean enabled;
    private DataProps source;
    private DataProps target;

    @Data
    public static class DataProps {
        private String database;
        private String host;
        private String port;
        private String username;
        private String password;
    }
}
