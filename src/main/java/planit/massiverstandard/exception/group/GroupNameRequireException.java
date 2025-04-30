package planit.massiverstandard.exception.group;

import planit.massiverstandard.exception.DomainException;

public class GroupNameRequireException extends DomainException {
    public GroupNameRequireException(String message) {
        super(message);
    }
}
