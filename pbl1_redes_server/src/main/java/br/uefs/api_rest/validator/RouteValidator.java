package br.uefs.api_rest.validator;

import br.uefs.api_rest.view.IView;

import java.util.Map;

/**
 * Classe singleton utilizada para validar as rotas da API
 */
public class RouteValidator {
    private static RouteValidator instance;

    private RouteValidator(){}

    public synchronized static RouteValidator getInstance(){
        if(instance == null){
            instance = new RouteValidator();
        }

        return  instance;
    }

    /**
     * Verifica se a rota passada na requisição existe na API
     * @param path rota da requisição
     * @param paths rotas aceitas
     * @return booleano que indica se a rota está contida ou não
     */
    public boolean containsPath(String path, Map<String, IView> paths) {
        return paths.containsKey(path);
    }

}
