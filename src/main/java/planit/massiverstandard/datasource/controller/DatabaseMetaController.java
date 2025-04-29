package planit.massiverstandard.datasource.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import planit.massiverstandard.datasource.dto.response.ColumnInfoResult;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.datasource.service.DataSourceService;
import planit.massiverstandard.datasource.service.ExecuteSqlScript;
import planit.massiverstandard.datasource.service.FindDataSource;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/datasource-meta")
@RequiredArgsConstructor
public class DatabaseMetaController {

    private final DataSourceService dataSourceService;
    private final ExecuteSqlScript executeSqlScript;
    private final FindDataSource findDataSource;

    @Operation(summary = "PK 조회", description = "테이블의 PK 정보를 조회합니다.")
    @GetMapping("/{dbId}/{schema}/{table}/pk")
    public List<String> getSchema(
        @PathVariable(name = "dbId") UUID dbId,
        @PathVariable(name = "schema") String schema,
        @PathVariable(name = "table") String table
    ) {
        DataSource dataSource = findDataSource.byId(dbId);
        return executeSqlScript.getPkList(dataSource, schema, table);
    }

    @Operation(summary = "데이터베이스 스키마 조회", description = "데이터베이스의 스키마를 조회합니다.")
    @GetMapping("/{dbId}/schema")
    public List<String> getSchema(@PathVariable(name = "dbId") UUID dbId) {
        DataSource byId = dataSourceService.findById(dbId);
        return executeSqlScript.getSchemas(byId);
    }

    @Operation(summary = "스키마의 테이블 조회", description = "데이터베이스의 테이블을 조회합니다.")
    @GetMapping("/{dbId}/{schema}/table")
    public List<String> getTables(@PathVariable(name = "dbId") UUID dbId,
                                  @PathVariable(name = "schema") String schema) {
        DataSource byId = dataSourceService.findById(dbId);
        return executeSqlScript.getTables(byId, schema);
    }

    @Operation(summary = "데이터베이스 컬럼 조회", description = "데이터베이스의 컬럼을 조회합니다.")
    @GetMapping("/{dbId}/{schema}/{table}/column")
    public List<ColumnInfoResult> getColumns(@PathVariable(name = "dbId") UUID dbId,
                                             @PathVariable(name = "schema") String schema,
                                             @PathVariable(name = "table") String table) {
        DataSource byId = dataSourceService.findById(dbId);
        return executeSqlScript.getColumns(byId, schema, table);
    }
}
