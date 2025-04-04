// Controller 인터페이스
package controller;

import http.model.HttpRequest;
import http.model.HttpResponse;

public interface Controller {
    void execute(HttpRequest request, HttpResponse response) throws Exception;
}