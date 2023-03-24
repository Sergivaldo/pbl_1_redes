package br.uefs.api_rest.exception;

/**
 * Classe que representa uma exceção que é lançada quando um método
 * passado na requisição não é aceito na rota definida
 */
public class NotAcceptableMethodException extends Exception{
    public NotAcceptableMethodException(String message) {
        super(message);
    }
}
