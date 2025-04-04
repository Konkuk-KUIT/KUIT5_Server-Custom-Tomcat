package http;

import http.controller.*;
import http.httpEnum.HttpMethod;
import http.httpEnum.UrlPath;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    private final HttpRequest request;
    private final HttpResponse response;

    private final Map<UrlPath, Controller> getMappings = new HashMap<>();
    private final Map<UrlPath, Controller> postMappings = new HashMap<>();

    public RequestMapper(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
        init(); // 매핑 초기화
    }

    private void init() {
        getMappings.put(UrlPath.ROOT, new HomeController());
        getMappings.put(UrlPath.USER_LIST, new UserListController());

        postMappings.put(UrlPath.SIGNUP, new SignUpController());
        postMappings.put(UrlPath.LOGIN, new LoginController());
    }

    public void proceed() throws IOException {
        HttpMethod method = HttpMethod.from(request.getMethod());
        String rawPath = request.getPath();
        UrlPath path = UrlPath.from(rawPath);

        Controller controller = getController(path, method);

        if (controller != null) {
            controller.execute(request, response);
        } else {
            handleStatic(rawPath);
        }
    }

    private Controller getController(UrlPath path, HttpMethod method) {
        return method == HttpMethod.POST ? postMappings.get(path) : getMappings.get(path);
    }

    private void handleStatic(String path) throws IOException {
        Controller staticController = new StaticFileController(path);
        staticController.execute(request, response);
    }
}
