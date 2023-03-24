package br.uefs.api_rest.view;

import br.uefs.api_rest.controller.ConsumptionAlertController;
import br.uefs.api_rest.exception.InvalidParameterException;
import br.uefs.http.message.HttpRequest;
import br.uefs.http.message.HttpResponse;
import br.uefs.http.message.HttpStatus;
import com.google.gson.JsonObject;

import java.util.Optional;

public class ConsumptionAlertView implements IView {

    /**
     * Retorna para o usuário um alerta de fatura e consumo
     * @param request requisição feita pelo cliente
     * @return resposta HTTP com resultado da requisição
     */
    @Override
    public HttpResponse get(HttpRequest request) {
        ConsumptionAlertController controller = new ConsumptionAlertController();
        try {
            Optional<JsonObject> response = controller.createConsumptionAlert(request.getQueryParameters());
            return response.isPresent()? new HttpResponse(HttpStatus.OK,response.get().toString())
                    : new HttpResponse(HttpStatus.NOT_FOUND);
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
