package br.uefs.http.message;

import br.uefs.http.message.header.HttpHeader;
import br.uefs.util.Constants;

import java.util.Iterator;
import java.util.Map;

public class HttpRequest {
    private HttpMethod method;
    private String path;
    private HttpHeader<String, String> header;
    private String body;
    private String httpVersion;
    private Map<String, String> queryParameters;

    private HttpRequest(HttpMethod method, String path, HttpHeader<String, String> header, String body, String httpVersion, Map<String, String> queryParameters) {
        this.method = method;
        this.path = path;
        this.header = header;
        this.body = body;
        this.httpVersion = httpVersion;
        this.queryParameters = queryParameters;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(Map<String, String> queryParameters) {
        this.queryParameters = queryParameters;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    @Override
    public String toString() {
        final String CRLF = Constants.CRLF.getValue();
        final String HTTP_VERSION = Constants.HTTP_PROTOCOL_VERSION.getValue();
        StringBuilder builder = new StringBuilder();
        builder.append(method + " " + path + " " + HTTP_VERSION + CRLF);

        if (header != null || !header.isEmpty()) {
            Iterator<Map.Entry<String, String>> iterator = header.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> map = iterator.next();
                builder.append(map.getKey() + ": " + map.getValue() + CRLF);
            }
        }

        builder.append(CRLF);
        builder.append(body);

        return builder.toString();
    }

    public static class Builder {
        private HttpMethod method;
        private String path;
        private HttpHeader<String, String> header;
        private String body;
        private String httpVersion;
        private Map<String, String> queryParameters;

        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder header(HttpHeader header) {
            this.header = header;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder httpVersion(String httpVersion) {
            this.httpVersion = httpVersion;
            return this;
        }

        public Builder queryParameters(Map<String, String> queryParameters) {
            this.queryParameters = queryParameters;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(method, path, header, body, httpVersion, queryParameters);
        }
    }
}
