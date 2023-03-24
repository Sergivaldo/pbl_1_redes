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
     * Lê e Verifica os cabeçalhos da mensagem recebida e converte em um objeto HttpHeader
     * @return HTTPHeader(HashMap)
     * @throws IOException Caso ocorra um erro de I/O
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

    /**
     * Lê e Verifica o método da mensagem recebida e converte em um objeto HttpMethod
     * @param method String com o método HTTP da mensagem recebida
     * @return Representação de um método HTTP no formato de Enum
     * @throws InvalidMethodException Caso o método da mensagem não seja válido
     */
    private HttpMethod setHttpRequestMethod(String method) throws InvalidMethodException{

        for(HttpMethod httpMethod: HttpMethod.values()){
            if(method.equals(httpMethod.name())){
                return httpMethod;
            }
        }

        throw new InvalidMethodException();
    }

    /**
     * Lê a rota da mensagem HTTP que pode conter parâmetros e pega apenas o caminho
     * @param path rota da mensagem HTTP
     * @return Caminho da mensagem HTTP
     */
    private String setHttpRequestPath(String path){
        return path.indexOf("?") != -1? path.substring(0,path.indexOf("?")):path;
    }

    /**
     * Pega o corpo da mensagem caso exista.
     * @return String com o corpo da mensagem HTTP
     * @throws IOException Caso ocorra um erro de I/O
     */

    private String setHttpBody() throws IOException{

        StringBuilder builder = new StringBuilder();
        while (bufferedReader.ready()){
            builder.append((char) bufferedReader.read());
        }

        String body = builder.toString();

        return body == null || body.isEmpty()? "":body;
    }

    /**
     * Verifica se o a versão do HTTP é compatível
     * @param httpVersion Versão do HTTP da mensagem recebida
     * @return String com versão HTTP
     * @throws IncompatibleHtppVersionException Caso a versão do HTTP seja incompatível com a da API
     */
    private String setRequestHttpVersion(String httpVersion) throws IncompatibleHtppVersionException {
        if(httpVersion.equals(Constants.HTTP_PROTOCOL_VERSION.getValue()))
            return httpVersion;

        throw new IncompatibleHtppVersionException();
    }

    /**
     * Verifica e pega os parâmetros da mensagem HTTP o tranformando em um objeto Map
     * @param url Rota da mensagem que pode conter parâmetros
     * @return Map com todos os parâmetros da requisição recebida
     * @throws InvalidQueryParameterException Caso a estrutura dos parâmetros esteja incorreta
     */
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
