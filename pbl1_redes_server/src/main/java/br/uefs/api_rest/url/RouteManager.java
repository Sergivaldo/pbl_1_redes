package br.uefs.api_rest.url;

import br.uefs.api_rest.view.IView;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe singleton que gerencia as rotas da API
 */
public class RouteManager {
    private final Map<String, IView> routes;
    private static RouteManager instance;

    private RouteManager(HashMap<String,IView> routes){
        this.routes = routes;
    }
    public synchronized static RouteManager getInstance(){
        if(instance == null){
            instance = new RouteManager(new HashMap<String,IView>());
        }

        return instance;
    }

    public Map<String, IView> getRoutes() {
        return routes;
    }

    /**
     * Adiciona uma nova rota com sua View a API
     * @param path nova rota
     * @param view View da rota
     */
    public void addRoute(String path, IView view) {
        routes.put(path,view);
    }

}
