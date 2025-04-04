package webserver;

import controller.*;
import http.util.HttpRequest;
import http.util.HttpResponse;

import java.util.HashMap;
import java.util.Map;

public class RequestMapper {

    private final Map<String, Controller> controllers = new HashMap<>();
    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;

    public RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;

        initMapping();
    }

    private void initMapping() {
        controllers.put("/", new HomeController());
        controllers.put(Route.SIGN_UP.path(), new SignUpController());
        controllers.put("/user/login", new LoginController());
        controllers.put(Route.USER_LIST.path(), new ListController());
    }

    public void proceed() {
        String url = httpRequest.getPath();
        String method = httpRequest.getMethod();

        Controller controller = controllers.get(url);

        if (controller == null) {
            controller = new ForwardController();
        }

        controller.execute(httpRequest, httpResponse);
    }
}
