package planit.massiverstandard.config;

import lombok.Builder;

import java.util.List;

public record PageResult<T>(
        long totalElements,
        int totalPages,
        int pageNumber,
        int pageSize,
        List<T> content
) {

    @Builder
    public PageResult {}
}
