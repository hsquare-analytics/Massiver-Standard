package planit.massiverstandard.datasource.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import planit.massiverstandard.config.Mapper;
import planit.massiverstandard.datasource.dto.request.DataSourceCreateDto;
import planit.massiverstandard.datasource.dto.request.DataSourceRequest;
import planit.massiverstandard.datasource.dto.request.DataSourceTestConnectionDto;
import planit.massiverstandard.datasource.dto.request.DataSourceTestConnectionRequest;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.datasource.service.CommandDataSource;
import planit.massiverstandard.datasource.service.DataSourceService;
import planit.massiverstandard.datasource.service.FindDataSource;

import java.util.List;
import java.util.UUID;

@Tag(name = "DataBase", description = "데이터 베이스를 관리합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/datasources")
public class DataSourceController {

    private final DataSourceService dataSourceService;
    private final CommandDataSource commandDataSource;
    private final FindDataSource findDataSource;

    private final Mapper mapper;

    @Operation(summary = "DataSource 연결 테스트", description = "데이터베이스 연결 테스트를 수행합니다.")
    @PostMapping("/test-connection")
    public void testConnection(@RequestBody DataSourceTestConnectionRequest request) {
        DataSourceTestConnectionDto dto = mapper.toDataSourceTestConnectionDto(request);
        commandDataSource.testConnection(dto);
    }

    @Operation(summary = "데이터베이스 생성", description = "데이터베이스를 생성합니다.")
    @PostMapping
    public DataSource createDataBase(@RequestBody DataSourceRequest dataBaseDto) {
        DataSourceCreateDto createDto = mapper.toDataSourceCreateDto(dataBaseDto);
        return commandDataSource.create(createDto);
    }

    @Operation(summary = "데이터소스 수정", description = "데이터소스를 수정합니다.")
    @PatchMapping("/{id}")
    public void updateDataBase(@PathVariable UUID id, @RequestBody DataSourceRequest dataBaseDto) {
        DataSourceCreateDto updateDto = mapper.toDataSourceCreateDto(dataBaseDto);
        commandDataSource.update(id, updateDto);
    }

    @Operation(summary = "데이터베이스 목록 조회", description = "데이터베이스 목록을 조회합니다.")
    @GetMapping
    public List<DataSource> findAll() {
        return dataSourceService.findAll();
    }

    @Operation(summary = "데이터베이스 단건 조회", description = "데이터베이스를 단건 조회합니다.")
    @GetMapping("/{id}")
    public DataSource findById(@PathVariable UUID id) {
        return findDataSource.byId(id);
    }


}
