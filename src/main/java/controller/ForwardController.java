package controller;

import data.UrlData;
import db.MemoryUserRepository;
import http.util.HttpRequest;
import http.util.HttpRequestUtils;
import http.util.HttpResponse;
import model.User;

import java.io.IOException;
import java.util.Map;

public class ForwardController implements Controller {

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        if(userSignup(request.getUrl())){
            response.forward(UrlData.DEFAULT_URL.getData());
        }
        response.forward(request.getUrl());
        
    }
    private boolean userSignup(String urlPath){
        String[] pathAndQuery = urlPath.split("\\?", 2);
        if (pathAndQuery.length == 2) {
            String queryString = pathAndQuery[1];
            Map<String, String> params = HttpRequestUtils.parseQueryParameter(queryString);
            return userCheck(params);
        }
        return false;
    }
    private boolean userCheck(Map<String, String> params){
        User newUser =User.userCheck(params);
        if(newUser!=null){
            MemoryUserRepository.getInstance().addUser(newUser);
            return true;
        }
        return false;
    }
}
