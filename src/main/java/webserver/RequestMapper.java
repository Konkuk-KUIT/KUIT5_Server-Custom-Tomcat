package webserver;

import controller.*;
import http.enums.UrlPath;

import java.util.HashMap;
import java.util.Map;

// 요청 경로(URL)에 따라 컨트롤러를 매핑해주는 책임을 가진 클래스.
public class RequestMapper {

    private final Map<String, Controller> controllerMap = new HashMap<>();

    public RequestMapper() {
        // 각 URL 경로에 맞는 Controller 매핑
        controllerMap.put(UrlPath.LOGIN.getPath(), new LoginController());
        controllerMap.put(UrlPath.SIGNUP.getPath(), new SignupController());
        controllerMap.put(UrlPath.USER_LIST.getPath(), new UserListController());
        controllerMap.put(UrlPath.ROOT.getPath(), (req, res) -> res.forward("/index.html"));
        controllerMap.put("/css/style.css", (req, res) -> res.forward("/css/style.css"));
    }

    // URL에 해당하는 컨트롤러 반환. 없으면 null을 반환
    public Controller getController(String path) {
        return controllerMap.get(path);
    }
}
