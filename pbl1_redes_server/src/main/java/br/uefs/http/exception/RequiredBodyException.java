package br.uefs.http.exception;

public class RequiredBodyException extends Exception{
    public RequiredBodyException(String message) {
        super(message);
    }
}
