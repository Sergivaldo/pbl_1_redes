package br.uefs.server.sockets.tcp;

import br.uefs.http.exception.InvalidQueryParameterException;
import br.uefs.http.message.header.HttpHeader;
import br.uefs.http.message.HttpMethod;
import br.uefs.http.message.HttpRequest;
import br.uefs.http.exception.IncompatibleHtppVersionException;
import br.uefs.http.exception.InvalidMethodException;
import br.uefs.util.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TCPMessage {
    private final InputStream clientMessage;
    private final BufferedReader bufferedReader;

    public TCPMessage(InputStream clientMessage) throws IOException{
        this.clientMessage = clientMessage;
        this.bufferedReader = new BufferedReader(new InputStreamReader(clientMessage));
    }

    /**
     * Tenta construir uma requisição HTTP verificando cada uma das
     * partes da mensagem recebida
     *
     * @return HttpRequest
     * @throws InvalidQueryParameterException Caso a mensagem não tenha parâmetros válidos
     * @throws IOException Caso ocorra erro de I/O
     * @throws InvalidMethodException Caso o método da requisição http seja inválido
     * @throws IncompatibleHtppVersionException Caso a versão do HTTP seja diferente de 1.1
     */
    public HttpRequest parseHttpRequest() throws InvalidQueryParameterException,IOException, InvalidMethodException, IncompatibleHtppVersionException {
        String line = bufferedReader.readLine();
        String[] requestFirstLine = line.split(" ");
        HttpRequest request = new HttpRequest.Builder()
                .method(setHttpRequestMethod(requestFirstLine[0]))
                .path(setHttpRequestPath(requestFirstLine[1]))
                .queryParameters(setHttpRequestParameters(requestFirstLine[1]))
                .httpVersion(setRequestHttpVersion(requestFirstLine[2]))
                .header(setHttpRequestHeaders())
                .body(setHttpBody()).build();
        return request;
    }

    /**
     *
     * @return HTTPHeader(HashMap)
     * @throws IOException
     */
    private HttpHeader<String,String> setHttpRequestHeaders() throws IOException{
        String line;
        HttpHeader<String,String> headers = new HttpHeader<String,String>();
        while(!(line = bufferedReader.readLine()).isEmpty()){
            String[] header = line.split(": ");
            headers.put(header[0],header[1]);
        }

        return headers.isEmpty()?null:headers;
    }

    private HttpMethod setHttpRequestMethod(String method) throws InvalidMethodException{

        for(HttpMethod httpMethod: HttpMethod.values()){
            if(method.equals(httpMethod.name())){
                return httpMethod;
            }
        }

        throw new InvalidMethodException();
    }

    private String setHttpRequestPath(String path){
        return path.indexOf("?") != -1? path.substring(0,path.indexOf("?")):path;
    }

    private String setHttpBody() throws IOException{

        StringBuilder builder = new StringBuilder();
        while (bufferedReader.ready()){
            builder.append((char) bufferedReader.read());
        }

        String body = builder.toString();

        return body == null || body.isEmpty()? "":body;
    }
    
    private String setRequestHttpVersion(String httpVersion) throws IncompatibleHtppVersionException {
        if(httpVersion.equals(Constants.HTTP_PROTOCOL_VERSION.getValue()))
            return httpVersion;

        throw new IncompatibleHtppVersionException();
    }

    private Map<String,String> setHttpRequestParameters(String url) throws InvalidQueryParameterException{
        Map<String,String> queryParameters = new HashMap<>();
        if(url.indexOf("?") > -1){
            try{
                String parameters = url.substring(url.indexOf('?')+1);
                List<String> listParameters = List.of(parameters.split("&"));
                Iterator<String> iterator = listParameters.iterator();
                while (iterator.hasNext()){
                    String[] parameter = iterator.next().split("=");
                    queryParameters.put(parameter[0],parameter[1]);
                }
            }catch (ArrayIndexOutOfBoundsException e){
                throw new InvalidQueryParameterException();
            }

        }
        return queryParameters;
    }
}
