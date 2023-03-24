package br.uefs.server.sockets.tcp;

import br.uefs.api_rest.MainApi;
import br.uefs.http.exception.IncompatibleHtppVersionException;
import br.uefs.http.exception.InvalidMethodException;
import br.uefs.http.exception.InvalidQueryParameterException;
import br.uefs.http.message.HttpRequest;
import br.uefs.http.message.HttpResponse;
import br.uefs.http.message.HttpStatus;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TCPClient extends Thread{
    private final Socket clientSocket;

    public TCPClient(Socket client){
        this.clientSocket = client;
    }

    /**
     * Processa a requisição do cliente a mandando para a API REST.
     * Este método poderá lançar exceções caso ocorra algum erro durante este processo.
     * Por fim, é retornada uma resposta HTTP com o resultado da solicitação feita.
     */
    public void run() {
        try {
            HttpResponse httpResponse;
            try{
                HttpRequest request;
                request = new TCPMessage(clientSocket.getInputStream()).parseHttpRequest();
                MainApi api = MainApi.getInstance();
                httpResponse = api.processRequest(request);
            } catch (IncompatibleHtppVersionException e) {
                httpResponse = new HttpResponse(HttpStatus.HTTP_VERSION_NOT_SUPPORTED);
            } catch (InvalidMethodException e) {
                httpResponse = new HttpResponse(HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e){
                httpResponse = new HttpResponse(HttpStatus.HTTP_INTERNAL_SERVER_ERROR);
            } catch (InvalidQueryParameterException e) {
                httpResponse = new HttpResponse(HttpStatus.BAD_REQUEST);
            }
            if(httpResponse == null){
                httpResponse = new HttpResponse(HttpStatus.HTTP_INTERNAL_SERVER_ERROR);
            }

            clientSocket.getOutputStream().write(httpResponse.toString().getBytes(StandardCharsets.UTF_8));
            clientSocket.getOutputStream().close();
            clientSocket.close();
        }catch (IOException e){}
    }
}
