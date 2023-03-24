package br.uefs.api_rest.repository;

import br.uefs.database.Data;
import br.uefs.api_rest.model.ClientModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Classe responsável por fazer a comunicação com a base de dados do sistema
 */
public class ClientRepository{
    private Data dataBase;
    private List<ClientModel> clientDB;
    public ClientRepository() {
        dataBase = Data.getInstance();
        this.clientDB = dataBase.getData().getOrDefault("clients",new ArrayList<>());
    }

    /**
     * Salva um novo cliente
     * @param clientModel cliente que será salvo
     */
    public void save(ClientModel clientModel){
        clientDB.add(clientModel);
        dataBase.getData().put("clients",clientDB);
    }

    /**
     * Atualiza um cliente
     * @param clientModel Cliente que será atualizado
     */
    public void update(ClientModel clientModel){
        clientDB.add(clientModel.getId(),clientModel);
    }

    /**
     * Procura um cliente pelo seu id
     * @param id id do cliente
     * @return Optional que pode conter ou não o cliente
     */
    public Optional<ClientModel> findById(int id){
        Optional<ClientModel> client =  id < clientDB.size()? Optional.of(clientDB.get(id)):Optional.empty();
        return client;
    }

    /**
     * Procura um cliente pelo código do seu medidor
     * @param code código do medidor do cliente
     * @return Optional que pode conter ou não o cliente
     */
    public Optional<ClientModel> findBySmartMeterCode(int code){
        try {
            ClientModel client = clientDB.stream().filter(
                    c -> c.getSmartMeterCode() == code).findFirst().get();
            return Optional.of(client);
        } catch (Exception e) {
            return Optional.empty();
        }

    }

    /**
     * Retorna uma lista com todos os clientes
     * @return lista de clientes
     */
    public List<ClientModel> getAll(){
        return clientDB;
    }
}
