package webserver.controller;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static webserver.URL.INDEX_HTML;

public class SignUpController implements HttpController {

    private byte[] body;
    private final Path filePath = Paths.get(INDEX_HTML.getPath());


    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {

        Map<String, String> map;

        if(request.getMethod().equals("GET")) {
            String[] str = request.getUrl().split("\\?");
            map = HttpRequestUtils.parseQueryParameter(str[1]);
        }
        else{
            String requestBody = request.getRequestBody();
            map= HttpRequestUtils.parseQueryParameter(requestBody);
        }

        User user = new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));
        MemoryUserRepository.getInstance().addUser(user);



        body = Files.readAllBytes(filePath);

        response.response302Header("/index.html");
        response.response200Header(body.length, "text/html");
        response.responseBody(body);

    }
}
