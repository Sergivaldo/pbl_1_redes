package br.uefs.api_rest.validator;

import br.uefs.api_rest.view.IView;

import java.util.Map;

public class RouteValidator {
    private static RouteValidator instance;

    private RouteValidator(){}

    public synchronized static RouteValidator getInstance(){
        if(instance == null){
            instance = new RouteValidator();
        }

        return  instance;
    }


    public boolean containsPath(String path, Map<String, IView> paths) {
        return paths.containsKey(path);
    }

}
