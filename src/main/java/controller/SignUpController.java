package controller;

import data.UrlData;
import db.MemoryUserRepository;
import http.util.HttpRequest;
import http.util.HttpRequestUtils;
import http.util.HttpResponse;
import model.User;

import java.io.IOException;
import java.util.Map;

public class SignUpController implements Controller {

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(request.getBody());
        User newUser =User.userCheck(params);
        if(newUser!=null){
            MemoryUserRepository.getInstance().addUser(newUser);
            response.redirect(UrlData.DEFAULT_URL.getData());
            return;
        }
        response.forward(UrlData.SIGN_URL.getData());
    }



}
