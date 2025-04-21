package planit.massiverstandard.database.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class DataBaseRequest {

    @Schema(description = "데이터베이스 이름", example = "massiver-source")
    private String name;

    @Schema(description = "데이터베이스 타입", example = "h2:tcp")
    private String type;

    @Schema(description = "호스트", example = "localhost")
    private String host;

    @Schema(description = "포트", example = "9092")
    private String port;

    @Schema(description = "사용자 이름", example = "massiver")
    private String username;

    @Schema(description = "비밀번호", example = "password")
    private String password;

    @Schema(description = "설명", example = "매시버 데이터 소스")
    private String description;

}
