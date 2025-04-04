package controller;

import db.MemoryUserRepository;
import db.Repository;
import enumClass.HttpMethod;
import enumClass.UrlPath;
import enumClass.UserQueryKey;
import http.util.httpRequest.HttpRequest;
import http.util.httpResponse.HttpResponse;
import model.User;

import java.io.IOException;

import static enumClass.HttpMethod.*;
import static enumClass.UrlPath.*;
import static enumClass.UserQueryKey.*;

public class LoginController implements Controller{


    Repository repository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

        String userId = httpRequest.getBodyMap().get(USER_ID.getKey());

        User loginUser = repository.findUserById(userId);
        if(validateLogin(httpRequest, loginUser)){
            httpResponse.send302WithLoginCookie(ROOT.getPath());
            return;
        }

        httpResponse.send302(LOGIN_FAILED.getPath());
    }



    private boolean validateLogin(HttpRequest httpRequest, User loginUser){
        return !(loginUser == null) && (loginUser.getPassword().equals(httpRequest.getBodyMap().get(PASSWORD.getKey())));
    }
}
