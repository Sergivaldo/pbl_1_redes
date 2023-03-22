package br.uefs.http.message.header;

public enum Headers {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    DATE("Date");

    private final String value;

    Headers(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
