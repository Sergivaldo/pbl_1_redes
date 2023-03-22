package br.uefs.api_rest.controller;

import br.uefs.api_rest.exception.InvalidParameterException;
import br.uefs.api_rest.exception.UnavaibleInvoiceException;
import br.uefs.api_rest.model.ClientModel;
import br.uefs.api_rest.model.InvoiceModel;
import br.uefs.api_rest.repository.ClientRepository;
import br.uefs.api_rest.validator.RequestValidator;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class InvoiceController {
    private final RequestValidator validator;
    private final ClientRepository repository;

    public InvoiceController() {
        this.validator = new RequestValidator();
        this.repository = new ClientRepository();
    }

    public Optional<JsonObject> createInvoice(Map<String,String> parameters) throws InvalidParameterException, UnavaibleInvoiceException {
        String[] expectedParameters = {"id"};
        Optional<JsonObject> invoice = Optional.empty();
        if (validator.isValidQueryParameters(parameters, expectedParameters)) {
            int id = Integer.parseInt(parameters.get("id"));
            Optional<ClientModel> expectedClient = repository.findById(id);
            JsonObject json = new JsonObject();
            if (expectedClient.isPresent()) {
                ClientModel client = expectedClient.get();
                BigDecimal totalConsumption = client.getTotalConsumptions();
                double invoicePrice =  (3.25 * (totalConsumption.floatValue() / 100));
                if(invoicePrice > 10){
                    InvoiceModel invoiceModel = new InvoiceModel(
                            client.getName(),
                            client.getSmartMeterCode(),
                            invoicePrice);
                    json.addProperty("nome", invoiceModel.getName());
                    json.addProperty("codigo_medidor", invoiceModel.getSmartMeterCode());
                    json.addProperty("preco", invoiceModel.getPrice());

                    client.getInvoices().add(invoiceModel);
                    client.setTotalConsumptions(new BigDecimal(0));
                    repository.update(client);
                }else{
                    json.addProperty("mensagem","valor mínimo para gerar fatura não atingido");
                    json.addProperty("preco_minimo","10");
                    json.addProperty("preco_atual",Double.toString(invoicePrice));
                    throw new UnavaibleInvoiceException(json);
                }
                invoice = Optional.of(json);
            }
        } else {
            throw new InvalidParameterException();
        }
        return invoice;
    }


}
