package br.uefs.http.message;

import br.uefs.http.message.header.Headers;
import br.uefs.http.message.header.HttpHeader;
import br.uefs.util.Constants;
import br.uefs.util.DateTime;

import java.util.Iterator;
import java.util.Map;

/**
 * Classe que representa uma resposta HTTP
 */
public class HttpResponse {
    private HttpStatus status;
    private HttpHeader<String, String> header;
    private String body;

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public HttpHeader<String, String> getHeader() {
        return header;
    }

    public void setHeader(HttpHeader<String, String> header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public HttpResponse(HttpStatus status){
        this.status = status;
        this.body = "";
        this.header = new HttpHeader<>();
        setCommonHeaders();
    }

    public HttpResponse(HttpStatus status, String body) {
        this.status = status;
        this.body = body;
        this.header = new HttpHeader<>();
        setCommonHeaders();
    }

    public HttpResponse(HttpStatus status, HttpHeader<String, String> header, String body) {
        this.status = status;
        this.body = body;
        this.header = header;
        setCommonHeaders();
    }

    protected void setCommonHeaders(){
        header.put(Headers.DATE.getValue(), DateTime.currentDateTime());
    }

    /**
     * Gera uma string na estrutura de uma resposta HTTP
     * @return String com resposta HTTP
     */
    @Override
    public String toString(){
        final String CRLF = Constants.CRLF.getValue();
        final String HTTP_VERSION = Constants.HTTP_PROTOCOL_VERSION.getValue();
        String statusMessage = status.getMessage();
        String statusCode = status.getCode();
        StringBuilder builder = new StringBuilder();

        builder.append(HTTP_VERSION+" "+statusCode+" "+statusMessage+CRLF);

        Iterator<Map.Entry<String,String>> iterator = header.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String,String> map = iterator.next();
            builder.append(map.getKey()+": "+map.getValue()+CRLF);
        }
        builder.append(CRLF);
        builder.append(body);
        builder.append(CRLF);
        return builder.toString();
    }
}
