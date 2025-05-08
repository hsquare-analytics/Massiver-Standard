package planit.massiverstandard.exception.batch;

import planit.massiverstandard.exception.BatchException;

public class ApiFilterWrongResponseException extends BatchException {
    public ApiFilterWrongResponseException(String message) {
        super(message);
    }
}
