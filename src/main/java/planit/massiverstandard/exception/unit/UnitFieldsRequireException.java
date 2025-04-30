package planit.massiverstandard.exception.unit;

import planit.massiverstandard.exception.DomainException;

public class UnitFieldsRequireException extends DomainException {
    public UnitFieldsRequireException(String message) {
        super(message);
    }
}
