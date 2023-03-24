package br.uefs.util;

/**
 * Enum que contém algumas constantes utilizadas no sistema
 */
public enum Constants {
    CRLF("\r\n"),
    HTTP_PROTOCOL_VERSION("HTTP/1.1"),
    NORMAL_CONSUMPTION_LIMIT("60"),
    NORMAL_PRINCE_LIMIT("100");
    private String value;

    Constants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
