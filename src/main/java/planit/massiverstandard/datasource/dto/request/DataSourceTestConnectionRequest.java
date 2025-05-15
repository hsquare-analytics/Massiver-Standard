package planit.massiverstandard.datasource.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import planit.massiverstandard.datasource.entity.DataSourceType;

public record DataSourceTestConnectionRequest(
    @Schema(description = "데이터 소스 타입", example = "H2_TCP")
    DataSourceType type,
    @Schema(description = "데이터베이스 이름", example = "massiver-source")
    String database,
    @Schema(description = "데이터베이스 설정", example = "DATABASE_TO_UPPER=false")
    String properties,
    @Schema(description = "호스트", example = "localhost")
    String host,
    @Schema(description = "포트", example = "9092")
    String port,
    @Schema(description = "사용자 이름", example = "massiver")
    String username,
    @Schema(description = "비밀번호", example = "password")
    String password
) {
}
