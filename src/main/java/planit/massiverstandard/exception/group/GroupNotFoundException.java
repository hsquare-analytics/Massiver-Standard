package planit.massiverstandard.exception.group;

import planit.massiverstandard.exception.DomainException;

public class GroupNotFoundException extends DomainException {
    public GroupNotFoundException(String message) {
        super(message);
    }
}
