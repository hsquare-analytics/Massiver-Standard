package planit.massiverstandard.filter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpMethod;
import planit.massiverstandard.filter.entity.FilterHttpMethod;

@Getter
@Data
@AllArgsConstructor
public class FilterDto {

    @Schema(description = "필터명", example = "필터")
    private String name;

    @Schema(description = "필터 순서", example = "1")
    private int order;

    @Schema(description = "필터 타입", example = "SQL")
    private String type;

    @Schema(description = "SQL 필터 쿼리", example = "SELECT * FROM TABLE", nullable = true)
    private String sql;

    @Schema(description = "API 필터 메소드", example = "GET", nullable = true)
    private FilterHttpMethod method;

    @Schema(description = "API 필터 URL", example = "http://localhost:8080", nullable = true)
    private String url;
}
