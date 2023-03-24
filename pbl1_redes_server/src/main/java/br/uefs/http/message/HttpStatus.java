package br.uefs.http.message;

/**
 * Enum que contém status HTTP que foram utilizados na API REST
 */
public enum HttpStatus {
    OK("200","OK"),
    NOT_IMPLEMENTED("501","Not Implemented"),
    METHOD_NOT_ALLOWED("405","Method Not Allowed"),
    BAD_REQUEST("400","Bad request"),
    HTTP_VERSION_NOT_SUPPORTED("505","HTTP Version Not Supported"),
    HTTP_INTERNAL_SERVER_ERROR("500","Internal Server Error"),
    CREATED("201","Created"),
    NOT_FOUND("404","Not Found");

    private final String code;
    private final String message;

    HttpStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
