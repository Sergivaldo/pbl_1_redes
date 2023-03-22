package br.uefs.api_rest.validator;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class RequestValidator {

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

    public boolean isValidQueryParameters(Map<String,String> queryParameters,String[] expectedParameters){
        for(String parameter : expectedParameters){
            if(!queryParameters.containsKey(parameter)){
                return false;
            }
        }
        return true;
    }

}
