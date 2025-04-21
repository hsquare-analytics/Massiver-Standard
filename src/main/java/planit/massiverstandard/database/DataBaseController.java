package planit.massiverstandard.database;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import planit.massiverstandard.database.dto.DataBaseRequest;

import java.util.List;
import java.util.UUID;

@Tag(name = "DataBase", description = "데이터 베이스를 관리합니다.")
@RestController
@RequestMapping("/api/databases")
public class DataBaseController {

    private final DataBaseService dataBaseService;
    private final DataBaseQueryService dataBaseQueryService;

    public DataBaseController(DataBaseService dataBaseService, DataBaseQueryService dataBaseQueryService) {
        this.dataBaseService = dataBaseService;
        this.dataBaseQueryService = dataBaseQueryService;
    }

    @Operation(summary = "데이터베이스 생성", description = "데이터베이스를 생성합니다.")
    @PostMapping
    public DataBase createDataBase(@RequestBody DataBaseRequest dataBaseDto) {
        DataBase dataBase = new DataBase(dataBaseDto.getName(), dataBaseDto.getType(), dataBaseDto.getHost(), dataBaseDto.getPort(), dataBaseDto.getUsername(), dataBaseDto.getPassword(), dataBaseDto.getDescription());
        return dataBaseService.createDataBase(dataBase);
    }

    @Operation(summary = "데이터베이스 목록 조회", description = "데이터베이스 목록을 조회합니다.")
    @GetMapping
    public List<DataBase> findAll() {
        return dataBaseService.findAll();
    }

    @Operation(summary = "데이터베이스 단건 조회", description = "데이터베이스를 단건 조회합니다.")
    @GetMapping("/{id}")
    public DataBase findById(@PathVariable UUID id) {
        return dataBaseService.findById(id);
    }

    @Operation(summary = "데이터베이스 스키마 조회", description = "데이터베이스의 스키마를 조회합니다.")
    @GetMapping("/{dbId}/schema")
    public List<String> getSchema(@PathVariable(name = "dbId") UUID dbId) {
        DataBase byId = dataBaseService.findById(dbId);
        return dataBaseQueryService.getSchemas(byId);
    }

    @Operation(summary = "데이터베이스 테이블 조회", description = "데이터베이스의 테이블을 조회합니다.")
    @GetMapping("/{dbId}/{schema}/table")
    public List<String> getTables(@PathVariable(name = "dbId") UUID dbId,
                                  @PathVariable(name = "schema") String schema) {
        DataBase byId = dataBaseService.findById(dbId);
        return dataBaseQueryService.getTables(byId, schema);
    }

    @Operation(summary = "데이터베이스 컬럼 조회", description = "데이터베이스의 컬럼을 조회합니다.")
    @GetMapping("/{dbId}/{schema}/{table}/column")
    public List<String> getColumns(@PathVariable(name = "dbId") UUID dbId,
                                   @PathVariable(name = "schema") String schema,
                                   @PathVariable(name = "table") String table) {
        DataBase byId = dataBaseService.findById(dbId);
        return dataBaseQueryService.getColumns(byId, schema, table);
    }

}
