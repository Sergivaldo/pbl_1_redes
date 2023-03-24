package br.uefs.api_rest.validator;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe singleton utilizada para validar as requisições da API
 */
public class RequestValidator {

    /**
     * Verifica se o corpo contém todos os campos necessários para processar a requisição
     * @param body corpo da requisição
     * @param expectedFields campos esperados
     * @return booleano indicando se o corpo é valido
     */
    public boolean isValidBody(String body,String[] expectedFields){
        Gson gson = new Gson();
        Map<String,String> json = gson.fromJson(body, HashMap.class);

        for(String field : expectedFields){
            if(!json.containsKey(field)){
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica se nos parâmetros de consulta estão contidos todos os parâmetros necessários para
     * processar a requisição
     * @param queryParameters parâmetros de consulta da requisição
     * @param expectedParameters parâmetros esperados
     * @return booleano indicando se os parâmetros são validos
     */
    public boolean isValidQueryParameters(Map<String,String> queryParameters,String[] expectedParameters){
        for(String parameter : expectedParameters){
            if(!queryParameters.containsKey(parameter)){
                return false;
            }
        }
        return true;
    }

}
