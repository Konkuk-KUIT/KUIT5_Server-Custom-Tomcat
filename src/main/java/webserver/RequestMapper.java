package webserver;

import controller.*;
import model.HttpRequest;
import model.HttpResponse;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static enums.URL.*;

public class RequestMapper {
    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;
    Map<String,Controller> controllers = new HashMap<>();

    public RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        initControllers();
    }

    public void proceed() throws IOException {
        //String method = httpRequest.getMethod();
        URI uri = httpRequest.getURI();
        String path = uri.getPath();

        Controller controller = new ForwardController();
        if(controllers.containsKey(path))
            controller = controllers.get(path);

        controller.execute(httpRequest, httpResponse);
    }

    private void initControllers(){
        controllers.put(ROOT.getUrl(), new HomeController());
        controllers.put(SIGNUP.getUrl(), new SignUpController());
        controllers.put(LOGIN.getUrl(), new LoginController());
        controllers.put(LIST_HTML.getUrl(), new ListController());
    }
}
