package webserver;

public enum Route {

    INDEX("/index.html"),
    LOGIN_FAILED("/user/login_failed.html"),
    LOGIN("/user/login.html"),
    USER_LIST("/user/userList"),/// 흠 이건 입력받을떄만 쓰긴하는데..
    SIGN_UP("/user/signup"),
    LIST("/user/list.html");

    private final String path;

    Route(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }

    @Override
    public String toString() {
        return path;
    }

}
