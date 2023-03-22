package br.uefs.api_rest.exception;

import com.google.gson.JsonObject;

public class UnavaibleInvoiceException extends Exception {

    private JsonObject json;

    public UnavaibleInvoiceException(JsonObject json) {
        this.json = json;
    }

    public JsonObject getJson() {
        return json;
    }
}
