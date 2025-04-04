package enumClass;

import controller.*;

public enum UrlMapping {
    ROOT("/", new HomeController()),
    INDEX("/index.html", new HomeController()),
    CSS("/css/styles.css", new CssController()),
    JS("/js/scripts.js", new ForwardController()),
    FAVICON("/favicon.ico", new ForwardController()),
    FORM("/user/form.html", new ForwardController()),
    LOGIN_PAGE("/user/login.html", new ForwardController()),
    SIGNUP("/user/signup", new SignUpController()),
    LOGIN("/user/login", new LoginController()),
    LOGIN_FAILED("/user/login_failed.html", new ForwardController()),
    USER_LIST("/user/userList", new ListController());

    private final String path;
    private final Controller controller;

    UrlMapping(String path, Controller controller) {
        this.path = path;
        this.controller = controller;
    }

    public String getPath() {
        return path;
    }

    public Controller getController() {
        return controller;
    }

    public static Controller getControllerByPath(String path) {
        for (UrlMapping mapping : values()) {
            if (mapping.getPath().equals(path)) {
                return mapping.getController();
            }
        }
        return new ForwardController(); // default fallback
    }
}
