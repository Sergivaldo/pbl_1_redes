package br.uefs.api_rest.repository;

import br.uefs.database.Data;
import br.uefs.api_rest.model.ClientModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientRepository{
    private Data dataBase;
    private List<ClientModel> clientDB;
    public ClientRepository() {
        dataBase = Data.getInstance();
        this.clientDB = dataBase.getData().getOrDefault("clients",new ArrayList<>());
    }

    public void save(ClientModel clientModel){
        clientDB.add(clientModel);
        dataBase.getData().put("clients",clientDB);
    }

    public void update(ClientModel clientModel){
        clientDB.add(clientModel.getId(),clientModel);
    }

    public Optional<ClientModel> findById(int id){
        Optional<ClientModel> client =  id < clientDB.size()? Optional.of(clientDB.get(id)):Optional.empty();
        return client;
    }

    public Optional<ClientModel> findBySmartMeterCode(int code){
        try {
            ClientModel client = clientDB.stream().filter(
                    c -> c.getSmartMeterCode() == code).findFirst().get();
            return Optional.of(client);
        } catch (Exception e) {
            return Optional.empty();
        }

    }

    public List<ClientModel> getAll(){
        return clientDB;
    }
}
