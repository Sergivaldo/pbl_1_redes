package br.uefs.http.exception;

/**
 * Classe que representa uma exceção que é lançada quando um corpo obrigatório não é passado
 */
public class RequiredBodyException extends Exception{
    public RequiredBodyException(String message) {
        super(message);
    }
}
