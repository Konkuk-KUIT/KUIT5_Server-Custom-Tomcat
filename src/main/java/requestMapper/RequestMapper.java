package requestMapper;

import controller.*;
import enumClass.UrlMapping;
import http.util.httpRequest.HttpRequest;
import http.util.httpResponse.HttpResponse;

import java.io.IOException;

public class RequestMapper {

    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;

    public static RequestMapper of(HttpRequest httpRequest, HttpResponse httpResponse){
        return new RequestMapper(httpRequest, httpResponse);
    }

    private RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
    }

    public void proceed() throws IOException {
        String path = httpRequest.getPath();
        Controller controller = UrlMapping.getControllerByPath(path);
        controller.execute(httpRequest, httpResponse);
    }
}
