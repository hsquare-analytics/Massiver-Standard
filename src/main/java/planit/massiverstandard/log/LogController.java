package planit.massiverstandard.log;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import planit.massiverstandard.log.dto.BatchJobInfo;
import planit.massiverstandard.log.dto.request.BatchJobLogSearchDto;
import planit.massiverstandard.log.service.FindLog;

@Tag(name = "Log", description = "Batch Job Execution Log API")
@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class LogController {

    private final FindLog findLog;

    @PostMapping
    public Page<BatchJobInfo> getLogs(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestBody BatchJobLogSearchDto searchDto) {

        Pageable pageable = PageRequest.of(page, size);

        return findLog.findLog(pageable, searchDto);

    }
}
