package planit.massiverstandard.exception.filter;

import planit.massiverstandard.exception.DomainException;

public class UnsupportedFilterTypeException extends DomainException {
  public UnsupportedFilterTypeException(String message) {
    super(message);
  }
}
