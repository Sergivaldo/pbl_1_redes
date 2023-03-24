package br.uefs.api_rest.controller;

import br.uefs.api_rest.exception.InvalidParameterException;
import br.uefs.api_rest.model.ClientModel;
import br.uefs.api_rest.model.ConsumptionModel;
import br.uefs.api_rest.pagination.PageManager;
import br.uefs.api_rest.repository.ClientRepository;
import br.uefs.api_rest.validator.RequestValidator;
import br.uefs.util.IdGenerator;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class ClientController {
    private final RequestValidator validator;
    private final ClientRepository repository;

    public ClientController() {
        this.validator = new RequestValidator();
        this.repository = new ClientRepository();
    }

    /**
     * Atualiza o consumo total do cliente
     * @param consumption novo consumo
     * @param clientModel cliente que será atualizado
     */
    public void updateTotalConsumptions(double consumption, ClientModel clientModel) {
        BigDecimal currentTotalConsumption = clientModel.getTotalConsumptions();
        clientModel.setTotalConsumptions(currentTotalConsumption.add(
                BigDecimal.valueOf(consumption)
        ));
    }

    /**
     * Cria um novo cliente
     * @param body corpo da requisição com nome do cliente
     * @return booleano indicando se o cliente foi salvo ou não
     */
    public boolean createClient(String body) {
        String[] expectedFields = {"nome"};
        if (validator.isValidBody(body, expectedFields)) {
            Map<String, String> validatedBody = new Gson().fromJson(body, HashMap.class);
            int clientId = IdGenerator.generateClientId(repository.getAll());
            String name = validatedBody.get("nome");
            int smartMeterId = IdGenerator.generateSmartMeterId(new String[]{Integer.toString(clientId), name});
            ClientModel clientModel = new ClientModel(clientId, name, smartMeterId);
            repository.save(clientModel);
            return true;
        }
        return false;
    }

    /**
     * Busca por um cliente através do seu id especificado nos parâmetros de consulta
     * @param parameters parâmetros de consulta
     * @return Optional com dados do cliente caso este tenha sido encontrado
     */
    public Optional<String> readClient(Map<String, String> parameters) {
        Optional<String> client = Optional.empty();
        String[] expectedParameters = {"id"};
        if (validator.isValidQueryParameters(parameters, expectedParameters)) {
            Optional<ClientModel> clientModel = repository.findById(Integer.parseInt(parameters.get("id")));
            if (clientModel.isPresent()) {
                JsonObject json = new JsonObject();
                json.addProperty("id", clientModel.get().getId());
                json.addProperty("cliente", clientModel.get().getName());
                json.addProperty("codigo_medidor", clientModel.get().getSmartMeterCode());
                client = Optional.of(json.toString());
            }
        }

        return client;
    }

    /**
     * Busca um cliente pelo id do seu medidor e atualiza a lista de consumos do cliente
     * @param message mensagem com valor de consumo, horário da medição e código do medidor
     */
    public void updateClientConsumption(Map<String, String> message) {
        int code = Integer.parseInt(message.get("code"));
        String consumption = message.get("consumption");
        String measuredAt = message.get("measured_at");
        Optional<ClientModel> expectedClient = repository.findBySmartMeterCode(code);
        if (expectedClient.isPresent()) {
            ClientModel client = expectedClient.get();
            ConsumptionModel newConsumption = new ConsumptionModel(measuredAt, BigDecimal.valueOf(
                    Float.parseFloat(consumption)).setScale(2, RoundingMode.HALF_UP));
            client.getConsumptions().add(newConsumption);
            updateTotalConsumptions(Double.parseDouble(consumption), client);
            repository.update(client);
        }
    }

    /**
     * Pega todos os consumos do cliente
     * @param parameters parâmetro de consulta com id do cliente
     * @return consumos do cliente
     * @throws InvalidParameterException Caso um parâmetro inválido seja passado
     */
    public Optional<String> getClientConsumptions(Map<String, String> parameters) throws InvalidParameterException {

        Optional<String> jsonConsumptions = Optional.empty();
        String[] expectedParameters = {"id"};
        if (validator.isValidQueryParameters(parameters, expectedParameters)) {
            int id = Integer.parseInt(parameters.get("id"));
            Optional<ClientModel> expectedClient = repository.findById(id);
            if (expectedClient.isPresent()) {
                ClientModel client = expectedClient.get();
                List<ConsumptionModel> consumptions = client.getConsumptions();
                PageManager pageManager = new PageManager(consumptions);
                List<ConsumptionModel> page;
                if (parameters.containsKey("offset") && parameters.containsKey("limit")) {
                    int paginationOffset = Integer.parseInt(parameters.get("offset"));
                    int paginationLimit = Integer.parseInt(parameters.get("limit"));
                    page = pageManager.createPage(paginationOffset, paginationLimit);
                } else {
                    page = pageManager.createPage();
                }
                JsonObject json = new JsonObject();
                JsonArray arrayConsumptions = new JsonArray();
                json.addProperty("id", client.getId());
                json.addProperty("cliente", client.getName());
                json.addProperty("consumo_total", client.getTotalConsumptions().toString());
                page.stream().forEach(
                        (t) -> {
                            JsonObject consumption = new JsonObject();
                            consumption.addProperty("consumo", t.getConsumption());
                            consumption.addProperty("horario_medicao", t.getDateTime());
                            arrayConsumptions.add(consumption);
                        }
                );
                json.add("consumos", arrayConsumptions);
                jsonConsumptions = Optional.of(json.toString());
            } else {
                jsonConsumptions = Optional.empty();
            }
        } else {
            throw new InvalidParameterException();
        }


        return jsonConsumptions;
    }


}
