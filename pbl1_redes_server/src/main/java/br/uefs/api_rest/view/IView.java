package br.uefs.api_rest.view;

import br.uefs.http.message.HttpRequest;
import br.uefs.http.message.HttpResponse;

public interface IView {
    HttpResponse get(HttpRequest request);
    HttpResponse put(HttpRequest request);
    HttpResponse post(HttpRequest request);

}
