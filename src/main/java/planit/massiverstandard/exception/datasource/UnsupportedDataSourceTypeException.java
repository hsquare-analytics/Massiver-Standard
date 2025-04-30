package planit.massiverstandard.exception.datasource;

import planit.massiverstandard.exception.DomainException;

public class UnsupportedDataSourceTypeException extends DomainException {
  public UnsupportedDataSourceTypeException(String message) {
    super(message);
  }
}
