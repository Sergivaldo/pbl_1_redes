package br.uefs.util;

import br.uefs.api_rest.model.ClientModel;

import java.util.List;

public class IdGenerator {
    public static int generateClientId(List<ClientModel> clients){
        return clients.size();
    }
    public static int generateSmartMeterId(String[] args){
        StringBuilder builder = new StringBuilder();
        for(String arg : args){
            builder.append(arg);
        }

        int id = builder.toString().hashCode();
        return  id < 0? id * -1: id;
    }
}
