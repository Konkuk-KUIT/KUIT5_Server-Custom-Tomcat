package enumClass;

public enum UrlPath {
    ROOT("/"),
    INDEX("/index.html"),
    USER_LIST("/user/userList.html"),
    LOGIN_PAGE("/user/login.html"),
    LOGIN_FAILED("/user/login_failed.html"),
    LOGIN("/user/login"),
    SIGNUP("/user/signup");

    private final String path;

    UrlPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}