package br.uefs.api_rest.view;

import br.uefs.api_rest.controller.ClientController;
import br.uefs.api_rest.exception.InvalidParameterException;
import br.uefs.http.message.HttpRequest;
import br.uefs.http.message.HttpResponse;
import br.uefs.http.message.HttpStatus;
import com.google.gson.JsonObject;

import java.util.Optional;

public class EnergyConsumptionView implements IView {

    /**
     * Retorna para o usuário o seu consumo de energia
     * @param request requisição feita pelo cliente
     * @return resposta HTTP com resultado da requisição
     */
    @Override
    public HttpResponse get(HttpRequest request) {
        ClientController controller = new ClientController();
        try{
            Optional<String> clientConsumptions = controller.getClientConsumptions(request.getQueryParameters());

            if (clientConsumptions.isPresent()){
                String body = clientConsumptions.get();
                return new HttpResponse(HttpStatus.OK,body);
            }else{
                return new HttpResponse(HttpStatus.NOT_FOUND);
            }
        } catch (InvalidParameterException e) {
            JsonObject message = new JsonObject();
            message.addProperty("reason",e.getMessage());
            return new HttpResponse(HttpStatus.BAD_REQUEST,message.toString());
        }
    }

    @Override
    public HttpResponse put(HttpRequest request) {
        return null;
    }

    @Override
    public HttpResponse post(HttpRequest request) {
        return null;
    }
}
