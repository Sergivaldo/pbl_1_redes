package br.uefs.database;

import br.uefs.api_rest.model.ClientModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Data {
    private final Map<String, List<ClientModel>> data;
    private static Data instance;

    private Data(Map<String, List<ClientModel>> data){
        this.data = data;
    }

    public synchronized static Data getInstance(){
        if(instance == null){
            instance = new Data(new TreeMap<>());
        }

        return  instance;
    }

    public Map<String, List<ClientModel>> getData() {
        return data;
    }
}
