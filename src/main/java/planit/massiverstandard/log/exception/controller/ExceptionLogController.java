package planit.massiverstandard.log.exception.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import planit.massiverstandard.config.PageResult;
import planit.massiverstandard.log.exception.entity.ExceptionLog;
import planit.massiverstandard.log.exception.service.FindExceptionLog;

@Tag(name = "Exception Log", description = "예외 로그 API")
@RestController
@RequestMapping("/api/exception-log")
@RequiredArgsConstructor
public class ExceptionLogController {

    private final FindExceptionLog findExceptionLog;

    @Operation(summary = "예외 로그 조회", description = "예외 로그를 최신순으로 조회합니다.")
    @GetMapping
    public PageResult<ExceptionLog> getExceptionLogs(@RequestParam int page,
                                                     @RequestParam int size) {
        Page<ExceptionLog> allBLatest = findExceptionLog.findAllBLatest(Pageable.ofSize(size).withPage(page));

        return PageResult.<ExceptionLog>builder()
            .totalElements(allBLatest.getTotalElements())
            .totalPages(allBLatest.getTotalPages())
            .pageNumber(allBLatest.getNumber())
            .pageSize(allBLatest.getSize())
            .content(allBLatest.getContent())
            .build();
    }
}
