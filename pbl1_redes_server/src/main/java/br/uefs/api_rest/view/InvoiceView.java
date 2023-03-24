package br.uefs.api_rest.view;

import br.uefs.api_rest.exception.InvalidParameterException;
import br.uefs.api_rest.controller.InvoiceController;
import br.uefs.api_rest.exception.UnavaibleInvoiceException;
import br.uefs.http.message.HttpRequest;
import br.uefs.http.message.HttpResponse;
import br.uefs.http.message.HttpStatus;
import com.google.gson.JsonObject;

import java.util.Optional;

public class InvoiceView implements IView {

    /**
     * Retorna para o usuário a sua fatura
     * @param request requisição feita pelo cliente
     * @return resposta HTTP com resultado da requisição
     */
    @Override
    public HttpResponse get(HttpRequest request) {
        InvoiceController controller = new InvoiceController();
        Optional<JsonObject> response = null;
        try {
            response = controller.createInvoice(request.getQueryParameters());
            return response.isPresent()? new HttpResponse(HttpStatus.OK,response.get().toString())
                    : new HttpResponse(HttpStatus.NOT_FOUND);
        } catch (InvalidParameterException e) {
            JsonObject message = new JsonObject();
            message.addProperty("reason",e.getMessage());
            return new HttpResponse(HttpStatus.BAD_REQUEST,message.toString());
        } catch (UnavaibleInvoiceException e) {
            return  new HttpResponse(HttpStatus.BAD_REQUEST,e.getJson().toString());
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
