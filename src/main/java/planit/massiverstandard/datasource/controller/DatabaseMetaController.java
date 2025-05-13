package planit.massiverstandard.datasource.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import planit.massiverstandard.datasource.dto.request.ProcedureQueryRequest;
import planit.massiverstandard.datasource.dto.response.ColumnInfoResult;
import planit.massiverstandard.datasource.dto.response.ProcedureArgumentResponse;
import planit.massiverstandard.datasource.dto.response.ProcedureResponse;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.datasource.service.DataSourceService;
import planit.massiverstandard.datasource.service.ExecuteSqlScript;
import planit.massiverstandard.datasource.service.FindDataSource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

    @Operation(summary = "프로시저 조회", description = "프로시저를 조회합니다.")
    @GetMapping("/{dbId}/{schema}/procedure")
    public List<ProcedureResponse> getProcedures(@PathVariable(name = "dbId") UUID dbId,
                                      @PathVariable(name = "schema") String schema) {
        DataSource byId = dataSourceService.findById(dbId);

        return executeSqlScript.getProcedures(byId, schema).stream()
            .map(pc -> {
                // 1) 인자 문자열(sentence)를 ,(쉼표)로 split. 괄호 안의 쉼표는 무시
                String argString = pc.procedureArguments();
                List<ProcedureArgumentResponse> argsList = Optional.ofNullable(argString)
                    .filter(s -> !s.isBlank())
                    .map(s -> Arrays.stream(s.split(",(?![^()]*\\))"))
                        .map(String::trim)
                        .map(item -> {
                            // 2) 공백 첫 번째를 경계로 name, typeRaw 분리
                            String[] parts = item.split("\\s+", 2);
                            String name = parts[0];
                            String typeRaw = parts.length > 1 ? parts[1] : "";
                            // 3) 타입 뒤 숫자(괄호 포함) 제거
                            String type = typeRaw.replaceAll("\\(.+\\)", "").trim();
                            return new ProcedureArgumentResponse(name, type);
                        })
                        .toList()
                    )
                    .orElseGet(List::of);

                return new ProcedureResponse(pc.procedureName(), argsList);
            })
            .toList();
    }

    @Operation(summary = "프로시저 쿼리 조회", description = "프로시저 쿼리를 조회합니다.")
    @PostMapping("/{dbId}/{schema}/{procedure}/query")
    public String getProcedureQuery(@PathVariable(name = "dbId") UUID dbId,
                                    @PathVariable(name = "schema") String schema,
                                    @PathVariable(name = "procedure") String procedure,
                                    @RequestBody ProcedureQueryRequest req) {
        DataSource byId = dataSourceService.findById(dbId);
        return executeSqlScript.getProcedureQuery(byId, schema, procedure, req.arguments());
    }

}
