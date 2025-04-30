package planit.massiverstandard.exception.unit;

import planit.massiverstandard.exception.DomainException;

public class UnitNotFoundException extends DomainException {
    public UnitNotFoundException(String message) {
        super(message);
    }
}
