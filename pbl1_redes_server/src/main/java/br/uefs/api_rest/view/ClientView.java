package br.uefs.api_rest.view;

import br.uefs.api_rest.controller.ClientController;
import br.uefs.http.message.HttpRequest;
import br.uefs.http.message.HttpResponse;
import br.uefs.http.message.HttpStatus;
import com.google.gson.JsonObject;

import java.util.Optional;

public class ClientView implements IView {
    private ClientController controller;

    public ClientView() {
        this.controller = new ClientController();
    }

    /**
     * Retorna para o usuário informações do cliente buscado
     * @param request requisição feita pelo cliente
     * @return resposta HTTP com resultado da requisição
     */
    @Override
    public HttpResponse get(HttpRequest request) {
        Optional<String> clientJson = controller.readClient(request.getQueryParameters());
        return clientJson.isPresent()? new HttpResponse(HttpStatus.OK,clientJson.get().toString())
                : new HttpResponse(HttpStatus.NOT_FOUND);
    }


    @Override
    public HttpResponse put(HttpRequest request) {
        JsonObject responseBody = new JsonObject();
        responseBody.addProperty("status_code",HttpStatus.METHOD_NOT_ALLOWED.getCode());
        responseBody.addProperty("status_message",HttpStatus.METHOD_NOT_ALLOWED.getMessage());
        responseBody.addProperty("reason","request method is not valid for this route");
        return new HttpResponse(HttpStatus.METHOD_NOT_ALLOWED, responseBody.toString());
    }

    /**
     * Cria um novo cliente no sistema
     * @param request requisição feita pelo cliente
     * @return resposta HTTP com resultado da requisição
     */
    @Override
    public HttpResponse post(HttpRequest request) {
        HttpResponse response = controller.createClient(request.getBody())?
                new HttpResponse(HttpStatus.CREATED)
                : new HttpResponse(HttpStatus.BAD_REQUEST);

        return response;
    }
}
