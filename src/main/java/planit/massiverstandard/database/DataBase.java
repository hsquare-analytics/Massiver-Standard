package planit.massiverstandard.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;
import java.util.UUID;

@ToString
@Getter
@Entity
@Table(name = "databases")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DataBase {

    @Id
    private UUID id;

    @Comment("DB명")
    private String name;

    @Comment("DB종류")
    private String type;

    @Comment("호스트")
    @Column(nullable = false)
    private String host;

    @Comment("포트")
    private String port;

    @Comment("사용자명")
    private String username;

    @Comment("비밀번호")
    @JsonIgnore // JSON 직렬화 시 비밀번호 감춤
    @Column(nullable = false, length = 255)
    private String password;

    @Comment("설명")
    private String description;

    @Builder
    public DataBase(String name, String type, String host, String port, String username, String password, String description) {

        this.id = UUID.randomUUID();
        this.name = name;
        this.type = type;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.description = description;

    }

    public String toUrl() {
        return "jdbc:" + type + "://" + host + ":" + port + "/" + name;
    }

    public DataSource createDataSource() {
        return DataSourceBuilder.create()
            .url("jdbc:" + type + "://" + host + "/" + name)
            .username(username)
            .password(password)
            .build();
    }

}
