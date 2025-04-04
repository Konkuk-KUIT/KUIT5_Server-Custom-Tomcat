package http.util;

import controller.*;
import data.Method;
import data.UrlData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestMapper {

    private static final Map<String, Controller> controllers = new HashMap<>();
    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;
    private Controller controller;
    static {
        controllers.put(UrlData.BLANK_URL.getData(), new HomeController());
        controllers.put(UrlData.DEFAULT_URL.getData(), new HomeController());
        controllers.put(UrlData.SIGNUP_URL.getData(), new SignUpController());
        controllers.put(UrlData.LOGIN_URL.getData(), new LoginController());
        controllers.put(UrlData.LIST_URL.getData(), new ListController());
    }

    public RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        controller = controllers.get(httpRequest.getUrl());

        if (controller == null && httpRequest.getMethod().equals(Method.GET.get()) && httpRequest.getUrl().endsWith(UrlData.END_HTML.getData())) {
            controller = new ForwardController();
        }
        if (controller == null&& !httpRequest.getUrl().endsWith(UrlData.END_HTML.getData())){
            controller=new DefaultController();
        }
    }

    public void proceed() throws IOException {
        controller.execute(httpRequest,httpResponse);
    }
}
