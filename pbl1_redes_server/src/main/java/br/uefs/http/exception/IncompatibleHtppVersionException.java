package br.uefs.http.exception;

/**
 * Classe que representa uma exceção que é lançada quando a versão do HTTP passada
 * na requisição não é compatível com a versão HTTP da API REST
 */
public class IncompatibleHtppVersionException extends Exception{

    public IncompatibleHtppVersionException() {
        super();
    }
}
