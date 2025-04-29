package planit.massiverstandard.datasource.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import planit.massiverstandard.BaseEntity;

import java.util.UUID;

@Getter
@Entity
@Table(name = "massiver_st_data_source")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DataSource extends BaseEntity {

    @Id
    private UUID id;

    @Comment("DataSource 명")
    private String name;

    @Enumerated(EnumType.STRING)
    @Comment("DB종류")
    @Column(nullable = false, length = 20)
    private DataSourceType type;

    @Comment("호스트")
    @Column(nullable = false)
    private String host;

    @Comment("포트")
    private String port;

    @Comment("데이터베이스")
    private String database;

    @Comment("사용자명")
    private String username;

    @Comment("비밀번호")
    @JsonIgnore // JSON 직렬화 시 비밀번호 감춤
    @Column(nullable = false, length = 255)
    private String password;

    @Builder
    public DataSource(String name, DataSourceType type, String host, String port, String database, String username, String password) {

        this.id = UUID.randomUUID();
        this.name = name;
        this.type = type;
        this.database = database;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;

    }

    public void update(DataSource entity) {
        this.name = entity.getName();
        this.type = entity.getType();
        this.host = entity.getHost();
        this.port = entity.getPort();
        this.database = entity.getDatabase();
        this.username = entity.getUsername();
        this.password = entity.getPassword();
    }
}
