package planit.massiverstandard.exception;

public record ApiErrorResponse(
    int status,
    String message
) {}
