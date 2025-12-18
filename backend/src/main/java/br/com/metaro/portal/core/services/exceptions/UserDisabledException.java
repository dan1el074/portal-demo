package br.com.metaro.portal.core.services.exceptions;

public class UserDisabledException extends RuntimeException {
    public UserDisabledException() {
        super("User disabled");
    }
}
