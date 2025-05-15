package planit.massiverstandard.datasource.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import planit.massiverstandard.datasource.entity.DataSourceType;

@Getter
@Data
public class DataSourceRequest {

    @Schema(description = "데이터베이스 이름", example = "매시버 데이터 소스")
    private String name;

    @Schema(description = "데이터베이스 타입", example = "H2_TCP")
    private DataSourceType type;

    @Schema(description = "데이터베이스 이름", example = "massiver-source")
    private String database;

    @Schema(description = "데이터베이스 설정", example = "DATABASE_TO_UPPER=false")
    private String properties;

    @Schema(description = "호스트", example = "localhost")
    private String host;

    @Schema(description = "포트", example = "9092")
    private String port;

    @Schema(description = "사용자 이름", example = "massiver")
    private String username;

    @Schema(description = "비밀번호", example = "password")
    private String password;
}
