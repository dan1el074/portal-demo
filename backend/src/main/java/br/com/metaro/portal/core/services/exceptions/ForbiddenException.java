package br.com.metaro.portal.core.services.exceptions;

public class forbiddenException extends RuntimeException {
  public forbiddenException(String message) {
    super(message);
  }
}
