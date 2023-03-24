package br.uefs.api_rest.exception;

/**
 * Classe que representa uma exceção que é lançada quando uma rota
 * inválida é passada
 */
public class InvalidRequestPathException extends Exception {
    public InvalidRequestPathException(String message) {
        super(message);
    }
}
