package br.uefs.api_rest;

import br.uefs.api_rest.url.RouteManager;
import br.uefs.api_rest.validator.RouteValidator;
import br.uefs.api_rest.view.*;
import br.uefs.http.message.HttpMethod;
import br.uefs.http.message.HttpRequest;
import br.uefs.http.message.HttpResponse;
import br.uefs.http.message.HttpStatus;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Objects;

public class MainApi {
    private final RouteManager routeManager;
    private static MainApi instance;

    private MainApi() {
        this.routeManager = RouteManager.getInstance();
        this.InitRoutes();
    }

    public synchronized static MainApi getInstance() {
        if (instance == null) {
            instance = new MainApi();
        }
        return instance;
    }

    /**
     * Inicializa as rotas da API
     */
    private void InitRoutes() {
        routeManager.addRoute("/consumo_energia", new EnergyConsumptionView());
        routeManager.addRoute("/fatura", new InvoiceView());
        routeManager.addRoute("/alertas_consumo", new ConsumptionAlertView());
        routeManager.addRoute("/cliente", new ClientView());
    }

    /***
     * Processa a requisição HTTP realizada por um cliente executando uma tarefa
     * específica para cada método. A requisição é validada verificando se não é
     * nula e se o caminho da requição existe.
     *
     * @param request - Requisição HTTP que será processada.
     */
    public HttpResponse processRequest(HttpRequest request) {
        Objects.requireNonNull(request);

        RouteValidator validator = RouteValidator.getInstance();
        HttpMethod method = request.getMethod();
        String path = request.getPath();
        Map<String, IView> routes = routeManager.getRoutes();
        HttpResponse response;
        JsonObject responseBody = new JsonObject();
        if (validator.containsPath(path, routes)) {
            IView routeView = routes.get(path);
            switch (method) {
                case GET:
                    response = routeView.get(request);
                    break;
                case POST:
                    response = routeView.post(request);
                    break;
                case PUT:
                    response = routeView.put(request);
                    break;
                default:
                    responseBody.addProperty("status_code", HttpStatus.NOT_IMPLEMENTED.getCode());
                    responseBody.addProperty("status_message", HttpStatus.NOT_IMPLEMENTED.getMessage());
                    responseBody.addProperty("reason", "Unsupported request method");
                    response = new HttpResponse(HttpStatus.NOT_IMPLEMENTED, responseBody.toString());
                    break;
            }
        } else {
            responseBody.addProperty("status_code", HttpStatus.BAD_REQUEST.getCode());
            responseBody.addProperty("status_message", HttpStatus.BAD_REQUEST.getMessage());
            responseBody.addProperty("reason", "Invalid route");
            response = new HttpResponse(HttpStatus.BAD_REQUEST, responseBody.toString());
        }

        return response;
    }


}
