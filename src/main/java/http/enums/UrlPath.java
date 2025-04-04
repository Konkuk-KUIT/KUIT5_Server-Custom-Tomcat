package http.enums;

public enum UrlPath {
    ROOT("/"),
    INDEX("/index.html"),
    LOGIN("/user/login"),
    LOGIN_FAIL("/user/login_failed.html"),
    SIGNUP("/user/signup"),
    USER_LIST("/user/userList"),
    LOGIN_PAGE("/user/login.html");

    private final String path;

    UrlPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
