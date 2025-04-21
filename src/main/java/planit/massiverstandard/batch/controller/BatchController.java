package planit.massiverstandard.batch.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import planit.massiverstandard.batch.BatchJobLauncher;
import planit.massiverstandard.batch.usecase.ExecuteGroup;

import java.util.UUID;

@Tag(name = "배치", description = "배치 API")
@RestController
@RequestMapping("/api/v1/batch")
@RequiredArgsConstructor
public class BatchController {

    private final BatchJobLauncher batchJobLauncher;
    private final ExecuteGroup executeGroup;

    @Operation(
        summary = "UNIT 실행",
        description = "UNIT 실행 API"
    )
    @PostMapping("/run/unit/{unitId}")
    public ResponseEntity<String> runUnit(@PathVariable UUID unitId) {

        try {
            batchJobLauncher.runBatchJob(unitId);
            return ResponseEntity.ok("Job started");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @Operation(
        summary = "GROUP 실행",
        description = "GROUP 실행 API"
    )
    @PostMapping("/run/group/{groupId}")
    public ResponseEntity<String> runGroup(@PathVariable UUID groupId) {

        try {
            executeGroup.executeGroup(groupId);
            return ResponseEntity.ok("Job started");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
