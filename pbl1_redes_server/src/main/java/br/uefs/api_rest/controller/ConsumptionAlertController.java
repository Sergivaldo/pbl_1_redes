package br.uefs.api_rest.controller;

import br.uefs.api_rest.exception.InvalidParameterException;
import br.uefs.api_rest.model.ClientModel;
import br.uefs.api_rest.model.InvoiceModel;
import br.uefs.api_rest.repository.ClientRepository;
import br.uefs.api_rest.validator.RequestValidator;
import br.uefs.util.Constants;
import com.google.gson.JsonObject;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ConsumptionAlertController {
    private final RequestValidator validator;
    private final ClientRepository repository;

    public ConsumptionAlertController() {
        this.validator = new RequestValidator();
        this.repository = new ClientRepository();
    }

    public Optional<JsonObject> createConsumptionAlert(Map<String,String> parameters) throws InvalidParameterException {
        String[] expectedParameters = {"id"};
        Optional<JsonObject> json;
        if(validator.isValidQueryParameters(parameters,expectedParameters)){
            int id = Integer.parseInt(parameters.get("id"));
            Optional<ClientModel> expectedClient = repository.findById(id);
            if(expectedClient.isPresent()){
                ClientModel client = expectedClient.get();
                JsonObject alert = new JsonObject();
                alert.addProperty("cliente",client.getName());
                JsonObject consumption = verifyConsumption(client);
                JsonObject invoice = verifyInvoicePrice(client);
                if(consumption.isEmpty()){
                    alert.addProperty("consumo","não há alertas");
                }else{
                    alert.add("consumo",consumption);
                }
                if (invoice.isEmpty()){
                    alert.addProperty("conta","não há alertas");
                }else{
                    alert.add("conta",invoice);
                }
                json = Optional.of(alert);
            }else {
                json = Optional.empty();
            }
        }else{
            throw new InvalidParameterException();
        }

        return json;
    }

    private JsonObject verifyConsumption(ClientModel client){
        JsonObject json = new JsonObject();
        if(client.getTotalConsumptions().floatValue() > 0){
            BigDecimal averageConsumption = BigDecimal.valueOf(client.getTotalConsumptions().floatValue()/client.getConsumptions().size())
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal normalConsumptionLimit = BigDecimal.valueOf(Float.parseFloat(Constants.NORMAL_CONSUMPTION_LIMIT.getValue()))
                    .setScale(2,RoundingMode.HALF_UP);
            if(averageConsumption.floatValue() > normalConsumptionLimit.floatValue()){
                json.addProperty("messagem","consumo excessivo de energia");
            }else{
                json.addProperty("messagem","consumo de energia normal");
            }
            json.addProperty("consumo_medio",averageConsumption.toString());
            json.addProperty("limite_consumo_normal",normalConsumptionLimit.toString());
        }

        return json;
    }

    private JsonObject verifyInvoicePrice(ClientModel client){
        List<InvoiceModel> invoices = client.getInvoices();
        JsonObject json = new JsonObject();
        double totalInvoicePrice = 0;
        for(InvoiceModel invoice:invoices){
            totalInvoicePrice += invoice.getPrice();
        }
        if(totalInvoicePrice > 0){
            BigDecimal averageInvoicePrince = BigDecimal.valueOf(totalInvoicePrice/invoices.size()).setScale(2,RoundingMode.HALF_UP);
            BigDecimal normalPriceLimit = new BigDecimal(
                    Float.parseFloat(Constants.NORMAL_PRINCE_LIMIT.getValue())).setScale(2,RoundingMode.HALF_UP);
            if(averageInvoicePrince.floatValue() > normalPriceLimit.floatValue()){
                json.addProperty("mensagem","grande variação de valor");
            }else{
                json.addProperty("mensagem","preço da conta normal");
            }
            json.addProperty("preco_medio",averageInvoicePrince.toString());
            json.addProperty("limite_preco_normal",normalPriceLimit.toString());
        }
        return json;
    }
}
