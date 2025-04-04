package constant;

public enum RequestPath {
    ROOT("/"),
    INDEX("/index.html"),
    SIGNUP("/user/signup"),
    LOGIN("/user/login"),
    USER_LIST("/user/userList");

    private final String path;

    RequestPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
