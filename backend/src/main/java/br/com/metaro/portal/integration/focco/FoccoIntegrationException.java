package br.com.metaro.portal.integration.focco;

public class FoccoIntegrationException extends RuntimeException {
    public FoccoIntegrationException(String message) {
        super(message);
    }

    public FoccoIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
