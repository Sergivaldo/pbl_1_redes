package br.uefs.api_rest.exception;

import com.google.gson.JsonObject;

/**
 * Classe que representa uma exceção que é lançada quando uma fatura do
 * cliente ainda não está disponível
 */
public class UnavaibleInvoiceException extends Exception {

    private JsonObject json;

    public UnavaibleInvoiceException(JsonObject json) {
        this.json = json;
    }

    public JsonObject getJson() {
        return json;
    }
}
