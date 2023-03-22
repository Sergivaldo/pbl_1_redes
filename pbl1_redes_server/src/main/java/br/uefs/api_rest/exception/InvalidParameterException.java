package br.uefs.api_rest.exception;

public class InvalidParameterException extends Exception {
    public InvalidParameterException() {
        super("Invalid query parameter");
    }
}
