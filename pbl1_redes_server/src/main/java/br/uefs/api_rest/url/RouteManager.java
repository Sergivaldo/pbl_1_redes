package br.uefs.api_rest.url;

import br.uefs.api_rest.view.IView;

import java.util.HashMap;
import java.util.Map;

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

    public void addRoute(String path, IView view) {
        routes.put(path,view);
    }

}
